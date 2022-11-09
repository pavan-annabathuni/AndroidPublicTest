package com.waycool.newsandarticles.view

import android.media.MediaPlayer
import nl.changer.audiowife.AudioWife
import android.os.Bundle
import android.text.Html
import com.squareup.picasso.Picasso
import com.waycool.newsandarticles.R
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.waycool.newsandarticles.Util.AppUtil
import com.waycool.newsandarticles.Util.NetworkUtil
import com.waycool.newsandarticles.databinding.ActivityNewsFullLayoutBinding
import com.waycool.newsandarticles.databinding.AudioNewLayoutBinding

class NewsFullviewActivity : AppCompatActivity() {
    private val binding: ActivityNewsFullLayoutBinding by lazy {
        ActivityNewsFullLayoutBinding.inflate(
            layoutInflater
        )
    }

    private lateinit var audioNewLayout: AudioNewLayoutBinding

    var title: String? = null
    var desc: String? = null
    var image: String? = null
    var audioUrl: String? = null
    var newsDate: String? = null
    var source: String? = null
    var mediaPlayer: MediaPlayer? = null
    private val startTime = 0.0
    private val finalTime = 0.0
    private val myHandler = Handler()
    var audioWife: AudioWife? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        audioNewLayout = binding.audioLayout

        val bundle = intent.extras
        if (bundle != null) {
            title = bundle.getString("title")
            desc = bundle.getString("content")
            image = bundle.getString("image")
            audioUrl = bundle.getString("audio")
            newsDate = bundle.getString("date")
            source = bundle.getString("source")
        }
        binding.newsHeading.text = "News Updates"
        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.title.text = title ?: ""
        binding.desc.text = Html.fromHtml(desc ?: "")
        binding.newsDate.text = AppUtil.changeDateFormat(newsDate ?: "")
        binding.shareBtn.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.primaryColor))

        if (source != null) {
            binding.newsSource.text = source
        } else {
            binding.newsSource.visibility = View.GONE
        }

        if (audioUrl == null) {
            audioNewLayout.root.visibility = View.GONE
        } else
            audioNewLayout.root.visibility = View.VISIBLE


        //setting colors for textview
        binding.newsDate.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.textdark))
        binding.newsSource.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.textdark))
        if (audioUrl == null) {
            audioNewLayout.root.visibility = View.GONE
        }

        //to load images
        Picasso.get()
            .load(image)
            .fit()
            .placeholder(R.drawable.outgrow_logo_new)
            .into(binding.newsImage)
        audioWife = AudioWife.getInstance()
        audioWife?.addOnCompletionListener(OnCompletionListener {
            //                audioWife.release();
        })
        audioNewLayout.play.setOnClickListener { view ->
            if (audioUrl != null) {
                playAudio(audioUrl!!)
            } else {
                Toast.makeText(this, "Audio file not found", Toast.LENGTH_SHORT).show()

            }
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            Toast.makeText(this@NewsFullviewActivity, "completed", Toast.LENGTH_SHORT).show()
            audioNewLayout.mediaSeekbar.setProgress(0)
            audioNewLayout.pause.setVisibility(View.GONE)
            audioNewLayout.play.setVisibility(View.VISIBLE)
        }
        binding!!.shareBtn.setOnClickListener { view: View ->
            AppUtil.shareItem(
                this@NewsFullviewActivity, image, binding!!.newsImage, """
     $title
     https://play.google.com/store/apps/details?id=${view.context.packageName}
     """.trimIndent()
            )
        }
    }

    private fun playAudio(path: String) {
        Log.d("RecordView", "actiondown2")
        audioWife = AudioWife.getInstance()
        audioWife?.init(this@NewsFullviewActivity, Uri.parse(path))
            ?.setPlayView(audioNewLayout.play)
            ?.setPauseView(audioNewLayout.pause)
            ?.setSeekBar(audioNewLayout.mediaSeekbar)
            ?.setRuntimeView(audioNewLayout.totalTime)
        //                .setTotalTimeView(mTotalTime);
        audioWife?.play()
    }

    //back button pressed method
    override fun onBackPressed() {
        super.onBackPressed()
        audioWife?.release()
    }

    companion object {
        var oneTimeOnly = 0
    }
}