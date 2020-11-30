package com.thinqtv.thinqtv_android.stripe;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.stripe.android.EphemeralKeyProvider;
import com.stripe.android.EphemeralKeyUpdateListener;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StripeEphemeralKeyProvider implements EphemeralKeyProvider {
    private Context context;

    public StripeEphemeralKeyProvider(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void createEphemeralKey(String apiVersion, EphemeralKeyUpdateListener keyUpdateListener) {
        JSONObject params = new JSONObject();
        try {
            params.put("api_version", apiVersion);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = context.getResources().getString(R.string.stripe_ephemeral_key_url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
            try {
                keyUpdateListener.onKeyUpdate(response.getString("key"));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            error.printStackTrace();
        });
        DataSource.getInstance().addToRequestQueue(request, context);
    }
}
