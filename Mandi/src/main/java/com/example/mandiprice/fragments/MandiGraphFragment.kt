package com.example.mandiprice.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.mandiprice.R
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
import com.waycool.data.Local.LocalSource
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.MandiDomainRecord
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.getDeepLinkAndScreenShot
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.adapter.AdsAdapter
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MandiGraphFragment : Fragment() {
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding
    lateinit var binding: FragmentMandiGraphBinding
    lateinit var listLine: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData
    private lateinit var shareLayout: ConstraintLayout
    private lateinit var mDateAdapter: DateAdapter
    private val viewModel: MandiViewModel by lazy {
        ViewModelProviders.of(this).get(MandiViewModel::class.java)
    }
    private var handler: Handler? = null
    private var runnable: Runnable?=null
    val moduleId="11"
    private var cropMasterId: Int? = null
    private var mandiMasterId: Int? = null
    private var cropName: String? = null
    private var marketName: String? = null

    private var sub_record_id:String? =null
    private var mandiDomain:MandiDomainRecord?=null

    private val inputDateFormatter: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    private val outputDateFormatter: SimpleDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            mandiDomain = it.getParcelable("mandiRecord")
//        }
        if (arguments != null) {
            if (arguments?.containsKey("mandiRecord") == true){
                mandiDomain = arguments?.getParcelable("mandiRecord")
            }

            else {
                cropName = arguments?.getString("cropName")
                mandiMasterId = arguments?.getInt("mandiId")
                cropMasterId = arguments?.getInt("cropId")
                marketName = arguments?.getString("market")
                sub_record_id=arguments?.getString("subRecordId")

            }
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMandiGraphBinding.inflate(inflater)
        apiErrorHandlingBinding = binding.errorState
        binding.cropName.text = cropName
        binding.tvMarket.text=marketName
       networkErrorStateTranslations(apiErrorHandlingBinding)

        binding.lifecycleOwner = this
        shareLayout = binding.shareCl2
        mDateAdapter = DateAdapter()
        binding.recycleViewDis.adapter = mDateAdapter

        if(mandiDomain!=null){
            cropMasterId = mandiDomain?.crop_master_id
            mandiMasterId = mandiDomain?.mandi_master_id
            cropName = mandiDomain?.crop
            marketName = mandiDomain?.market
            sub_record_id = mandiDomain?.sub_record_id
            checkLang()
        }
        else{
            binding.cropName.text=cropName
            binding.tvMarket.text=marketName

        }

        binding.imgShare.setOnClickListener {
            binding.clShareProgress.visibility=View.VISIBLE
            Log.d("MandiGraph", "MandiGraph: Share clicked")

            binding.imgShare.isEnabled = false
            val bundle=Bundle()
            bundle.putString("","$marketName")
            bundle.putString("","$cropName")
            EventItemClickHandling.calculateItemClickEvent("mandi_share",bundle)
            screenShot(cropMasterId, mandiMasterId, cropName, marketName, "one")
        }
        binding.recycleViewDis.adapter = DateAdapter()
        binding.recycleViewDis.isNestedScrollingEnabled = true
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            mandiGraphPageApi()
        }
        mandiGraphPageApi()


        binding.tvMarket.isSelected = true
        binding.cropName.isSelected = true
        EventClickHandling.calculateClickEvent("Mandi_graph_landing")
        return binding.root
    }

    private fun mandiGraphPageApi() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE

            AppUtils.translatedToastCheckInternet(context)

        } else {
            viewModel.viewModelScope.launch {
                viewModel.getMandiHistoryDetails(cropMasterId, mandiMasterId, sub_record_id)
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is Resource.Success -> {
                                binding.viewModel = it.data
                                binding.clInclude.visibility = View.GONE
                                apiErrorHandlingBinding.clInternetError.visibility = View.GONE
                                binding.recycleViewDis.adapter = mDateAdapter
                                mDateAdapter.submitList(it.data?.data)
                                graph()
                                setBanners()

                            }

                            is Resource.Loading -> {

                            }
                            is Resource.Error -> {

                            }
                        }
                    }
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.myLooper()!!)

        onClick()
        graph()
        setBanners()
        translation()
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@MandiGraphFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )


    }

    private fun onClick() {

        binding.imgBack.setOnClickListener {

                this.findNavController()
                    .navigateUp()
        }
    }


    private fun graph() {
        viewModel.viewModelScope.launch {
            viewModel.getMandiHistoryDetails(cropMasterId, mandiMasterId,sub_record_id)
                .observe(viewLifecycleOwner) { it ->
                    listLine = ArrayList()
                    if (it.data?.data != null) {
                        for (i in it.data?.data!!.indices) {
                            /**setting the api in graph*/
                            val xAxis: XAxis = binding.lineChart.xAxis
                            listLine.add(
                                Entry(
                                    i.toFloat(), it.data!!.data[i].avgPrice!!.toFloat()
                                )
                            )
                        }
                    }

                    lineDataSet = LineDataSet(listLine, "")
                    /** formating the date for graph*/
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
                    val xAxis2 = datesList?.toTypedArray()
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
                    lineDataSet.fillDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.bg_graph)
                    // binding.lineChart.xAxis.spaceMax = 1f
                    /** fitting graph with screen*/
                    binding.lineChart.fitScreen()
                    binding.lineChart.setScaleEnabled(false)
                    binding.lineChart.xAxis.setDrawGridLinesBehindData(false)
                    // binding.lineChart.axisLeft.isEnabled = false;
                    binding.lineChart.isScaleXEnabled = false
                    binding.lineChart.legend.isEnabled = false
                    binding.lineChart.xAxis.setCenterAxisLabels(false)
                    binding.lineChart.xAxis.granularity = 1f
                    /** giving space for top*/
                    binding.lineChart.viewPortHandler.offsetTop()
                    binding.lineChart.axisLeft.spaceTop = 150f
                    // binding.lineChart.setVisibleXRangeMaximum(3f);

                }
        }

    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity?:requireContext(), binding.bannerViewpager)
        runnable =Runnable {
            if ((bannerAdapter.itemCount - 1) == binding.bannerViewpager.currentItem)
                binding.bannerViewpager.currentItem = 0
            else
                binding.bannerViewpager.currentItem += 1
        }
        binding.bannerViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (runnable != null) {
                    AppUtil.handlerSet(handler!!,runnable!!,3000)
                }
            }
        })
        viewModel.getVansAdsList(moduleId).observe(viewLifecycleOwner) {

            bannerAdapter.submitList( it.data)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: TabLayout.Tab, position: Int ->
                tab.text = "${position + 1} / ${bannerAdapter.itemCount}"
            }.attach()
        }
        binding.bannerViewpager.adapter = bannerAdapter


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

    fun screenShot(
        crop_master_id: Int?,
        mandi_master_id: Int?,
        crop_name: String?,
        market_name: String?,
        fragment: String?
    ) {
        val uriString="http://app.outgrowdigital.com/mandigraph?crop_master_id=$crop_master_id&mandi_master_id=$mandi_master_id&sub_record_id=$sub_record_id&crop_name=$crop_name&market_name=$market_name&fragment=$fragment"
        val title="Outgrow - Mandi Detail for $crop_name"
        val description="Find Mandi details and more on Outgrow app"
        getDeepLinkAndScreenShot(context,shareLayout,uriString,title,description) { task, uri ->
            if (task.isSuccessful) {
                Log.d("MandiGraph", "MandiGraph: Share Success")
                binding.clShareProgress.visibility = View.GONE
                Handler().postDelayed({
                    binding.imgShare.isEnabled = true
                }, 1000)
                val shortLink: Uri? = task.result.shortLink
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                sendIntent.type = "text/plain"
                sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(sendIntent, "choose one"))
            }

        }

    }

    private fun translation() {
        TranslationsManager().loadString("str_share", binding.imgShare,"Share")
        TranslationsManager().loadString("date", binding.textView7,"Rate Kg")
        TranslationsManager().loadString("rate_kg", binding.tvKg,"Rate Kg")
        TranslationsManager().loadString("rate_kg", binding.textView8,"Date")
    }

    /**checking language translation for cropName and Market*/
    private fun checkLang(){
        viewModel.viewModelScope.launch {
            var langCode = LocalSource.getLanguageCode() ?: "en"
            when (langCode) {
                "en" -> {
                    binding.cropName.text = mandiDomain?.crop
                    binding.tvMarket.text = mandiDomain?.market
                }
                "hi" -> {
                    binding.cropName.text = mandiDomain?.crop_hi
                    binding.tvMarket.text = mandiDomain?.market_hi
                }
                "kn" -> {
                    binding.cropName.text = mandiDomain?.crop_kn
                    binding.tvMarket.text = mandiDomain?.market_kn
                }
                "te" -> {
                    binding.cropName.text = mandiDomain?.crop_te
                    binding.tvMarket.text = mandiDomain?.market_te
                }
                "ta" -> {
                    binding.cropName.text = mandiDomain?.crop_ta
                    binding.tvMarket.text = mandiDomain?.market_ta
                }
                "mr" -> {
                    binding.cropName.text = mandiDomain?.crop_mr
                    binding.tvMarket.text = mandiDomain?.market_mr

                }

            }
        }

    }
    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
    }
    override fun onResume() {
        super.onResume()
        if (runnable != null) {
            handler?.postDelayed(runnable!!, 3000)
        }
        EventScreenTimeHandling.calculateScreenTime("MandiGraphFragment")
    }
}