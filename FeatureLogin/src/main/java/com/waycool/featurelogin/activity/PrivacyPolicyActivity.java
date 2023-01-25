package com.waycool.featurelogin.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



;import com.waycool.data.eventscreentime.EventScreenTimeHandling;
import com.waycool.featurelogin.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    WebView webView;
    String video_url;
    TextView my_crop_title;
    ImageButton mBack_btn;
    String TAG = getClass().getSimpleName();
    SharedPreferences prefs;
    String tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        prefs = getSharedPreferences(getString(R.string.USER_PREFERENCES), 0);


        webView = findViewById(R.id.webview);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            video_url = bundle.getString("url");
            tittle = bundle.getString("tittle");
        }
        Log.d(TAG, video_url);
        webView.setWebViewClient(new Browser_Home());
        webView.setWebChromeClient(new ChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        loadWebSite();
        setActionBar();
    }

    private void setActionBar() {

        my_crop_title = (TextView) findViewById(R.id.crop_select_title);
            my_crop_title.setText(tittle);
        mBack_btn = findViewById(R.id.back_btn);
        mBack_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadWebSite() {
        webView.loadUrl(video_url);
    }

    private class Browser_Home extends WebViewClient {
        Browser_Home() {
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventScreenTimeHandling.INSTANCE.calculateScreenTime("PrivacyPolicyActivity");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}