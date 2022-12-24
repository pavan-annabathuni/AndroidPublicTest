package com.waycool.videos.fragments

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
import com.waycool.core.utils.AppSecrets
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.repository.domainModels.VansFeederListDomain
import com.waycool.data.utils.NetworkUtil
import com.waycool.uicomponents.databinding.ApiErrorHandlingBinding
import com.waycool.videos.R
import com.waycool.videos.VideoViewModel
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.databinding.FragmentPlayVideoBinding

class PlayVideoFragment : Fragment() {
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
        networkCall()
        apiErrorHandlingBinding.clBtnTryAgainInternet.setOnClickListener {
            networkCall()
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    this@PlayVideoFragment.findNavController().navigateUp()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            callback
        )
        return binding.root
    }

    private fun networkCall() {
        if (NetworkUtil.getConnectivityStatusString(context) == 0) {
            binding.clInclude.visibility = View.VISIBLE
            apiErrorHandlingBinding.clInternetError.visibility = View.VISIBLE
            context?.let {
                ToastStateHandling.toastWarning(
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
        adapterVideo = VideosPagerAdapter(requireContext())
        binding.ytBottomsheetRv.adapter = adapterVideo

        Handler(Looper.myLooper()!!).postDelayed({
            getVideos()
        }, 600)


        val frag = childFragmentManager.findFragmentById(R.id.youtube_fragment) as YouTubePlayerSupportFragment

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




        adapterVideo.onItemClick = {
            if (player != null) {
                player!!.loadVideo(it?.contentUrl)
                player!!.play()
                binding.ytTitleTv.text = it?.title
                binding.ytDescriptionTv.text = it?.desc
            }
        }
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
        videoViewModel.getVansVideosList(tags, categoryId).observe(requireActivity()) {
            adapterVideo.submitData(lifecycle, it)
        }
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
//        dialog.setOnShowListener { dialogInterface ->
//            val bottomSheetDialog =
//                dialogInterface as BottomSheetDialog
//            setupFullHeight(bottomSheetDialog)
//        }
//        return dialog
//    }
//
//
//    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
//        val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
//        val behavior = BottomSheetBehavior.from(bottomSheet?.rootView!!)
//        val layoutParams = bottomSheet.layoutParams
//        val windowHeight = getWindowHeight()
//        if (layoutParams != null) {
//            layoutParams.height = windowHeight
//        }
//        behavior.isHideable = false
//        behavior.isDraggable = false
//        bottomSheet.layoutParams = layoutParams
//        behavior.state = BottomSheetBehavior.STATE_EXPANDED
//        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {}
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            }
//        })
//    }
//
//    private fun getWindowHeight(): Int {
//        // Calculate window height for fullscreen use
//        val displayMetrics = DisplayMetrics()
//        (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
//        return displayMetrics.heightPixels
//    }

}