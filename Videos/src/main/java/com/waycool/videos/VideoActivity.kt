package com.waycool.videos


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.waycool.data.utils.Resource
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginActivity
import com.waycool.featurelogin.deeplink.DeepLinkNavigator
import com.waycool.featurelogin.deeplink.DeepLinkNavigator.VIDEO_LIST
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.databinding.ActivityVideoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoActivity : AppCompatActivity() {


    private val binding: ActivityVideoBinding by lazy { ActivityVideoBinding.inflate(layoutInflater) }
    lateinit var navHost: Fragment
    private val videoViewModel: VideoViewModel by lazy { ViewModelProvider(this)[VideoViewModel::class.java] }
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
                if (deepLink.lastPathSegment.equals(VIDEO_LIST)) {
                    this.findNavController(R.id.nav_host_fragment_videos)
                        .navigate(R.id.videosListFragment)
                } else {
                    val id = deepLink.getQueryParameter("id")
                    if (!id.isNullOrEmpty()) {
                        Log.d("NADeepLink", "NADeepLink2 $id")
                        videoViewModel.getVansSharedData(id.toInt())
                            .observe(this, androidx.lifecycle.Observer {
                                when (it) {
                                    is Resource.Success -> {
                                        val vans = it.data
                                        Log.d("NADeepLink", "NADeepLink2 $vans")

                                        val args = Bundle()
                                        args.putString("title", vans?.title)
                                        args.putString("description", vans?.desc)
                                        args.putString("url", vans?.content_url)
                                        this.findNavController(R.id.nav_host_fragment_videos)
                                            .navigate(
                                                R.id.action_videosListFragment_to_playVideoFragment,
                                                args
                                            )
                                    }
                                    is Resource.Loading -> {
                                        Log.d("NADeepLink", "NADeepLink2 load")

                                    }
                                    is Resource.Error -> {
                                        Log.d("NADeepLink", "NADeepLink2 error ${it.message}")

                                    }
                                }

                            })
                    }


                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
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