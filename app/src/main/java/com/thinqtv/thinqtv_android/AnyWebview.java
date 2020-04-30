package com.thinqtv.thinqtv_android;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class AnyWebview extends AppCompatActivity {
    private AnyWebview webViewActivity = this;
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_webview);

        view = findViewById(R.id.webView);
        view.setVisibility(WebView.INVISIBLE);
        String link = "";
        if (getIntent().getExtras() != null)
            link = getIntent().getExtras().getString("webviewLink");

        String address = link;

        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String url) {
                try {
                    // remove the nav bar and the footer from each loaded page.
                    webView.loadUrl("javascript:(window.onload = function() { " +
                            "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                            "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                            "(label = document.getElementsByClassName('box')[0]); if (label != undefined) { label.parentNode.removeChild(label); }" +
                            "Android.displayPage();" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(address);
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
                    view.setVisibility(WebView.VISIBLE);
                }
            });
        }
    }
}
