package com.example.vl;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    private WebView webview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        webview = new WebView(this);
        setContentView(webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Perform basic validation on the URL (with more rigorous validation as per actual requirements)
                if (isValidUrl(url)) {
                    view.loadUrl(url);
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });

        configureWebViewSettings();

        webview.loadUrl("http://your_url/");//Display a remote webpage
    }

    private void configureWebViewSettings() {
        WebSettings settings = webview.getSettings();
        settings.setLoadsImagesAutomatically(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setDomStorageEnabled(true);
       // settings.setAcceptCookie(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.getSettings().setAppCacheEnabled(true);

        // Allow mixed content only on API level 21 and above. For lower versions, ensure content is transmitted over HTTPS.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Enabling Cross-Origin Resource Sharing (CORS) and Mixed Content Cookies
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            // Third-party cookies can be enabled if necessary
            cookieManager.setAcceptThirdPartyCookies(webview, true);
        }

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
    }

    private boolean isValidUrl(String url) {
        return url != null && url.startsWith("http://") || url.startsWith("https://");
    }

    // Return Functionality
    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else if (secondTime - firstTime > 2000) {
            webview.loadUrl("http://your_url/");
            firstTime = secondTime;
            return true;
        } else {
            System.exit(0);
        }
        return super.onKeyDown(keyCode, event);
    }

    // Prevent Memory Leaks
    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webview.clearHistory();
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }
}
