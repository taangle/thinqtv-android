//For the "Get Involved" -> "Organizations"

package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

public class GetInvolved extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private WebView webpageViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_get_involve);
            webpageViewer = findViewById(R.id.webView);
            webpageViewer.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
            webpageViewer.getSettings().setJavaScriptEnabled(true);
            webpageViewer.getSettings().setDomStorageEnabled(true);
            webpageViewer.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView webView, String url) {
                    try {
                        // remove the nav bar and the footer from each loaded page.
                        webpageViewer.loadUrl("javascript:(window.onload = function() { " +
                                "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                                "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                                "})()");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //the following disable any links on the web page.
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                   // if (url.equals("https://thinqtv.herokuapp.com/parents")) {
                    if (url.equals("https://www.thinq.tv/jointheteam")) {
                        view.loadUrl(url);
                    }
                    return true;
                }
            }
            );
           // webpageViewer.loadUrl("https://thinqtv.herokuapp.com/parents");
        webpageViewer.loadUrl("https://www.thinq.tv/jointheteam");

        /*
          - 'spinner' is the actual Spinner object which is our drop down menu
          - 'adapter' fills 'spinner' with the strings in the string array pages under res/values/strings.xml
          - line 50 sets the layout of the adapter, which we connect to  'spinner' in line 51
          - Finally, setOnItemSelectedListener allows the app to react to clicks on 'spinner'
         */
//            Spinner spinner = findViewById(R.id.spinner1);
//            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Pages, android.R.layout.simple_spinner_item);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner.setAdapter(adapter);
//            spinner.setOnItemSelectedListener(this);
    }

    //Identical to load, except can be called when user selects something on spinner.
    public void changeSite(String site){
        webpageViewer = findViewById(R.id.webView);
        webpageViewer.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        webpageViewer.getSettings().setDomStorageEnabled(true);
        webpageViewer.getSettings().setJavaScriptEnabled(true);
        webpageViewer.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webpageViewer.setWebViewClient(new WebViewClient() {
            @Override
            public void onLoadResource(WebView webView, String url) {
                try {
                    // remove the nav bar and the footer from each loaded page.
                    webpageViewer.loadUrl("javascript:(window.onload = function() { " +
                            "(navBar = document.getElementsByTagName('nav')[0]); navBar.parentNode.removeChild(navBar);" +
                            "(footer = document.getElementsByTagName('footer')[0]); footer.parentNode.removeChild(footer);" +
                            // remove the red box at the top of the page
                            "(label = document.getElementsByClassName('box')[0]); label.parentNode.removeChild(label);" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //the following disable any links on the web page.
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.equals(site) || url.equals(("https://thinqtv.herokuapp.com/boardofdirectors")) ||
                url.equals("https://www.thinq.tv/jointheteam") || url.equals("https://www.thinq.tv/drschaeferspeaking")
                || url.equals("https://www.thinq.tv/getinvolved")) {
                    view.loadUrl(url);
                }
                return true;
            }

        });

        webpageViewer.loadUrl(site);
    }

    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        System.out.println(" ''" + v + " ''");
    }

    public void goToProfile(View view) {
        //Intent i = new Intent(this, Profile.class);
        //startActivity(i);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                changeSite("https://www.thinq.tv/jointheteam");
                //goHome(view);
                break;
            case 1:
               // changeSite("https://thinqtv.herokuapp.com/drschaeferspeaking");
                changeSite("https://www.thinq.tv/drschaeferspeaking");
                break;
            case 2:
                changeSite("https://www.thinq.tv/getinvolved");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
