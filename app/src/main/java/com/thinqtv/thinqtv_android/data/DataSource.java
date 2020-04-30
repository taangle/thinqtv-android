package com.thinqtv.thinqtv_android.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class that handles requests to the server. Creates a standard request queue.
 */
public class DataSource {
    private static DataSource instance;
    private RequestQueue requestQueue;
    private static final String url = "https://fast-mountain-02267.herokuapp.com/";

    private DataSource() {
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static String getServerUrl() {
        return url;
    }

    public <T> void addToRequestQueue(Request<T> request, Context context) {
        getRequestQueue(context).add(request);
    }
}