package com.thinqtv.thinqtv_android.stripe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.CustomerSession;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.PaymentSession;
import com.stripe.android.PaymentSessionConfig;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.ShippingInformation;
import com.thinqtv.thinqtv_android.R;
import com.thinqtv.thinqtv_android.data.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StripeHostActivity extends ComponentActivity {
    private PaymentSession paymentSession;
    private MyPaymentSessionListener paymentSessionListener;
    private Stripe stripe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String merchandiseId = getIntent().getExtras().getString("merchandise_id");
        setContentView(R.layout.activity_stripe_host);

        stripe = new Stripe(getApplicationContext(), "pk_test_wWEX8coPe3XAN08BCg8wk7hg00b8AUS35M");

        Button choosePaymentMethodButton = findViewById(R.id.changePaymentButton);
        choosePaymentMethodButton.setOnClickListener(v -> {
            paymentSession.presentPaymentMethodSelection(null);
        });

        Button startPaymentFlowButton = findViewById(R.id.startPaymentFlowButton);
        TextView paymentMethodLabel = findViewById(R.id.paymentMethodLabel);
        startPaymentFlowButton.setEnabled(false);
        paymentSession = new PaymentSession(
                this,
                new PaymentSessionConfig.Builder()
                        .setShippingInfoRequired(false)
                        .setShippingMethodsRequired(false)
                        .build()
        );
        paymentSessionListener = new MyPaymentSessionListener(this, startPaymentFlowButton, paymentMethodLabel);
        paymentSession.init(paymentSessionListener);
        startPaymentFlowButton.setOnClickListener((v) -> {
            JSONObject params = new JSONObject();
            try {
                params.put("merchandise_id", merchandiseId);
                params.put("customer_id", CustomerSession.getInstance().getCachedCustomer().getId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = getString(R.string.stripe_url) + "/create_payment_intent";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, params, response -> {
                try {
                    String paymentIntentClientSecret = response.getString("client_secret");
                    processPayment(paymentIntentClientSecret, paymentSessionListener.getPaymentMethodId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                error.printStackTrace();
            });
            DataSource.getInstance().addToRequestQueue(request, this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data);
        }

        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this, paymentSession));
    }

    @NonNull
    private ShippingInformation getDefaultShippingInfo() {
        // optionally specify default shipping address
        return new ShippingInformation();
    }

    private void processPayment(String clientSecret, String paymentId) {
        stripe.confirmPayment(this,
                ConfirmPaymentIntentParams.createWithPaymentMethodId(
                        paymentId,
                        clientSecret,
                        getString(R.string.stripe_url)
                )
        );
    }

    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        private Activity activity;
        private PaymentSession paymentSession;

        public PaymentResultCallback(Activity activity, PaymentSession paymentSession) {
            this.activity = activity;
            this.paymentSession = paymentSession;
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                paymentSession.onCompleted();
                activity.finish();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            e.printStackTrace();
        }
    }
}