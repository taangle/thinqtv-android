package com.thinqtv.thinqtv_android;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;

import com.stripe.android.PaymentConfiguration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidThreeTen.init(this);

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_wWEX8coPe3XAN08BCg8wk7hg00b8AUS35M"
        );
    }
}
