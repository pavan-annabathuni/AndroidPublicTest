package com.waycool.cropprotect.fragments

import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NavUtils
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout.*
import com.google.android.material.tabs.TabLayoutMediator
import com.stfalcon.imageviewer.StfalconImageViewer
import com.waycool.data.eventscreentime.EventClickHandling
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.PestDiseaseDomain
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.translations.TranslationsManager
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.NetworkUtil
import com.waycool.data.utils.Resource
import com.waycool.featurecropprotect.Adapter.DiseasesChildAdapter
import com.waycool.featurecropprotect.CropProtectViewModel
import com.waycool.featurecropprotect.R
import com.waycool.featurecropprotect.databinding.FragmentPestDiseaseDetailsBinding
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.getDeepLinkAndScreenShot
import com.waycool.newsandarticles.adapter.NewsGenericAdapter
import com.waycool.newsandarticles.adapter.onItemClick
import com.waycool.newsandarticles.databinding.GenericLayoutNewsListBinding
import com.waycool.newsandarticles.view.NewsAndArticlesActivity
import com.waycool.uicomponents.utils.AppUtil
import com.waycool.videos.VideoActivity
import com.waycool.videos.adapter.AdsAdapter
import com.waycool.videos.adapter.VideosGenericAdapter
import com.waycool.videos.databinding.GenericLayoutVideosListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.roundToInt


class PestDiseaseDetailsFragment : Fragment(), onItemClick {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private lateinit var newsBinding: GenericLayoutNewsListBinding
    private lateinit var videosBinding: GenericLayoutVideosListBinding

    //    private var audio: AudioWife? = null
   private var mediaPlayer: MediaPlayer = MediaPlayer()

    private lateinit var binding: FragmentPestDiseaseDetailsBinding
    private lateinit var shareLayout: ConstraintLayout

    private val viewModel: CropProtectViewModel by lazy { ViewModelProvider(this)[CropProtectViewModel::class.java] }
    private val adapter: DiseasesChildAdapter by lazy { DiseasesChildAdapter() }
    private var moduleId = "2"


    private var diseaseId: Int? = null
    private var cropId: Int? = null

    private var diseaseName: String? = null
    private var audioUrl: String? = null

    var chemical = "Chemical"
    var biological = "Biological"
    var cultural = "Cultural"

    private val AUDIO_PROGRESS_UPDATE_TIME: Long = 100

    private val mProgressUpdateHandler: Handler = Handler(Looper.myLooper()!!)

    private val mUpdateProgress: Runnable = object : Runnable {
        override fun run() {
            if (mProgressUpdateHandler != null && mediaPlayer.isPlaying) {
                binding.mediaSeekbar.progress = mediaPlayer.currentPosition
                val currentTime: Int = mediaPlayer.currentPosition
                updatePlaytime(currentTime)
                updateRuntime(currentTime)
                // repeat the process
                mProgressUpdateHandler.postDelayed(this, AUDIO_PROGRESS_UPDATE_TIME)
            } else {
                // DO NOT update UI if the player is paused
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPestDiseaseDetailsBinding.inflate(inflater)

        binding.toolbar.setOnClickListener {

            val isSuccess = findNavController().popBackStack()
            if (!isSuccess) activity?.let { it1 -> NavUtils.navigateUpFromSameTask(it1) }
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    val isSuccess = activity?.let { findNavController().popBackStack() }
                    if (!isSuccess!!) activity?.let { NavUtils.navigateUpFromSameTask(it) }
                }
            }
        activity?.let {
            activity?.onBackPressedDispatcher?.addCallback(
                it,
                callback
            )
        }
        binding.toolbarTitle.isSelected = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videosBinding = binding.layoutVideos
        newsBinding = binding.layoutNews
        handler = Handler(Looper.myLooper()!!)


        videosBinding.viewAllVideos.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_protect_video_viewall")
            val intent = Intent(activity, VideoActivity::class.java)
            startActivity(intent)
        }
        videosBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_protect_video_viewall")
            val intent = Intent(activity, VideoActivity::class.java)
            startActivity(intent)
        }
        newsBinding.viewAllNews.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_protect_NewsArticles_viewall")
            val intent = Intent(activity, NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }
        newsBinding.ivViewAll.setOnClickListener {
            EventClickHandling.calculateClickEvent("crop_protect_NewsArticles_viewall")
            val intent = Intent(activity, NewsAndArticlesActivity::class.java)
            startActivity(intent)
        }

