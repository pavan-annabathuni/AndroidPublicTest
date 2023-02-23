package com.waycool.videos.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.waycool.core.utils.AppSecrets
import com.waycool.data.eventscreentime.EventItemClickHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.AppUtils
import com.waycool.data.utils.AppUtils.networkErrorStateTranslations
import com.waycool.data.utils.NetworkUtil
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.DOMAIN_URI_PREFIX
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.uicomponents.utils.Constants
import com.waycool.videos.R
import com.waycool.videos.VideoViewModel
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.adapter.itemClick
import com.waycool.videos.databinding.FragmentPlayVideoBinding

@Suppress("DEPRECATION")
class PlayVideoFragment : Fragment(), itemClick {
    private var selectedCategory: String?=null
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private lateinit var binding: FragmentPlayVideoBinding
    private var videoSelected: VansFeederListDomain? = null
    private var videoTitle: String? = null
    private var videoDesc: String? = null
    private var videoUrl: String? = null
    private var isFullScreen: Boolean? = null


    private val videoViewModel: VideoViewModel by lazy { ViewModelProvider(this)[VideoViewModel::class.java] }
    private lateinit var adapterVideo: VideosPagerAdapter
    private var player: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayVideoBinding.inflate(layoutInflater)
        apiErrorHandlingBinding = binding.errorState
       networkErrorStateTranslations(apiErrorHandlingBinding)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            if (arguments?.containsKey("video") == true){
                videoSelected = requireArguments().getParcelable("video")
                selectedCategory=requireArguments().getString("selectedCategory")
            }
            else {
                videoTitle = arguments?.getString("title")
                videoDesc = arguments?.getString("description")
                videoUrl = arguments?.getString("url")
            }
        }

        if (videoSelected != null) {
            binding.ytTitleTv.text = videoSelected?.title
            binding.ytDescriptionTv.text = videoSelected?.desc
        } else {
            binding.ytTitleTv.text = videoTitle
            binding.ytDescriptionTv.text = videoDesc
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isFullScreen == true) {
                        player?.setFullscreen(false)
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        activity?.onBackPressedDispatcher?.addCallback(
            requireActivity(),
            callback
        )
        Handler(Looper.myLooper()!!).postDelayed({
            getVideos()
        }, 600)
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }

        val frag =
            childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment

        Handler(Looper.myLooper()!!).postDelayed({
            frag.initialize(
                AppSecrets.getYoutubeKey(),
                object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(
                        p0: YouTubePlayer.Provider?,
                        youTubePlayer: YouTubePlayer?,
                        p2: Boolean
                    ) {
                        if (!p2) {
                            player = youTubePlayer

//                            player?.fullscreenControlFlags =
//                                YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION

                            player?.setOnFullscreenListener {
                                isFullScreen = it
                            }
                            if (videoSelected != null)
                                youTubePlayer?.loadVideo(videoSelected?.contentUrl)
                            else youTubePlayer?.loadVideo(videoUrl)
                            youTubePlayer?.play()
                            setupPlayerEvents()
                        }
                    }

                    override fun onInitializationFailure(
                        p0: YouTubePlayer.Provider?,
                        p1: YouTubeInitializationResult?
                    ) {

                    }
                })
        }, 200)


    }

    //    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // Pause the video
//            player?.pause()
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            // Resume playing the video
//            player?.play()
//        }
//    }


    private fun setupPlayerEvents() {
        player?.setPlaybackEventListener(object : YouTubePlayer.PlaybackEventListener {
            override fun onPlaying() {
                binding.pauseIv.setImageResource(R.drawable.ic_baseline_pause_24)
            }

            override fun onPaused() {
                binding.pauseIv.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }

            override fun onStopped() {
                binding.pauseIv.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            }

            override fun onBuffering(p0: Boolean) {
            }

            override fun onSeekTo(p0: Int) {
            }


        })
    }

    private fun getVideos(
        tags: String? = null,
        categoryId: Int? = null
    ) {
        adapterVideo = VideosPagerAdapter(context, this)
        binding.ytBottomsheetRv.adapter = adapterVideo
        videoViewModel.getVansVideosList(tags, categoryId).observe(viewLifecycleOwner) {
            adapterVideo.submitData(lifecycle, it)
        }
    }

    override fun onItemClick(van: VansFeederListDomain?) {
        val eventBundle=Bundle()
        eventBundle.putString("VideoTitle",van?.title)
        if(selectedCategory!=null){
            eventBundle.putString("selectedCategory","video_$selectedCategory")
        }
        EventItemClickHandling.calculateItemClickEvent("video_landing",eventBundle)
        if (player != null) {
            player!!.loadVideo(van?.contentUrl)
            player!!.play()
            binding.ytTitleTv.text = van?.title
            binding.ytDescriptionTv.text = van?.desc


        }
    }


    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            AppUtils.translatedToastCheckInternet(context)

        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            getVideos()

        }

    }

    override fun onShareItemClick(it: VansFeederListDomain?, view: View?) {
        binding.clShareProgress.visibility=View.VISIBLE
        val thumbnail = if(!it?.thumbnailUrl.isNullOrEmpty()){
            it?.thumbnailUrl
        } else{
            DeepLinkNavigator.DEFAULT_IMAGE_URL
        }
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://app.outgrowdigital.com/videoshare?video_id=${it?.id}&video_name=${it?.title}&video_desc=${it?.desc}&content_url=${it?.contentUrl}"))
            .setDomainUriPrefix(DOMAIN_URI_PREFIX)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse(Constants.PLAY_STORE_LINK))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse(thumbnail))
                    .setTitle("Outgrow - Hi, Checkout the video  ${it?.title}.")
                    .setDescription("Watch more videos and learn with Outgrow")
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.clShareProgress.visibility=View.GONE
                    Handler().postDelayed({view?.isEnabled = true
                    },1000)
                    val shortLink: Uri? = task.result.shortLink
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
                    sendIntent.type = "text/plain"
                    startActivity(Intent.createChooser(sendIntent, "choose one"))
                }

            }

    }
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("PlayVideoFragment")
    }

}