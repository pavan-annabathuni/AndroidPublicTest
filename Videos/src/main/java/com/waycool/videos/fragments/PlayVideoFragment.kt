package com.waycool.videos.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.R
import com.waycool.videos.VideoViewModel
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.adapter.itemClick
import com.waycool.videos.databinding.FragmentPlayVideoBinding

@Suppress("DEPRECATION")
class PlayVideoFragment : Fragment() ,itemClick{
    private lateinit var apiErrorHandlingBinding: ApiErrorHandlingBinding

    private lateinit var binding: FragmentPlayVideoBinding
    private var videoSelected: VansFeederListDomain? = null
    private var videoTitle:String?=null
    private var videoDesc:String?=null
    private var videoUrl:String?=null


    private val videoViewModel: VideoViewModel by lazy { ViewModelProvider(this)[VideoViewModel::class.java] }
    private lateinit var adapterVideo: VideosPagerAdapter
    private var player: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayVideoBinding.inflate(layoutInflater)
        apiErrorHandlingBinding=binding.errorState




        return binding.root
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            context?.let {
                ToastStateHandling.toastError(
                    it,
                    "Please check your internet connectivity",
                    Toast.LENGTH_SHORT
                )
            }
        } else {
            binding.clInclude.visibility = View.GONE
            apiErrorHandlingBinding.clInternetError.visibility = View.GONE
            getVideos()

        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            if (arguments?.containsKey("video") == true)
                videoSelected = requireArguments().getParcelable("video")
            else {
                videoTitle=arguments?.getString("title")
                videoDesc=arguments?.getString("description")
                videoUrl=arguments?.getString("url")
            }
        }

        if(videoSelected!=null) {
            binding.ytTitleTv.text = videoSelected?.title
            binding.ytDescriptionTv.text = videoSelected?.desc
        }else{
            binding.ytTitleTv.text = videoTitle
            binding.ytDescriptionTv.text =videoDesc
        }

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
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

        val frag = childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment

        Handler(Looper.myLooper()!!).postDelayed({
            frag.initialize(AppSecrets.getYoutubeKey(), object : YouTubePlayer.OnInitializedListener {
                    override fun onInitializationSuccess(
                        p0: YouTubePlayer.Provider?,
                        youTubePlayer: YouTubePlayer?,
                        p2: Boolean
                    ) {
                        if (!p2) {
                            player = youTubePlayer
                            if(videoSelected!=null)
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
        adapterVideo = VideosPagerAdapter(context,this)
        binding.ytBottomsheetRv.adapter = adapterVideo
        videoViewModel.getVansVideosList(tags, categoryId).observe(viewLifecycleOwner) {
            adapterVideo.submitData(lifecycle, it)
        }
    }

    override fun onItemClick(van: VansFeederListDomain?) {
        if (player != null) {
            player!!.loadVideo(van?.contentUrl)
            player!!.play()
            binding.ytTitleTv.text = van?.title
            binding.ytDescriptionTv.text = van?.desc


        }
    }

    override fun onShareItemClick(it: VansFeederListDomain?) {

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://adminuat.outgrowdigital.com/videoshare?video_id=${it?.id}&video_name=${it?.title}&video_desc=${it?.desc}&content_url=${it?.contentUrl}"))
            .setDomainUriPrefix("https://outgrowdev.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setImageUrl(Uri.parse("https://admindev.outgrowdigital.com/img/OutgrowLogo500X500.png"))
                    .setTitle("Outgrow - Hi, Checkout the video on ${it?.title}.")
                    .setDescription("Watch more videos and learn with Outgrow")
                    .build()
            )
            .buildShortDynamicLink().addOnCompleteListener { task ->
                if (task.isSuccessful) {
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