        arguments?.let {
            cropId = it.getInt("cropId")
            diseaseId = it.getInt("diseaseid")
            diseaseName = it.getString("diseasename", "")
            audioUrl = it.getString("audioUrl")

        }


        if (audioUrl.isNullOrEmpty()) {
            binding.audioLayout.visibility = View.GONE
        } else {
            binding.audioLayout.visibility = View.VISIBLE
        }


        binding.toolbarTitle.text = diseaseName
        binding.cropProtectDiseaseName.text = diseaseName


        TranslationsManager().loadString("str_share", binding.imgShare, "Share")
        TranslationsManager().loadString("related_images", binding.cropProtectRelatedImageTv)
        TranslationsManager().loadString("symptoms", binding.symptomsTitle)
        TranslationsManager().loadString("control_measures", binding.controlMeasuresTitle)
        TranslationsManager().loadString("videos", videosBinding.videosTitle, "Videos")
        TranslationsManager().loadString("str_viewall", videosBinding.viewAllVideos, "View all")
        TranslationsManager().loadString("news_articles", newsBinding.newsTitle, "News &amp; Articles")
        TranslationsManager().loadString("str_viewall", newsBinding.viewAllNews, "View All")
        TranslationsManager().loadString("videos_not_available", videosBinding.tvNoVANs, "Videos are not available with us.")
        TranslationsManager().loadString("news_not_available", newsBinding.tvNoVANS, "News and Articles are not \navailable with us.")
        setVideos()
        setNews()
        setBanners()

//        handler?.postDelayed({
//            audioPlayer()
//            binding.clProgress.visibility = View.GONE
//            binding.constraintLayout2.visibility = View.GONE
//        }, 2000)



