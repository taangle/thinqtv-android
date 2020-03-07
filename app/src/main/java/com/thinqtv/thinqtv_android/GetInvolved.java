//For the "Get Involved" -> "Organizations"

package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GetInvolved extends AppCompatActivity {

    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        myWebView = findViewById(R.id.webView);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String url) {
                try {
                    // remove the nav bar and the footer from each loaded page.
                    myWebView.loadUrl("javascript:(window.onload = function() { " +
                            "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                            "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        myWebView.setWebViewClient(new HelloWebViewClient());
        myWebView.loadUrl("https://thinqtv.herokuapp.com/getinvolved");
    }

    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void applyNow(View V){
        Intent i = new Intent(this, GetInvolvedApplyNowPage.class);
        startActivity(i);
    }

    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }


}
