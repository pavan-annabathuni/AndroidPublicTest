package com.waycool.iwap.allservices

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.iwap.databinding.FragmentServiceDescBinding
import nl.changer.audiowife.AudioWife

class ServiceDescFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentServiceDescBinding
    private var audio: AudioWife? = null
    var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var image: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title").toString()
            description = it.getString("desc").toString()
            image = it.getString("icon").toString()
           audioUrl = it.getString("audioUrl")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentServiceDescBinding.inflate(inflater)
        binding.privacyCloseBtn.setOnClickListener() {
            this.dismiss()
            audio?.release()
        }
        setData()
        audioPlayer()
        return binding.root
    }


    private fun setData() {
        binding.descItemName.text = title
        binding.descTv.text = description
        Glide.with(requireContext()).load(image).into(binding.descImage)
    }

    private fun audioPlayer() {
        Log.d("audioUrl", "audioPlayer: $audioUrl")
        if (audioUrl.isNullOrEmpty()) {
            binding.audioLayout.visibility = View.GONE
        }else{
            binding.audioLayout.visibility = View.VISIBLE
            binding.playPauseLayout.setOnClickListener() {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setOnCompletionListener {
                binding.mediaSeekbar.progress = 0
                binding.pause.visibility = View.GONE
                binding.play.visibility = View.VISIBLE


                audio = AudioWife.getInstance()
                    .init(requireContext(), Uri.parse(audioUrl))
                    .setPlayView(binding.play)
                    .setPauseView(binding.pause)
                    .setSeekBar(binding.mediaSeekbar)
                    .setRuntimeView(binding.totalTime)
                // .setTotalTimeView(mTotalTime);
                audio?.play()
            }

        }
    }}

    override fun onPause() {
        super.onPause()
        audio?.release()
    }

    override fun getTheme(): Int {
        return com.example.irrigationplanner.R.style.BottomSheetDialog

    }

    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("ServiceDescFragment")
    }
}