        shareLayout = binding.shareScreen
        binding.imgShare.setOnClickListener {
            binding.clShareProgress.visibility = View.VISIBLE
            binding.imgShare.isEnabled = false
            screenShot(diseaseId, diseaseName)
        }
        viewModel.viewModelScope.launch {
            chemical = TranslationsManager().getString("chemical")
            cultural = TranslationsManager().getString("str_cultural")
            biological = TranslationsManager().getString("str_biological")

            binding.subRecycler.adapter = adapter

            diseaseId?.let { diseaseId ->
                viewModel.getSelectedDisease(diseaseId).observe(viewLifecycleOwner) {
                    Log.d("pestAndDisease", "onViewCreated: ${it.data?.chemical} ")
                    when (it) {
                        is Resource.Success -> {
                            binding.toolbarTitle.text = it.data?.diseaseName
                            binding.cropProtectDiseaseName.text = it.data?.diseaseName
                            audioUrl = it.data?.audioUrl
                            audioUrl?.let { it1 -> initMediaPlayer(it1) }

                            if (it.data?.imageUrl == null)
                                adapter.submitList(emptyList())
                            else adapter.submitList(it.data?.imageUrl)
                            activity?.let { act ->
                                Glide.with(act).load(it.data?.thumb)
                                    .placeholder(com.waycool.uicomponents.R.drawable.outgrow_logo_new)
                                    .into(binding.cropProtectDiseaseImage)

                            }

                            binding.cropProtectDiseaseImage.setOnClickListener { _ ->
                                StfalconImageViewer.Builder<String>(
                                    binding.cropProtectDiseaseImage.context,
                                    listOf(it.data?.thumb)
                                ) { imageView: ImageView, image: String? ->
                                    Glide.with(binding.cropProtectDiseaseImage.context)
                                        .load(image)
                                        .into(imageView)
                                }.allowSwipeToDismiss(true)
                                    .allowZooming(true)
                                    .withTransitionFrom(binding.cropProtectDiseaseImage)
                                    .show(true)
                            }

                            if (it.data?.audioUrl != null)
                                binding.audioLayout.visibility = VISIBLE
                            else
                                binding.audioLayout.visibility = GONE

                            if (it.data != null && it.data!!.symptoms != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    Log.d("Symp", "${it.data?.symptoms}")
                                    binding.symptomBodyTv.text = HtmlCompat.fromHtml(
                                        it.data?.symptoms!!,
                                        HtmlCompat.FROM_HTML_MODE_COMPACT
                                    )
                                } else {
                                    binding.symptomBodyTv.text =
                                        HtmlCompat.fromHtml(
                                            it.data?.symptoms!!,
                                            HtmlCompat.FROM_HTML_MODE_COMPACT
                                        )
                                }
                            } else {
                                binding.symptomBodyTv.text = "NA"
                            }
                            binding.tabLayout.removeAllTabs()
                            binding.tabLayout.addOnTabSelectedListener(object :
                                OnTabSelectedListener {
                                override fun onTabSelected(tab: Tab?) {
                                    populateTabText(tab, it.data)
                                }

                                override fun onTabUnselected(tab: Tab?) {

                                }

                                override fun onTabReselected(tab: Tab?) {
                                    populateTabText(tab, it.data)
                                }
                            })
                            if (it.data != null && it.data!!.chemical != null) {
                                addTab(chemical)
                            }
                            if (it.data != null && it.data!!.biological != null) {
                                addTab(biological)
                            }
                            if (it.data != null && it.data!!.cultural != null) {
                                addTab(cultural)
                            }


                        }
                        is Resource.Loading -> {
                            AppUtils.translatedToastLoading(context)
                        }
                        is Resource.Error -> {}
                    }
                }
            }
        }

        binding.play.setOnClickListener {
            playerPlay()
            binding.play.visibility = View.INVISIBLE
            binding.pause.visibility = View.VISIBLE
        }

        binding.pause.setOnClickListener {
            playerPause()
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.INVISIBLE
        }

    }


