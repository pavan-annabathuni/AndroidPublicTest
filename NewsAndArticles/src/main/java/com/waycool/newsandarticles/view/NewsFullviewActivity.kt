package com.waycool.newsandarticles.view

import android.content.Intent
import android.media.MediaPlayer
import nl.changer.audiowife.AudioWife
import android.os.Bundle
import android.text.Html
import com.squareup.picasso.Picasso
import com.waycool.newsandarticles.R
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.waycool.data.error.ToastStateHandling
import com.waycool.data.eventscreentime.EventScreenTimeHandling
import com.waycool.newsandarticles.Util.AppUtil
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
        binding.title.text = title?:""
        binding.desc.text = Html.fromHtml(desc?:"")
        binding.newsDate.text = AppUtil.changeDateFormat(newsDate?:"" )
        binding.shareBtn.setTextColor(resources.getColor(com.waycool.uicomponents.R.color.primaryColor))


        binding.shareBtn.setOnClickListener {
            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://adminuat.outgrowdigital.com/newsandarticlesfullscreen?title=${title}&content=${desc}&image=${image}&audio=${audioUrl}&date=${newsDate}&source=${source}"))
                .setDomainUriPrefix("https://outgrowdev.page.link")
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder()
                        .setFallbackUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.waycool.iwap"))
                        .build()
                )
                .setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setImageUrl(Uri.parse("https://admindev.outgrowdigital.com/img/OutgrowLogo500X500.png"))
                        .setTitle("Outgrow - Hi, Checkout the News and Articles on ${title}.")
                        .setDescription("Watch more News and Articles and learn with Outgrow")
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
                ToastStateHandling.toastError(this, "Audio file not found", Toast.LENGTH_SHORT)

            }
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnCompletionListener {
            ToastStateHandling.toastSuccess(this@NewsFullviewActivity, "Completed", Toast.LENGTH_SHORT)
            audioNewLayout.mediaSeekbar.progress = 0
            audioNewLayout.pause.visibility = View.GONE
            audioNewLayout.play.visibility = View.VISIBLE
        }
/*        binding!!.shareBtn.setOnClickListener { view: View ->
            AppUtil.shareItem(
                this@NewsFullviewActivity, image, binding!!.newsImage, """
     $title
     https://play.google.com/store/apps/details?id=${view.context.packageName}
     """.trimIndent()
            )
        }*/
    }




    private fun playAudio(path: String) {
        Log.d("RecordView", "actiondown2")
        mediaPlayer = MediaPlayer();
        mediaPlayer!!.setOnCompletionListener {
            audioNewLayout.mediaSeekbar.progress = 0
            audioNewLayout.pause.visibility = View.GONE
            audioNewLayout.play.visibility = View.VISIBLE
        }
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
    override fun onResume() {
        super.onResume()
        EventScreenTimeHandling.calculateScreenTime("NewsFullViewActivity")
    }
}