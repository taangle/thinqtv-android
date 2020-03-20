package com.thinqtv.thinqtv_android.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Class that handles requests to the server.
 */
public class DataSource {
    private static DataSource instance;
    private RequestQueue requestQueue;
    private Context context;
    private static final String url = "https://fast-mountain-02267.herokuapp.com/";

    private DataSource(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static DataSource getInstance(Context context) {
        if (instance == null) {
            instance = new DataSource(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static String getServerUrl() {
        return url;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