//    private fun initializeMediaPlayer() {
//
//        audio = AudioWife.getInstance()
//            .init(activity, Uri.parse(audioUrl))
//            .setPlayView(binding.play)
//            .setPauseView(binding.pause)
//            .setSeekBar(binding.mediaSeekbar)
//            .setRuntimeView(binding.totalTime)
//
//    }

    private fun screenShot(diseaseId: Int?, diseaseName: String?) {
        val uriString="http://app.outgrowdigital.com/pestdiseasedetail?disease_id=$diseaseId&disease_name=${this.diseaseName}"
        val title="Outgrow - Pest Disease Detail for $diseaseName"
        val description="Find Pest Management and more on Outgrow app"
        getDeepLinkAndScreenShot(context,shareLayout,uriString,title,description) { task, uri ->
            if (task.isSuccessful) {
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

    private fun setNews() {
        val adapter = NewsGenericAdapter(context, this)
        newsBinding.newsListRv.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getVansNewsList(cropId, moduleId).collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    newsBinding.videoCardNoInternet.visibility = View.VISIBLE
                    newsBinding.noDataNews.visibility = View.GONE
                    newsBinding.newsListRv.visibility = View.INVISIBLE
                    newsBinding.viewAllNews.visibility = View.GONE
                    newsBinding.ivViewAll.visibility = View.GONE


                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error && adapter.itemCount == 0) {
                                    newsBinding.noDataNews.visibility = View.VISIBLE
                                    newsBinding.videoCardNoInternet.visibility = View.GONE
                                    newsBinding.tvNoVANS.text =
                                        "News and Articles are being loaded.Please wait for some time"
                                    newsBinding.newsListRv.visibility = View.INVISIBLE
                                    newsBinding.viewAllNews.visibility = View.GONE
                                    newsBinding.ivViewAll.visibility = View.GONE

                                }
                                if (it1 is LoadState.NotLoading) {

                                    if (adapter.itemCount == 0) {
                                        newsBinding.noDataNews.visibility = View.VISIBLE
                                        TranslationsManager().loadString(
                                            "news_not_available",
                                            newsBinding.tvNoVANS,
                                            "News and Articles are not \navailable with us."
                                        )
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.INVISIBLE
                                        newsBinding.viewAllNews.visibility = View.GONE
                                        newsBinding.ivViewAll.visibility = View.GONE


                                    } else {
                                        newsBinding.noDataNews.visibility = View.GONE
                                        newsBinding.videoCardNoInternet.visibility = View.GONE
                                        newsBinding.newsListRv.visibility = View.VISIBLE
                                        newsBinding.viewAllNews.visibility = View.VISIBLE
                                        newsBinding.ivViewAll.visibility = View.VISIBLE


                                    }
                                }
                            }
                    }
                }
            }
        }

    }

    private fun setVideos() {
        val adapter = VideosGenericAdapter()
        videosBinding.videosListRv.adapter = adapter
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getVansVideosList(cropId, moduleId).collect { pagingData ->
                adapter.submitData(lifecycle, pagingData)
                if (NetworkUtil.getConnectivityStatusString(context) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    videosBinding.videoCardNoInternet.visibility = View.VISIBLE
                    videosBinding.noDataVideo.visibility = View.GONE
                    videosBinding.videosListRv.visibility = View.INVISIBLE
                    videosBinding.viewAllVideos.visibility = View.GONE
                    videosBinding.ivViewAll.visibility = View.GONE

                } else {
                    lifecycleScope.launch(Dispatchers.Main) {
                        adapter.loadStateFlow.map { it.refresh }
                            .distinctUntilChanged()
                            .collect { it1 ->
                                if (it1 is LoadState.Error) {
                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.tvNoVANs.text =
                                            "Videos are being loaded.Please wait for some time"
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.ivViewAll.visibility = View.GONE
                                    }

                                }
                                if (it1 is LoadState.NotLoading) {
                                    if (adapter.itemCount == 0) {
                                        videosBinding.noDataVideo.visibility = View.VISIBLE
                                        TranslationsManager().loadString("videos_not_available", videosBinding.tvNoVANs, "Videos are not available with us.")
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.INVISIBLE
                                        videosBinding.viewAllVideos.visibility = View.GONE
                                        videosBinding.ivViewAll.visibility = View.GONE

                                    } else {
                                        videosBinding.noDataVideo.visibility = View.GONE
                                        videosBinding.videoCardNoInternet.visibility = View.GONE
                                        videosBinding.videosListRv.visibility = View.VISIBLE
                                        videosBinding.viewAllVideos.visibility = View.VISIBLE
                                        videosBinding.ivViewAll.visibility = View.VISIBLE


                                    }
                                }
                            }
                    }
                }


            }
        }


        adapter.onItemClick = {
            val eventBundle = Bundle()
            eventBundle.putString("title", it?.title)
            EventItemClickHandling.calculateItemClickEvent("PestDiseaseDetailVideo", eventBundle)
            val bundle = Bundle()
            bundle.putParcelable("video", it)
            findNavController().navigate(
                R.id.action_pestDiseaseDetailsFragment_to_playVideoFragment3,
                bundle
            )
        }
        videosBinding.videosScroll.setCustomThumbDrawable(com.waycool.uicomponents.R.drawable.slider_custom_thumb)

        videosBinding.videosListRv.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
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
    }

    private fun populateTabText(tab: Tab?, pestDisease: PestDiseaseDomain?) {
        when (tab?.text) {
            chemical -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = HtmlCompat.fromHtml(
                        pestDisease?.chemical!!,
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        HtmlCompat.fromHtml(
                            pestDisease?.chemical!!, HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                }
            }
            biological -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = HtmlCompat.fromHtml(
                        pestDisease?.biological!!,
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        HtmlCompat.fromHtml(
                            pestDisease?.biological!!,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                }
            }
            cultural -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.measuresTv.text = HtmlCompat.fromHtml(
                        pestDisease?.cultural!!,
                        HtmlCompat.FROM_HTML_MODE_COMPACT
                    )
                } else {
                    binding.measuresTv.text =
                        HtmlCompat.fromHtml(
                            pestDisease?.cultural!!,
                            HtmlCompat.FROM_HTML_MODE_COMPACT
                        )
                }
            }
        }

    }

    private fun setBanners() {

        val bannerAdapter = AdsAdapter(activity ?: requireContext(), binding.bannerViewpager)
        runnable = Runnable {
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
                    handler?.let { AppUtil.handlerSet(it, runnable!!, 3000) }
                }
            }
        })
        viewModel.getVansAdsList(moduleId).observe(viewLifecycleOwner) {

            bannerAdapter.submitList(it.data)
            TabLayoutMediator(
                binding.bannerIndicators, binding.bannerViewpager
            ) { tab: Tab, position: Int ->
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
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
        }
        binding.bannerViewpager.setPageTransformer(compositePageTransformer)

    }

