package com.waycool.iwap.splash

import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.waycool.featurelogin.FeatureLogin
import com.waycool.featurelogin.activity.LoginMainActivity
import com.waycool.iwap.MainActivity
import com.waycool.iwap.databinding.ActivitySplashBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    lateinit var splashBinding: ActivitySplashBinding
    var logoAnim: AnimatedVectorDrawable? = null
    var imageSplashAnim: AnimatedVectorDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(LayoutInflater.from(this))
        setContentView(splashBinding.root)

        if (splashBinding.imageSplash.drawable is AnimatedVectorDrawable) {
            imageSplashAnim = splashBinding.imageSplash.drawable as AnimatedVectorDrawable
            imageSplashAnim!!.start()
        }

        if (splashBinding.logoAvdIv.drawable is AnimatedVectorDrawable) {
            logoAnim = splashBinding.logoAvdIv.drawable as AnimatedVectorDrawable
            logoAnim!!.start()
        }


        Handler(Looper.myLooper()!!).postDelayed({
            CoroutineScope(Dispatchers.Main).launch {
                if (!FeatureLogin.getLoginStatus()) {
                    val intent = Intent(this@SplashActivity, LoginMainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }else{
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }
            }
        }, 2000)

    }

}