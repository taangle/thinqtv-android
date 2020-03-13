package com.thinqtv.thinqtv_android.data;

public interface LoginListener {
    void onError(Object response);
    void onResponse(Object response);
}