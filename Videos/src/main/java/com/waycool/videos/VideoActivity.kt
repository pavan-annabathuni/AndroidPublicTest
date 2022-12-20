package com.waycool.videos


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
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
                val intent = Intent(this@VideoActivity, LoginMainActivity::class.java)
                startActivity(intent)
                this@VideoActivity.finish()

            }
        }
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null) {

                    Log.d("videoLink","$deepLink")
                    val id = deepLink.getQueryParameter ("video_id")
                    val title = deepLink.getQueryParameter ("video_name")
                    val description = deepLink.getQueryParameter ("video_desc")
                    val contentUrl = deepLink.getQueryParameter ("content_url")

                    Log.d("Video","$deepLink")

                        if (!id.isNullOrEmpty()&&!title.isNullOrEmpty()) {
                            val args = Bundle()
                            args.putInt("id", id.toInt())
                            args.putString("title",title)
                            args.putString("description",description)
                            args.putString("url",contentUrl)

                            this.findNavController(R.id.nav_host_fragment_videos).navigate(R.id.action_videosListFragment_to_playVideoFragment, args)
                        }
                    }
            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }
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