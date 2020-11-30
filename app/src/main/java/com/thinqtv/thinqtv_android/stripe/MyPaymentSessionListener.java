package com.thinqtv.thinqtv_android.stripe;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionData;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MyPaymentSessionListener
        implements PaymentSession.PaymentSessionListener {

    private String paymentMethodId;
    private String clientSecret;
    private Context context;
    private Button startButton;
    private TextView paymentMethodLabel;
    public MyPaymentSessionListener(Context context, Button startButton, TextView paymentMethodLabel) {
        this.context = context;
        this.startButton = startButton;
        this.paymentMethodLabel = paymentMethodLabel;
    }
    // Called whenever the PaymentSession's data changes,
    // e.g. when the user selects a new `PaymentMethod` or enters shipping info.
    @Override
    public void onPaymentSessionDataChanged(@NonNull PaymentSessionData data) {
        if (data.getUseGooglePay()) {
            // customer intends to pay with Google Pay
        } else {
            final PaymentMethod paymentMethod = data.getPaymentMethod();
            if (paymentMethod != null) {
                paymentMethodId = paymentMethod.id;
                if (paymentMethod.card != null) {
                    paymentMethodLabel.setText(paymentMethod.card.brand + " ending in " + paymentMethod.card.last4);
                }
            }
        }

        // Update your UI here with other data
        if (data.isPaymentReadyToCharge()) {
            startButton.setEnabled(true);
        }
    }

    @Override
    public void onCommunicatingStateChanged(boolean isCommunicating) {
        if (isCommunicating) {
            // update UI to indicate that network communication is in progress
        } else {
            // update UI to indicate that network communication has completed
        }
    }

    @Override
    public void onError(int errorCode, @NotNull String errorMessage) {
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }
}