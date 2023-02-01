package com.waycool.featurelogin.activity

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.waycool.data.eventscreentime.EventScreenTimeHandling.calculateScreenTime
import com.waycool.featurelogin.R
import com.waycool.featurelogin.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    var webView: WebView? = null
    var videoUrl: String? = null
    var my_crop_title: TextView? = null
    var mBack_btn: ImageButton? = null
    var TAG = javaClass.simpleName
    var prefs: SharedPreferences? = null
    var tittle: String? = null

    var activityPrivacyPolicyBinding: ActivityPrivacyPolicyBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPrivacyPolicyBinding = ActivityPrivacyPolicyBinding.inflate(
            layoutInflater
        )
        setContentView(activityPrivacyPolicyBinding!!.root)
        prefs = getSharedPreferences(getString(R.string.USER_PREFERENCES), 0)
        webView = findViewById(R.id.webview)
        val bundle = intent.extras
        if (bundle != null) {
            videoUrl = bundle.getString("url")
            tittle = bundle.getString("tittle")
        }
        Log.d(TAG, videoUrl!!)
        webView?.webViewClient = Browser_Home()
        webView?.webChromeClient = ChromeClient()
        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.allowFileAccess = true
        loadWebSite()
        setActionBar()
    }

    private fun setActionBar() {
        my_crop_title = findViewById<View>(R.id.crop_select_title) as TextView
        my_crop_title!!.text = tittle
        mBack_btn = findViewById(R.id.back_btn)
        mBack_btn?.setOnClickListener(View.OnClickListener { onBackPressed() })
    }

    private fun loadWebSite() {
        webView!!.loadUrl(videoUrl!!)
    }

    private inner class Browser_Home internal constructor() : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
        }
    }

    private inner class ChromeClient internal constructor() : WebChromeClient() {
        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        protected var mFullscreenContainer: FrameLayout? = null
        private var mOriginalOrientation = 0
        private var mOriginalSystemUiVisibility = 0
        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(
                applicationContext.resources,
                2130837573
            )
        }

        override fun onHideCustomView() {
            (window.decorView as FrameLayout).removeView(mCustomView)
            mCustomView = null
            window.decorView.systemUiVisibility = mOriginalSystemUiVisibility
            requestedOrientation = mOriginalOrientation
            mCustomViewCallback!!.onCustomViewHidden()
            mCustomViewCallback = null
        }

        override fun onShowCustomView(
            paramView: View,
            paramCustomViewCallback: CustomViewCallback
        ) {
            if (mCustomView != null) {
                onHideCustomView()
                return
            }
            mCustomView = paramView
            mOriginalSystemUiVisibility = window.decorView.systemUiVisibility
            mOriginalOrientation = requestedOrientation
            mCustomViewCallback = paramCustomViewCallback
            (window.decorView as FrameLayout).addView(mCustomView, FrameLayout.LayoutParams(-1, -1))
            window.decorView.systemUiVisibility = 3846 or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onResume() {
        super.onResume()
        calculateScreenTime("PrivacyPolicyActivity")
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}