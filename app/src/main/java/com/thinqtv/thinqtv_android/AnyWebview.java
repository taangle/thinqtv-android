package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stripe.android.CustomerSession;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.data.model.LoggedInUser;
import com.thinqtv.thinqtv_android.stripe.StripeHostActivity;

import androidx.appcompat.app.AppCompatActivity;

public class AnyWebview extends AppCompatActivity {
    private AnyWebview webViewActivity = this;
    private ProgressBar loading;
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_webview);
        loading = findViewById(R.id.loading);

        view = findViewById(R.id.webView);
        view.setHorizontalFadingEdgeEnabled(true);
        view.setFadingEdgeLength(40);
        view.setVisibility(WebView.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        String link = "";
        if (getIntent().getExtras() != null)
            link = getIntent().getExtras().getString("webviewLink");

        String address = link;

        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String url) {
                try {
                    // remove the nav bar and the footer from each loaded page.
                    webView.loadUrl("javascript:(window.onload = function() { " +
                            "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                            "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                            "(label = document.getElementsByClassName('box')[0]); if (label != undefined) { label.parentNode.removeChild(label); }" +
                            "(scheduleBtn = document.getElementById('appBackground')); if (scheduleBtn != undefined) { scheduleBtn.parentNode.removeChild(scheduleBtn); }" +
                            "links = document.getElementsByTagName('a'); [...links].forEach((link) => { link.removeAttribute('data-method'); });" +
                            "Android.displayPage();" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String[] urlSegments = url.split("/");
                if (urlSegments.length > 3 && urlSegments[3].equals("purchases")) {
                    Intent i = new Intent(webViewActivity, StripeHostActivity.class);
                    i.putExtra("merchandise_id", urlSegments[4].split("=")[1]);
                    startActivity(i);
                }
                return true;
            }
        });
        view.addJavascriptInterface(new JavascriptInterface(view), "Android");
        view.loadUrl(address);
    }

    class JavascriptInterface {
        private WebView view;

        public JavascriptInterface(WebView view) {
            this.view = view;
        }

        @android.webkit.JavascriptInterface
        public void displayPage() {
            webViewActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loading.setVisibility(View.INVISIBLE);
                    view.setVisibility(WebView.VISIBLE);
                }
            });
        }
    }
}
