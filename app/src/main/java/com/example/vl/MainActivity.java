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

/**
 * The main activity of the application, responsible for displaying and managing the WebView.
 */
public class MainActivity extends AppCompatActivity {
    private WebView webview;

    /**
     * Called when the activity is starting. This is where most initialization should go: calling setContentView(int) to inflate
     * the activity's UI, using findViewById to interact with widgets in the UI.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle
     *                           contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize WebView and set its layout
        webview = new WebView(this);
        setContentView(webview);

        // Configure WebView client to handle URL loading
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Decide whether to handle URL loading in WebView or let the system handle it
                // Perform basic validation on the URL (with more rigorous validation as per actual requirements)
                if (isValidUrl(url)) {
                    view.loadUrl(url);
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });

        // Configure WebView settings
        configureWebViewSettings();

        // Load the initial URL
        webview.loadUrl("http://your_url/");//Display a remote webpage
    }

    /**
     * Configures various settings for the WebView to optimize the web browsing experience.
     */
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

        // For API level 21 and above, allow mixed content (for lower versions, ensure content is transmitted over HTTPS)
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

    /**
     * Validates whether a URL is in a correct format.
     * @param url The URL string to validate.
     * @return Returns true if the URL is valid; otherwise, returns false.
     */
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    /**
     * Handles the back button event to customize WebView's back navigation behavior.
     * @param keyCode The key code of the event.
     * @param event The KeyEvent object.
     * @return Returns true if the event was handled, false otherwise.
     */
    // Return Functionality
    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
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

    /**
     * Cleans up the WebView to avoid memory leaks when the activity is destroyed.
     */
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
