package com.waycool.videos


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.waycool.videos.adapter.VideosPagerAdapter
import com.waycool.videos.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {


    private val binding: ActivityVideoBinding by lazy { ActivityVideoBinding.inflate(layoutInflater) }
    lateinit var navHost: Fragment

    private lateinit var adapterVideo: VideosPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
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