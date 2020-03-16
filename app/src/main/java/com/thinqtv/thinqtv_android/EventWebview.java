package com.thinqtv.thinqtv_android;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class EventWebview extends AppCompatActivity {
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_webview);

        view = findViewById(R.id.webView);

        view.getSettings().setJavaScriptEnabled(true);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String url) {
                try {
                    // remove the nav bar and the footer from each loaded page.
                    view.loadUrl("javascript:(window.onload = function() { " +
                            "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                            "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });

        String code = "";
        if(getIntent().getExtras() !=null)
            code = getIntent().getExtras().getString("eventCode");

        String address = "https://thinqtv.herokuapp.com/events/" + code;
        Log.d("myTag", "String address = " + address);
        view.loadUrl(address);
    }
}
