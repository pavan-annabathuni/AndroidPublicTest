package com.waycool.videos


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.databinding.ActivityVideoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoActivity : AppCompatActivity() {


    private val binding: ActivityVideoBinding by lazy { ActivityVideoBinding.inflate(layoutInflater) }
    lateinit var navHost: Fragment

    private lateinit var adapterVideo: VideosPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_videos)!!
        CoroutineScope(Dispatchers.Main).launch {

            if (!FeatureLogin.getLoginStatus()) {
                val intent = Intent(this@VideoActivity, LoginActivity::class.java)
                startActivity(intent)
                this@VideoActivity.finish()

            }
        }

    }

    override fun onStart() {
        super.onStart()
        DeepLinkNavigator.navigateFromDeeplink(this@VideoActivity) { pendingDynamicLinkData ->
            var deepLink: Uri? = null
            if (pendingDynamicLinkData != null) {
                deepLink = pendingDynamicLinkData.link
            }
            if (deepLink != null) {
                if (deepLink.lastPathSegment == "videoslist") {
                    this.findNavController(R.id.nav_host_fragment_videos).navigate(R.id.videosListFragment)
                } else {
                    val id = deepLink.getQueryParameter("video_id")
                    val title = deepLink.getQueryParameter("video_name")
                    val description = deepLink.getQueryParameter("video_desc")
                    val contentUrl = deepLink.getQueryParameter("content_url")
                    if (!id.isNullOrEmpty() && !title.isNullOrEmpty()) {
                        val args = Bundle()
                        args.putInt("id", id.toInt())
                        args.putString("title", title)
                        args.putString("description", description)
                        args.putString("url", contentUrl)
                        this.findNavController(R.id.nav_host_fragment_videos)
                            .navigate(R.id.action_videosListFragment_to_playVideoFragment, args)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        navHost.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.onActivityResult(
                requestCode,
                resultCode,
                data
            )
        }
    }

}