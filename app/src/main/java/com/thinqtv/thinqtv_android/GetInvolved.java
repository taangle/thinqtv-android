//For the "Get Involved" -> "Organizations"

package com.thinqtv.thinqtv_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class GetInvolved extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private WebView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_involve);
        view = findViewById(R.id.webView);
        view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
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
        });
        view.loadUrl("https://thinqtv.herokuapp.com/getinvolved");
        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Pages,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void changeSite(String site){
        view = findViewById(R.id.webView);
        view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setDomStorageEnabled(true);
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
        });
        view.loadUrl(site);
    }

    public void goHome(View v){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        System.out.println(" ''" + v + " ''");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0:
                changeSite("https://thinqtv.herokuapp.com/getinvolved");
                break;
            case 1:
                changeSite("https://thinqtv.herokuapp.com/drschaeferspeaking");
                break;
            case 2:
                changeSite("https://thinqtv.herokuapp.com/bystanderguidelines");
                break;
            case 3:
                changeSite("https://thinqtv.herokuapp.com/aboutus");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
