package com.example.mandiprice.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.mandiprice.R
import com.example.mandiprice.adapter.AdsAdapter
import com.example.mandiprice.adapter.DateAdapter
import com.example.mandiprice.databinding.FragmentMandiGraphBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.newsandarticles.adapter.BannerAdapter
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import java.io.File
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MandiGraphFragment : Fragment() {
    lateinit var binding: FragmentMandiGraphBinding
    lateinit var listLine: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData
    private lateinit var shareLayout: ConstraintLayout
    private lateinit var mDateAdapter: DateAdapter
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    private var crop_master_id: Int? = null
    private var mandi_master_id: Int? = null
    private var crop_name: String? = null
    private var market_name: String? = null
    private var fragment: String? = null

    private val inputDateFormatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    private val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            crop_master_id = it.getInt("cropId")
            mandi_master_id = it.getInt("mandiId")
            crop_name = it.getString("cropName")
            market_name = it.getString("market")

            fragment = it.getString("fragment")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMandiGraphBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.cropName.text = crop_name
        binding.tvMarket.text = market_name
        shareLayout = binding.shareCl2
        mDateAdapter = DateAdapter()
        binding.recycleViewDis.adapter = mDateAdapter
        viewModel.viewModelScope.launch {
            viewModel.getMandiHistoryDetails(crop_master_id,mandi_master_id).observe(viewLifecycleOwner) { it2 ->
//                 val data2 = (it2.data?.data?.map { data ->
//                         data.arrivalDate
//                     } ?: emptyList()).toMutableList()
//                data2.sort()

               mDateAdapter.submitList(it2.data?.data)
                //     Toast.makeText(context,"${it.data}",Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onClick()
        graph()
        setBanners()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@MandiGraphFragment.findNavController().
                    navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )


    }

    private fun onClick() {

        Log.d("navigation", "onClick: $fragment")
        binding.imgBack.setOnClickListener() {
            if (fragment == "one") {
                this.findNavController()
                    .navigateUp()
            } else {
                this.findNavController()
                    .popBackStack()
            }
        }

            binding.imgShare.setOnClickListener() {
                screenShot()
                Log.d("toast", "onClick: Working")
            }
    }


    private fun graph() {
        viewModel.viewModelScope.launch {
            viewModel.getMandiHistoryDetails(crop_master_id,mandi_master_id).observe(viewLifecycleOwner) { it ->


                listLine = ArrayList()
                if (it.data?.data != null) {
                    for (i in it.data?.data!!.indices) {
//                val inputDate:SimpleDateFormat = SimpleDateFormat(it.data!!.data[0].arrivalDate)
//                val outputDate:SimpleDateFormat = SimpleDateFormat("yyyy-MM-ddThh:mm:ssZ")
//                val date:Date = inputDate.parse(it.data!!.data[0].arrivalDate)
//                val formateDate = outputDate.format(date)
//                Log.d("DATE", "graph: $formateDate ")
                        val xAxis: XAxis = binding.lineChart.getXAxis()
                        listLine.add(
                            Entry(
                                i.toFloat(),it.data!!.data[i].avgPrice!!.toFloat()
                            )
                        )
                    }
                }
//        listLine.add(Entry(20f,13f))
//        listLine.add(Entry(30f,11f))
//        listLine.add(Entry(40f,13f))
//        listLine.add(Entry(60f,12f))

                lineDataSet = LineDataSet(listLine, "")
                    val datesList = it.data?.data?.map { mandi ->
                        try {
                            val date: Date = inputDateFormatter.parse(mandi.arrivalDate)
                            outputDateFormatter.format(date)
                        } catch (e: ParseException) {
                            Log.d("Mandi Graphs", e.message.toString())
                            ""
                        }
                    }

                val valueFormatter2 = IndexAxisValueFormatter()

                var xAxis2 = datesList?.toTypedArray()
                valueFormatter2.values = xAxis2
                binding.lineChart.xAxis.valueFormatter = valueFormatter2
                binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//                binding.lineChart.xAxis.labelRotationAngle = -45f

                binding.lineChart.axisLeft.axisMinimum = 0f

                lineData = LineData(lineDataSet)
                lineDataSet.color = resources.getColor(R.color.WoodBrown)
                binding.lineChart.data = lineData
                lineDataSet.setCircleColor(Color.WHITE)
                lineDataSet.circleHoleColor = resources.getColor(R.color.DarkGreen)
                lineDataSet.circleRadius = 6f
                lineDataSet.mode = LineDataSet.Mode.LINEAR


                lineDataSet.setDrawFilled(true)
                binding.lineChart.setDrawGridBackground(false)
                binding.lineChart.setDrawBorders(true)
                binding.lineChart.setBorderColor(R.color.LightGray)
                binding.lineChart.setBorderWidth(2f)
                binding.lineChart.axisRight.setDrawGridLines(false)
                binding.lineChart.axisLeft.setDrawGridLines(false)
                //binding.lineChart.xAxis.setDrawGridLines(false)
                binding.lineChart.description.isEnabled = false
                binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                binding.lineChart.axisRight.isEnabled = false
                lineDataSet.fillDrawable = resources.getDrawable(R.drawable.bg_graph)
               // binding.lineChart.xAxis.spaceMax = 1f
                binding.lineChart.fitScreen()
                binding.lineChart.setScaleEnabled(false)
                binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                // binding.lineChart.axisLeft.isEnabled = false;
                binding.lineChart.isScaleXEnabled = false
                binding.lineChart.getLegend().setEnabled(false);
               binding.lineChart.xAxis.setCenterAxisLabels(false);
                binding.lineChart.xAxis.setGranularity(1f);
               // binding.lineChart.setVisibleXRangeMaximum(3f);

            }
        }

    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter()
        viewModel.getVansAdsList().observe(viewLifecycleOwner) {

            bannerAdapter.submitData(lifecycle, it)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.snapshot().size}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter
//        TabLayoutMediator(
//            binding.bannerIndicators, binding.bannerViewpager
//        ) { tab: TabLayout.Tab, position: Int ->
//            tab.text = "${position + 1} / ${bannerImageList.size}"
//        }.attach()

        binding.bannerViewpager.clipToPadding = false
        binding.bannerViewpager.clipChildren = false
        binding.bannerViewpager.offscreenPageLimit = 3
        binding.bannerViewpager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - Math.abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)
    }

    fun screenShot() {
        val now = Date()
        android.text.format.DateFormat.format("", now)
        val path = context?.getExternalFilesDir(null)?.absolutePath + "/" + now + ".jpg"
        val bitmap =
            Bitmap.createBitmap(shareLayout.width, shareLayout.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        shareLayout.draw(canvas)
        val imageFile = File(path)
        val outputFile = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputFile)
        outputFile.flush()
        outputFile.close()
        val URI = com.example.mandiprice.FileProvider.getUriForFile(requireContext(), "com.example.outgrow", imageFile)

        val i = Intent()
        i.action = Intent.ACTION_SEND
        //i.putExtra(Intent.EXTRA_TEXT,"Title")
        i.putExtra(Intent.EXTRA_STREAM, URI)
        i.type = "text/plain"
        startActivity(i)
    }
}