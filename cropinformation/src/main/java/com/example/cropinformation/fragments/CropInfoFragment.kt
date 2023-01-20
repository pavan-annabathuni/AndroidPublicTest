package com.example.cropinformation.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cropinformation.R
import com.example.cropinformation.adapter.ViewpagerAdapter
import com.example.cropinformation.databinding.FragmentCropInfoBinding
import com.example.cropinformation.viewModle.TabViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurechat.Contants.Companion.CALL_NUMBER
import com.waycool.featurechat.FeatureChat
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.adapter.onItemClick
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.newsandarticles.viewmodel.NewsAndArticlesViewModel
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class CropInfoFragment : Fragment(), onItemClick {
    private lateinit var videosBinding: GenericLayoutVideosListBinding
    private lateinit var newsBinding: GenericLayoutNewsListBinding
    private lateinit var binding: FragmentCropInfoBinding
    private val ViewModel: TabViewModel by lazy {
        ViewModelProviders.of(this).get(TabViewModel::class.java)
    }
    private val viewModel by lazy { ViewModelProvider(this)[NewsAndArticlesViewModel::class.java] }

    private var cropId: Int? = null
    private var cropName: String? = null
    private var cropLogo: String? = null
    private var size: Int = 0
    private var module_id = "1"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cropId = it.getInt("cropid")
            cropName = it.getString("cropname")
            cropLogo = it.getString("cropLogo")

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCropInfoBinding.inflate(inflater)

        setBanners()



        ViewModel.getTabItem()
        binding.lifecycleOwner = this
        binding.viewModel = ViewModel

//        recycleViewIndicator()

//        binding.ViewPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         newsBinding = binding.layoutNews
         videosBinding = binding.layoutVideos

        binding.topName.text = cropName
        binding.back.setOnClickListener() {
            findNavController().popBackStack()
        }
        newsBinding.viewAllNews.setOnClickListener {
            EventClickHandling.calculateClickEvent("news_view_all_CropInfo")
            val intent = Intent(requireActivity(), NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        newsBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("news_view_all_CropInfo")
            val intent = Intent(requireActivity(), NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        videosBinding.viewAllVideos.setOnClickListener {
            EventClickHandling.calculateClickEvent("video_view_all_CropInfo")
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }
        videosBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("video_view_all_CropInfo")
            val intent = Intent(requireActivity(), VideoActivity::class.java)
            startActivity(intent)
        }

        setTabs()
        Glide.with(requireContext()).load(cropLogo).into(binding.cropLogo)
        tabCount()
        ViewModel.cropAdvisory()
        binding.ViewPager.setPageTransformer { page, position ->
            updatePagerHeightForChild(page, binding.ViewPager)
        }
        setVideos()
        setNews()
        fabButton()

        ViewModel.downloadCropInfo()
        translation()
    }


    private fun setTabs() {

        ViewModel.getCropInformationDetails(cropId!!).observe(viewLifecycleOwner) { it ->
            val data = it.data!!
            size = it.data!!.size

            //data.sortedBy { it.id }
            binding.ViewPager.adapter = ViewpagerAdapter(this, it.data, data.size, cropId!!)
            binding.tvTotalItem.text = "/${data.size}"




            TabLayoutMediator(binding.tabLayout, binding.ViewPager) { tab, position ->
                val customView = tab.setCustomView(R.layout.item_tab_crop)

                when (data[position].labelNameTag ?: data[position].label_name) {

                    "Crop Variety" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_crop_variety)
                        customView
                    }
                    "Soil Type Others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView
                    }
                    "Soil pH" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_soilph_image)
                        customView
                        // tab.text = "Soil Ph"
                    }
                    "Soil Type" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_soil_type_img)
                        customView
                    }
                    "Sowing Season" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_sowing_planting_img)
                        customView
                    }
                    "Crop Duration" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_crop_duration_img)
                        customView

                    }
                    "Days to first harvest" -> {
                        //  binding.tabLayout.getTabAt(position)?.setCustomView(R.layout.emptytab)
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_harves_img)
                        customView
                    }
                    "Seed Rate (Pit Sowing)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_seed_pit)
                        customView

                    }
                    "Planting Material" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_planting_material)
                        customView

                    }
                    "Planting Material others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView

                    }
                    "Nursery Practices" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_nursery_period_img)
                        customView
                    }
                    "Seed Rate (Line Sowing)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_seed_line)
                        customView

                    }
                    "Seed Rate (Broadcast)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_seed_broadcast)
                        customView

                    }
                    "Seed Treatment" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_seed_treatment_img)
                        customView

                    }
                    "Sowing Depth(cm)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_sowing_depth_img)
                        customView

                    }
                    "Spacing between Row to Row" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_spacing)
                        customView

                    }
                    "Spacing between Plant to Plant" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_spacing)
                        customView
                    }
                    "Field preparation" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_field_preparation_img)
                        customView

                    }
                    "Mulching" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_mulching_image)
                        customView


                    }
                    "Staking" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_staking)
                        customView
                    }
                    "Distance between stakes" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_staking)
                        customView
                    }
                    "Irrigation Type" -> {
                        tab.setText(data[position].label_name)
                        tab.setIcon(R.drawable.ci_irrigation_img)
                        customView
                    }
                    "Flooding" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.img_flooding)
                        customView
                    }
                    "Rain Gun" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_rain_gun)
                        customView
                    }
                    "Drip" -> {
                        //tab.tabLabelVisibility = TabLayout.TAB_LABEL_VISIBILITY_UNLABELED
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_drip)
                        customView
                    }

                    "Fertilizers" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_fertiliz_image)
                        customView


                    }
                    "Micronutrients" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_micro_img)
                        customView


                    }
                    "Border crop" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_border_image)
                        customView


                    }
                    "Intercrop" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_intercrop_image)
                        customView
                    }
                    "Weed control(cultural)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_weed_image)
                        customView
                    }
                    "Weed control(chemical)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_weed_chemical_img)
                        customView
                    }
                    "Harvest (sowing, planting, transplantation)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_harves_img)
                        customView
                    }
                    "Sowing/Planting-Yield" -> {
                        tab.text = data[position].label_name
                        //tab.setIcon(R.drawable.ci_sowing_planting_img)
                        customView
                    }
                    "Yield ( kg or tons/ac)" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_yield_image)
                        customView
                    }
                    "Yield-Harvest" -> {
                        tab.text = data[position].label_name
                        //   tab.setIcon(R.drawable.ci_yield_image)
                        customView
                    }
                    "Post Harvesting" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_post_har_img)
                        customView
                    }
                    "Proposed Next Crops" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_pro_nextcrop_img)
                        customView
                    }
                    "Sowing/Planting" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ic_sowing_planting_img)
                        customView
                    }
                    "Training and Pruning" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_trimming_image)
                        customView
                    }
                    "Ratooning" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.ci_ratooning_image)
                        customView
                    }
                    "Planting Material Others" -> {
                        tab.text = data[position].label_name
                        tab.setIcon(R.drawable.planting_others)
                        customView
                    }
                    else -> {
                        //  tab.tabLabelVisibility = TabLayout.TAB_LABEL_VISIBILITY_UNLABELED


                    }
                }
            }.attach()
        }
    }

    private fun tabCount() {
        var myPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tvCount.text = (position + 1).toString()
                if (position < (size - 1)) {
                    binding.imgNext.visibility = View.VISIBLE
                    binding.imgNext.setOnClickListener() {
                        binding.ViewPager.currentItem = position + 1
                    }
                } else {
                    binding.imgNext.visibility = View.GONE
                }
                if (position == 0) {
                    binding.imgPrev.visibility = View.GONE
                } else {
                    binding.imgPrev.visibility = View.VISIBLE
                    binding.imgPrev.setOnClickListener {
                        binding.ViewPager.currentItem = position - 1
                    }
                }

            }
        }
        binding.ViewPager.registerOnPageChangeCallback(myPageChangeCallback)
    }

    //Zendesk Chat and Calling Function
    private fun fabButton() {
        var isVisible = false
        binding.addFab.setOnClickListener() {
            if (!isVisible) {
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_cross))
                binding.addChat.show()
                binding.addCall.show()
                binding.addFab.isExpanded = true
                isVisible = true
            } else {
                binding.addChat.hide()
                binding.addCall.hide()
                binding.addFab.setImageDrawable(ContextCompat.getDrawable(requireContext(),com.waycool.uicomponents.R.drawable.ic_chat_call))
                binding.addFab.isExpanded = false
                isVisible = false
            }
        }
        binding.addCall.setOnClickListener() {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse(CALL_NUMBER)
            startActivity(intent)
        }
        binding.addChat.setOnClickListener() {
            FeatureChat.zenDeskInit(requireContext())
        }
    }


    private fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
        view.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)
            pager.layoutParams = (pager.layoutParams).also { lp -> lp.height = view.measuredHeight }
            pager.invalidate()
        }
    }

    private fun setNews() {
        val adapter = NewsGenericAdapter(context,this)
        newsBinding.newsListRv.adapter = adapter
        lifecycleScope.launch((Dispatchers.Main)) {
            ViewModel.getVansNewsList(cropId,module_id).collect {
                adapter.submitData(lifecycle, it)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    newsBinding.videoCardNoInternet.visibility = View.VISIBLE
                    newsBinding.noDataNews.visibility = View.GONE
                    newsBinding.newsListRv.visibility = View.INVISIBLE
                    newsBinding.viewAllNews.visibility=View.GONE
                    newsBinding.ivViewAll.visibility=View.GONE


                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    newsBinding.noDataNews.visibility = View.VISIBLE
                                    newsBinding.videoCardNoInternet.visibility = View.GONE
                                    newsBinding.newsListRv.visibility = View.INVISIBLE
                                    newsBinding.viewAllNews.visibility=View.GONE
                                    newsBinding.ivViewAll.visibility=View.GONE

                                }
                                if (it1 is LoadState.Error ) {
                                    if(adapter.itemCount == 0) {
                                        newsBinding.noDataNews.visibility = View.VISIBLE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.INVISIBLE
                                        newsBinding.viewAllNews.visibility = View.GONE
                                        newsBinding.ivViewAll.visibility = View.GONE
                                    }

                                }

                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        newsBinding.noDataNews.visibility = View.VISIBLE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.INVISIBLE
                                        newsBinding.viewAllNews.visibility=View.GONE
                                        newsBinding.ivViewAll.visibility=View.GONE


                                    } else {
                                        newsBinding.noDataNews.visibility = View.GONE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.VISIBLE
                                        newsBinding.viewAllNews.visibility=View.VISIBLE
                                        newsBinding.ivViewAll.visibility=View.VISIBLE



                                    }
                                }
                            }
                    }
                }
            }



        }

        /*    ViewModel.getVansNewsList(cropId,module_id).observe(requireActivity()) {
                adapter.submitData(lifecycle, it)

                *//*   if (adapter.snapshot().size==0){
                   newsBinding.noDataNews.visibility=View.VISIBLE
               }
               else{
                   newsBinding.noDataNews.visibility=View.GONE
                   adapter.submitData(lifecycle, it)
               }*//*

        }*/

    }

    private fun setVideos() {
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            ViewModel.getVansVideosList(cropId.toString(),module_id).collect {
                adapter.submitData(lifecycle, it)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                    videosBinding.viewAllVideos.visibility=View.GONE
                    videosBinding.ivViewAll.visibility=View.GONE


                }
                else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    videosBinding.noDataVideo.visibility = View.VISIBLE
                                    videosBinding.videoCardNoInternet.visibility = View.GONE
                                    videosBinding.videosListRv.visibility = View.INVISIBLE
                                    videosBinding.viewAllVideos.visibility=View.GONE
                                    videosBinding.ivViewAll.visibility=View.GONE

                                }
                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    videosBinding.noDataVideo.visibility = View.VISIBLE
                                    videosBinding.videoCardNoInternet.visibility = View.GONE
                                    videosBinding.videosListRv.visibility = View.INVISIBLE
                                    videosBinding.viewAllVideos.visibility=View.GONE
                                    videosBinding.ivViewAll.visibility=View.GONE

                                }

                                if (it1 is LoadState.NotLoading) {
                                    Log.d("HomePage", "Adapter Size: ${adapter.itemCount}")

                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                        videosBinding.viewAllVideos.visibility=View.GONE
                                        videosBinding.ivViewAll.visibility=View.GONE

                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility=View.VISIBLE
                                        videosBinding.ivViewAll.visibility=View.VISIBLE



                                    }
                                }
                            }
                    }


                }

            }
        }





        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_cropInfoFragment_to_playVideoFragment2,
                bundle
            )
        }
        videosBinding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        videosBinding.videosListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                videosBinding.videosScroll.value =
                    calculateScrollPercentage(videosBinding).toFloat()
            }
        })
    }

    fun calculateScrollPercentage(videosBinding: GenericLayoutVideosListBinding): Int {
        val offset: Int = videosBinding.videosListRv.computeHorizontalScrollOffset()
        val extent: Int = videosBinding.videosListRv.computeHorizontalScrollExtent()
        val range: Int = videosBinding.videosListRv.computeHorizontalScrollRange()
        val scroll = 100.0f * offset / (range - extent).toFloat()
        if (scroll.isNaN())
            return 0
        return scroll.roundToInt()
    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity?:requireContext())
        ViewModel.getVansAdsList().observe(viewLifecycleOwner) {

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

    private fun translation(){
        TranslationsManager().loadString("str_title",binding.textView2,"Crop information")
        TranslationsManager().loadString("videos", videosBinding.videosTitle,"Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos,"View all")
        TranslationsManager().loadString("news_articles", newsBinding.newsTitle,"News & Article")
        TranslationsManager().loadString("str_viewall", newsBinding.viewAllNews,"View all")
      //  TranslationsManager().loadString("str_video")
    }

    override fun onItemClickListener(it: VansFeederListDomain?) {
        EventClickHandling.calculateClickEvent(it?.title.toString())
        val bundle = Bundle()
        bundle.putString("title", it?.title)
        bundle.putString("content", it?.desc)
        bundle.putString("image", it?.thumbnailUrl)
        bundle.putString("audio", it?.audioUrl)
        bundle.putString("date", it?.startDate)
        bundle.putString("source", it?.sourceName)

        findNavController().navigate(
            R.id.action_cropInfoFragment_to_newsFullviewActivity,
            bundle
        )
    }

}