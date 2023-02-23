package com.waycool.newsandarticles.view

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.squareup.picasso.Picasso
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.newsandarticles.R
import com.waycool.newsandarticles.Util.AppUtil
import com.waycool.newsandarticles.databinding.ActivityNewsFullLayoutBinding
import com.waycool.newsandarticles.databinding.AudioNewLayoutBinding

import nl.changer.audiowife.AudioWife

class NewsAndArticlesFullViewActivity : AppCompatActivity() {
    private lateinit var audioNewLayout: AudioNewLayoutBinding
    private lateinit var  binding: ActivityNewsFullLayoutBinding
    var title: String? = null
    var desc: String? = null
    var image: String? = null
    var audioUrl: String? = null
    var newsDate: String? = null
    var source: String? = null
    var vansType: String? = null

    var mediaPlayer: MediaPlayer? = null
    var audioWife: AudioWife? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsFullLayoutBinding.inflate(layoutInflater)
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
            vansType = bundle.getString("vansType")

        }
        if (audioUrl.isNullOrEmpty()) {
            audioNewLayout.root.visibility = View.GONE
        } else {
            audioNewLayout.root.visibility = View.VISIBLE
        }
        if(vansType=="news"){
            vansType="News"
        }
        else if(vansType=="articles"){
            vansType="Articles"
        }
        else{
            vansType="Latest"
        }
        binding.newsHeading.text = "$vansType Updates"
        binding.backBtn.setOnClickListener { onBackPressed() }
        binding.title.text = title?:""
        binding.desc.text = Html.fromHtml(desc?:"")
        binding.newsDate.text = AppUtil.changeDateFormat(newsDate?:"" )
        binding.shareBtn.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.primaryColor))


        binding.shareBtn.setOnClickListener {
            binding.shareBtn.isEnabled=false
            binding.clShareProgress.visibility=View.VISIBLE
            val thumbnail = if(image.isNullOrEmpty()){
               image
            } else{
                "https://admindev.outgrowdigital.com/img/OutgrowLogo500X500.png"
            }
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://adminuat.outgrowdigital.com/newsandarticlesfullscreen?title=${title}&content=${desc}&image=${image}&audio=${audioUrl}&date=${newsDate}&source=${source}&vansType=${vansType}"))
                .setDomainUriPrefix("https://outgrowdev.page.link")
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setImageUrl(Uri.parse(thumbnail))
                        .setTitle("Outgrow - Hi, Checkout the News and Articles about ${title}.")
                        .setDescription("Watch more News and Articles and learn with Outgrow")
                        .build()
                )
                .buildShortDynamicLink().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        binding.clShareProgress.visibility=View.GONE
                        Handler().postDelayed({binding.shareBtn.isEnabled = true
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
        if (source != null) {
            binding.newsSource.text = source
        } else {
            binding.newsSource.visibility = View.GONE
        }

        //setting colors for textview
        binding.newsDate.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.textdark))
        binding.newsSource.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.textdark))


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
        audioNewLayout.play.setOnClickListener {
            audioUrl?.let { it1 -> playAudio(it1) }
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            ToastStateHandling.toastSuccess(this@NewsAndArticlesFullViewActivity, "Completed", Toast.LENGTH_SHORT)
            audioNewLayout.mediaSeekbar.progress = 0
            audioNewLayout.pause.visibility = View.GONE
            audioNewLayout.play.visibility = View.VISIBLE
        }
    }
    private fun playAudio(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            audioNewLayout.mediaSeekbar.progress = 0
            audioNewLayout.pause.visibility = View.GONE
            audioNewLayout.play.visibility = View.VISIBLE
        }
            audioWife = AudioWife.getInstance()
            audioWife?.init(this@NewsAndArticlesFullViewActivity, Uri.parse(path))
                ?.setPlayView(audioNewLayout.play)
                ?.setPauseView(audioNewLayout.pause)
                ?.setSeekBar(audioNewLayout.mediaSeekbar)
                ?.setRuntimeView(audioNewLayout.totalTime)
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
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NewsFullViewActivity")
    }
}