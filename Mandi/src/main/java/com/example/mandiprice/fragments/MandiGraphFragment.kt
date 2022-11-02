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
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mandiprice.R
import com.example.mandiprice.adapter.DateAdapter
import com.example.mandiprice.databinding.FragmentMandiGraphBinding
import com.example.mandiprice.viewModel.MandiViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.newsandarticles.adapter.BannerAdapter
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList


class MandiGraphFragment : Fragment() {
     lateinit var binding: FragmentMandiGraphBinding
     lateinit var listLine: ArrayList<Entry>
     lateinit var lineDataSet:LineDataSet
     lateinit var lineData: LineData
    private lateinit var shareLayout: ConstraintLayout
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    var bannerImageList: MutableList<AdBannerImage> = java.util.ArrayList()
    private var crop_master_id:Int? =null
    private var mandi_master_id:Int? =null
    private var crop_name:String? =null
    private var market_name:String? =null
    private var fragment:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            crop_master_id =it.getInt("cropId")
            mandi_master_id =it.getInt("mandiId")
            crop_name = it.getString("cropName")
            market_name = it.getString("market")

            fragment =it.getString("fragment")
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
        binding.tvMarket.text = "$market_name Market"
        shareLayout = binding.clShare
        binding.recycleViewDis.adapter = DateAdapter()
        viewModel.viewModelScope.launch {
            viewModel.getMandiHistoryDetails(crop_master_id,mandi_master_id).observe(viewLifecycleOwner){
                binding.viewModel = it.data
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



    }

    private fun onClick() {

            Log.d("navigation", "onClick: $fragment")
        binding.imgBack.setOnClickListener(){
            if(fragment=="one"){
            this.findNavController()
                .navigate(MandiGraphFragmentDirections.actionMandiGraphFragmentToMandiFragment())
        }else{
                this.findNavController()
                    .navigate(MandiGraphFragmentDirections.actionMandiGraphFragmentToSearchFragment())
            }}
        binding.imgShare.setOnClickListener(){
            screenShot()
        }
    }


    private fun graph() {
        viewModel.viewModelScope.launch {
        viewModel.getMandiHistoryDetails(crop_master_id,mandi_master_id).observe(viewLifecycleOwner){


        listLine = ArrayList()
//            for(i in it.data?.data!!.indices){
//        listLine.add(Entry(it.data!!.data[i]?.avgPrice!!.toFloat(), it.data!!.data[i].arrivalDate!!.toFloat()
//        ))
          //  }
//        listLine.add(Entry(20f,13f))
//        listLine.add(Entry(30f,11f))
//        listLine.add(Entry(40f,13f))
//        listLine.add(Entry(60f,12f))
        lineDataSet = LineDataSet(listLine,"")

        val labels = ArrayList<String>()
        labels.add("2015")
        labels.add("2016")
        labels.add("2017")
        labels.add("2018")
        lineData= LineData(lineDataSet)
        lineDataSet.color = resources.getColor(R.color.WoodBrown)
        binding.lineChart.data = lineData
        lineDataSet.setCircleColor(Color.WHITE)
        lineDataSet.circleHoleColor = resources.getColor(R.color.DarkGreen)
        lineDataSet.circleRadius = 6f
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER


        lineDataSet.setDrawFilled(true)
        binding.lineChart.setDrawGridBackground(false)
        binding.lineChart.setBorderColor(R.color.black)
        binding.lineChart.setBorderWidth(4f)
        binding.lineChart.axisRight.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawGridLines(false)
        //binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.description.isEnabled = false
        binding.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.lineChart.axisRight.isEnabled = false
        lineDataSet.fillDrawable = resources.getDrawable(R.drawable.bg_graph)
        binding.lineChart.xAxis.spaceMax = 2f
        binding.lineChart.fitScreen()
        binding.lineChart.isScaleXEnabled = false
        }}

    }
    private fun setBanners() {
        val adBannerImage =
            AdBannerImage("https://www.digitrac.in/pub/media/magefan_blog/Wheat_crop.jpg", "1", "0")
        bannerImageList.add(adBannerImage)
        val adBannerImage2 = AdBannerImage(
            "https://cdn.telanganatoday.com/wp-content/uploads/2020/10/Paddy.jpg",
            "2",
            "1"
        )
        bannerImageList.add(adBannerImage2)
        val bannerAdapter = BannerAdapter(requireContext(), bannerImageList)
        binding.bannerViewpager.adapter = bannerAdapter
        TabLayoutMediator(
            binding.bannerIndicators, binding.bannerViewpager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = "${position + 1} / ${bannerImageList.size}"
        }.attach()

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
        val URI = FileProvider.getUriForFile(requireContext(), "com.example.outgrow", imageFile)

        val i = Intent()
        i.action = Intent.ACTION_SEND
        //i.putExtra(Intent.EXTRA_TEXT,"Title")
        i.putExtra(Intent.EXTRA_STREAM, URI)
        i.type = "text/plain"
        startActivity(i)
    }
}