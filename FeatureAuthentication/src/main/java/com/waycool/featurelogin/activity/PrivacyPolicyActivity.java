package com.waycool.featurelogin.activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.waycool.data.eventscreentime.EventScreenTimeHandling;
import com.waycool.featurelogin.R;

;

public class PrivacyPolicyActivity extends AppCompatActivity {

    /*WebView is a View that displays web pages.*/
    WebView webView;
    String video_url;
    TextView my_crop_title;
    ImageButton mBack_btn;
    SharedPreferences prefs;
    String tittle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        prefs = getSharedPreferences(getString(R.string.USER_PREFERENCES), 0);
        webView = findViewById(R.id.webview);

        /*Getting the values from LoginFragment*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            video_url = bundle.getString("url");
            tittle = bundle.getString("tittle");
        }

        /*setWebViewClient- Sets the WebViewClient that will receive various notifications and requests. This will replace the current handler.*/
        webView.setWebViewClient(new Browser_Home());
        /*setWebChromeClient- Sets the chrome handler. This is an implementation of WebChromeClient for use in handling JavaScript dialogs, favicons, titles, and the progress. This will replace the current handler.*/
        webView.setWebChromeClient(new ChromeClient());
        /*WebSettings- Manages settings state for a WebView.*/
        WebSettings webSettings = webView.getSettings();
        /*setJavaScriptEnabled- Tells the WebView to enable JavaScript execution. The default is false.*/
        webSettings.setJavaScriptEnabled(true);
        /*setAllowFileAccessEnables or disables file access within WebView*/
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
        /*loadUrl- Loads the given URL(video_url).*/
        webView.loadUrl(video_url);
    }


    /*WebViewClient-
    1.Give the host application a chance to take control when a URL is about to be loaded in the current WebView.
    2.If a WebViewClient is not provided, by default WebView will ask Activity Manager to choose the proper handler for the URL.
    3.If a WebViewClient is provided, returning true causes the current WebView to abort loading the URL, while returning false causes the WebView to continue loading the URL as usual.*/
    private class Browser_Home extends WebViewClient {
        Browser_Home() {
        }

        /*onPageStarted- Notify the host application that a page has started loading. */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        /*onPageFinished- Notify the host application that a page has finished loading.*/
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class ChromeClient extends WebChromeClient {

        /*View- A View occupies a rectangular area on the screen and is responsible for drawing and event handling.*/
        private View mCustomView;
        private CustomViewCallback mCustomViewCallback;
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
            /*getWindow()- Retrieve the current Window for the activity. */
            /*getDecorView()- Returns the top-level window decor view.*/
            /*setSystemUiVisibility-Request that the visibility of the status bar or other screen/window decorations be changed.*/
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);

            /*setRequestedOrientation-Change the desired orientation of this activity.*/
            setRequestedOrientation(this.mOriginalOrientation);

            /*CustomViewCallback- Invoked when the host application dismisses the custom view.*/
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;

            /*getSystemUiVisibility- Returns the last setSystemUiVisibility(int) that this view has requested.*/
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();

            /*getRequestedOrientation-Return the current requested orientation of the activity. */
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;

            /*addView-Adds a child view with the specified layout parameters.*/
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*Time spent on this Screen*/
        EventScreenTimeHandling.INSTANCE.calculateScreenTime("PrivacyPolicy");
    }

    /*onBackPressed-Called when the activity has detected the user's press of the back key. */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}