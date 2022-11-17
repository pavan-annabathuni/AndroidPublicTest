package com.waycool.cropprotect.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import com.google.android.material.tabs.TabLayoutMediator
import com.stfalcon.imageviewer.StfalconImageViewer
import com.stfalcon.imageviewer.loader.ImageLoader
import com.waycool.data.Network.NetworkModels.AdBannerImage
import com.waycool.data.repository.domainModels.PestDiseaseDomain
import com.waycool.data.utils.Resource
import com.waycool.featurecropprotect.Adapter.BannerAdapter
import com.waycool.featurecropprotect.Adapter.DiseasesChildAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.AudioNewLayoutBinding
import com.waycool.featurecropprotect.databinding.FragmentPestDiseaseDetailsBinding
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlin.math.roundToInt

class PestDiseaseDetailsFragment : Fragment() {

    private lateinit var binding: FragmentPestDiseaseDetailsBinding

    private val viewModel: CropProtectViewModel by lazy { ViewModelProvider(requireActivity())[CropProtectViewModel::class.java] }
    private val adapter: DiseasesChildAdapter by lazy { DiseasesChildAdapter() }

    //banners
    var bannerImageList: MutableList<AdBannerImage> = ArrayList()

    private var diseaseId: Int? = null
    private var diseaseName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPestDiseaseDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            diseaseId = it.getInt("diseaseid")
            diseaseName = it.getString("diseasename", "")
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbarTitle.text = diseaseName
        binding.cropProtectDiseaseName.text = diseaseName

        val audioNewLayoutBinding: AudioNewLayoutBinding =
            AudioNewLayoutBinding.inflate(layoutInflater)

        binding.subRecycler.adapter = adapter

        diseaseId?.let { diseaseId ->
            viewModel.getSelectedDisease(diseaseId).observe(requireActivity()) {
                when (it) {
                    is Resource.Success -> {

                        binding.toolbarTitle.text = it.data?.diseaseName
                        binding.cropProtectDiseaseName.text = it.data?.diseaseName

                        if (it.data?.imageUrl == null)
                            adapter.submitList(emptyList())
                        else adapter.submitList(it.data?.imageUrl)
                        activity?.let { act ->
                            Glide.with(act).load(it.data?.thumb)
                                .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                                .into(binding.cropProtectDiseaseImage)
                        }

                        binding.cropProtectDiseaseImage.setOnClickListener {view->
                            StfalconImageViewer.Builder<String>(binding.cropProtectDiseaseImage.context, listOf(it.data?.thumb) ,
                                ImageLoader { imageView: ImageView, image: String? ->
                                    Glide.with(binding.cropProtectDiseaseImage.context)
                                        .load(image)
                                        .into(imageView)
                                }).allowSwipeToDismiss(true)
                                .allowZooming(true)
                                .withTransitionFrom(binding.cropProtectDiseaseImage)
                                .show(true)
                        }

                        if (it.data?.audioUrl != null)
                            audioNewLayoutBinding.root.visibility = VISIBLE
                        else
                            audioNewLayoutBinding.root.visibility = GONE


                        if (it.data != null && it.data!!.symptoms != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                binding.symptomBodyTv.text = Html.fromHtml(
                                    it.data?.symptoms!!,
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                binding.symptomBodyTv.text =
                                    Html.fromHtml(it.data?.symptoms!!)
                            }
                        } else {
                            binding.symptomBodyTv.text = "NA"

                        }

                        binding.tabLayout.removeAllTabs()


                        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                populateTabText(tab, it.data)
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {
                            }

                            override fun onTabReselected(tab: TabLayout.Tab?) {
                                populateTabText(tab, it.data)
                            }
                        })

                        if (it.data != null && it.data!!.chemical != null) {
                            addTab("Chemical")
                        }

                        if (it.data != null && it.data!!.biological != null) {
                            addTab("Biological")
                        }

                        if (it.data != null && it.data!!.cultural != null) {
                            addTab("Cultural")
                        }


                    }
                    is Resource.Loading -> {
                        Toast.makeText(requireContext(), "Loading..", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Error: ${it.message}", Toast.LENGTH_SHORT)
                            .show()

                    }

                }
            }
        }

        setVideos()
        setNews()
        setBanners()
    }

    private fun setNews() {
        val newsBinding: GenericLayoutNewsListBinding = binding.layoutNews
        val adapter = NewsGenericAdapter()
        newsBinding.newsListRv.adapter = adapter
        viewModel.getVansNewsList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        newsBinding.viewAllNews.setOnClickListener {
            val intent=Intent(requireActivity(),NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("title", it?.title)
            bundle.putString("content", it?.desc)
            bundle.putString("image", it?.thumbnailUrl)
            bundle.putString("audio", it?.audioUrl)
            bundle.putString("date", it?.startDate)
            bundle.putString("source", it?.sourceName)

            findNavController().navigate(
                R.id.action_pestDiseaseDetailsFragment_to_newsFullviewActivity,
                bundle
            )
        }

    }

    private fun setVideos() {
        val videosBinding: GenericLayoutVideosListBinding = binding.layoutVideos
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        viewModel.getVansVideosList().observe(requireActivity()) {
            adapter.submitData(lifecycle, it)
        }

        videosBinding.viewAllVideos.setOnClickListener {
            val intent =Intent(requireActivity(),VideoActivity::class.java)
            startActivity(intent)
        }

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_pestDiseaseDetailsFragment_to_playVideoFragment3,
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


    private fun addTab(title: String) {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(title))
        if (binding.tabLayout.tabCount == 1) {
            binding.tabLayout.selectTab(binding.tabLayout.getTabAt(0))
        }
        Log.d("PestDisease", "TabCount:${binding.tabLayout.tabCount}")
    }

    private fun populateTabText(tab: Tab?, pestDisease: PestDiseaseDomain?) {
        when (tab?.text) {
            "Chemical" -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = Html.fromHtml(
                        pestDisease?.chemical!!,
                        Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        Html.fromHtml(pestDisease?.chemical!!)
                }
            }
            "Biological" -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = Html.fromHtml(
                        pestDisease?.biological!!,
                        Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        Html.fromHtml(pestDisease?.biological!!)
                }
            }
            "Cultural" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = Html.fromHtml(
                        pestDisease?.cultural!!,
                        Html.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        Html.fromHtml(pestDisease?.cultural!!)
                }
            }
        }

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

        val bannerAdapter = BannerAdapter(requireActivity())
        bannerAdapter.submitList(bannerImageList)
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


}