//    private fun audioPlayer() {
//        if (audioUrl != null)
//            audio = AudioWife.getInstance()
//                .init(requireActivity(), Uri.parse(audioUrl))
//                .setPlayView(binding.play)
//                .setPauseView(binding.pause)
//                .setSeekBar(binding.mediaSeekbar)
//                .setRuntimeView(binding.totalTime)
//
////            if (audioUrl!=null && URLUtil.isValidUrl(audioUrl)){
////                audio?.play()
////                binding.audioProgress.visibility = INVISIBLE
////                binding.play.visibility = INVISIBLE
////            }
//
////            AudioWife.getInstance().addOnPlayClickListener {
////                if(audio==null){
////                    binding.audioProgress.visibility = VISIBLE
////                    binding.play.visibility = INVISIBLE
////                }
////            }
//
//        AudioWife.getInstance().addOnCompletionListener {
//            binding.mediaSeekbar.progress = 0
//            binding.pause.visibility = View.GONE
//            binding.play.visibility = View.VISIBLE
//        }
//
//
//        AudioWife.getInstance().addOnPlayClickListener {
//            binding.pause.visibility = View.VISIBLE
//            binding.play.visibility = View.GONE
//            audio?.play()
//        }
//
//        AudioWife.getInstance().addOnPauseClickListener {
//            binding.pause.visibility = View.GONE
//            binding.play.visibility = View.VISIBLE
//            audio?.pause()
//        }
//
//
////        binding.playPauseLayout.setOnClickListener {
////            Log.d("health", "onViewCreated: $audioUrl")
////            if (binding.play.isVisible) {
////                binding.pause.visibility = View.VISIBLE
////                binding.play.visibility = View.GONE
////                audioPlayer()
////
////            } else {
////                binding.pause.visibility = View.GONE
////                binding.play.visibility = View.VISIBLE
////                audio?.pause()
////            }
//////            audioPlayer()
////        }
//
////        if(mediaPlayer!!.isPlaying){
////            binding.pause.visibility = View.VISIBLE
////            binding.play.visibility = View.GONE
////        }else{
////            binding.play.visibility = View.GONE
////            binding.pause.visibility = View.VISIBLE
////
////        }
//
//    }

    private fun initMediaPlayer(audioUrl: String) {

        mediaPlayer = MediaPlayer()
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
        mediaPlayer.setDataSource(audioUrl)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            binding.mediaSeekbar.max = mediaPlayer.duration
            Log.d("pestdisease", "Duration: ${mediaPlayer.duration}")

        }

        binding.mediaSeekbar.progress = 0

        binding.mediaSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaPlayer.seekTo(seekBar.progress)

                // if the audio is paused and seekbar is moved,
                // update the play time in the UI.
                updateRuntime(seekBar.progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
        })

        mediaPlayer.setOnCompletionListener {
            binding.play.visibility = View.VISIBLE
            binding.pause.visibility = View.INVISIBLE
        }
    }

    private fun updateRuntime(currentTime: Int) {
        val playbackStr = StringBuilder()
        playbackStr.append(
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())
                )
            )
        )
        binding.totalTime.text = playbackStr
    }

    private fun updatePlaytime(currentTime: Int) {

        val playbackStr = java.lang.StringBuilder()
        playbackStr.append(
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(currentTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(currentTime.toLong())
                )
            )
        )
        playbackStr.append("/")

        // show total duration.
        var totalDuration: Long = 0
        if (mediaPlayer != null) {
            try {
                totalDuration = mediaPlayer.duration.toLong()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        // set total time as the audio is being played
        if (totalDuration != 0L) {
            playbackStr.append(
                String.format(
                    "%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(totalDuration), TimeUnit.MILLISECONDS.toSeconds(
                        totalDuration
                    ) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalDuration))
                )
            )
        } else {
            Log.w("pestdisease", "Something strage this audio track duration in zero")
        }
        binding.totalTime.text = playbackStr

        // DebugLog.i(currentTime + " / " + totalDuration);
    }


    private fun playerPlay() {


//        mediaPlayer.apply {
//            setAudioAttributes(
//                AudioAttributes.Builder()
//                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
//                    .setUsage(AudioAttributes.USAGE_MEDIA)
//                    .build()
//            )
//        }
        // on below line we are running a try
        // and catch block for our media player.
        try {
            // on below line we are setting audio
            // source as audio url on below line.
            mediaPlayer.start()

            mProgressUpdateHandler.postDelayed(mUpdateProgress, AUDIO_PROGRESS_UPDATE_TIME)

            // on below line we are
            // preparing our media player.

            // on below line we are
            // starting our media player.

        } catch (e: Exception) {

            // on below line we are handling our exception.
            e.printStackTrace()
        }
    }

    private fun playerPause() {
        if (mediaPlayer.isPlaying) {
            // if media player is playing we
            // are stopping it on below line.
            mediaPlayer.pause()
        }
    }

    private fun playerRelease() {
        mediaPlayer.reset()
        mediaPlayer.release()

    }

    override fun onPause() {
        super.onPause()
        playerPause()

        if (runnable != null) {
            handler?.removeCallbacks(runnable!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerRelease()
    }

    override fun onItemClickListener(vans: VansFeederListDomain?) {
        val bundleEvents = Bundle()
        bundleEvents.putString("", "${vans?.title}")
        EventItemClickHandling.calculateItemClickEvent("cropprotection_news", bundleEvents)
        val bundle = Bundle()
        bundle.putString("title", vans?.title)
        bundle.putString("content", vans?.desc)
        bundle.putString("image", vans?.thumbnailUrl)
        bundle.putString("audio", vans?.audioUrl)
        bundle.putString("date", vans?.startDate)
        bundle.putString("source", vans?.sourceName)
        bundle.putString("vansType", vans?.vansType)

        findNavController().navigate(
            R.id.action_pestDiseaseDetailsFragment_to_newsFullviewActivity,
            bundle
        )
    }


    override fun onResume() {
        super.onResume()

        if (runnable != null) {
            handler?.postDelayed(runnable!!, 3000)
        }
        EventScreenTimeHandling.calculateScreenTime("PestDiseaseDetailsFragment")

        audioUrl?.let {
            initMediaPlayer(it)
        }
        binding.clProgress.visibility = View.VISIBLE
        binding.constraintLayout2.visibility = View.VISIBLE
        binding.pause.visibility = View.GONE
        binding.play.visibility = View.VISIBLE
//        handler?.postDelayed({
//            audioPlayer()
        binding.clProgress.visibility = View.GONE
        binding.constraintLayout2.visibility = View.GONE
//        }, 1000)
    }
}