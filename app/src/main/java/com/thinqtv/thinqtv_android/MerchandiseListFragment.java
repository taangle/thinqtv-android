package com.thinqtv.thinqtv_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;
import com.thinqtv.thinqtv_android.stripe.StripeHostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class MerchandiseListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getMerchandiseList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_merchandise_list, container, false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        getMerchandiseList();
    }

    public void getMerchandiseList() {
        String permalink = UserRepository.getInstance().getLoggedInUser().getUserInfo().get("permalink");
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, getString(R.string.users_url) + "/" + permalink + "/merchandise", null, response -> {
            // Clear the linearLayout
            try {
                LinearLayout layout = getView().findViewById(R.id.merchandiseListLayout);
                layout.removeAllViews();
            } catch (NullPointerException e) { e.printStackTrace(); }

            // Fill it back in with the response data
            ArrayList<JSONObject> array = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                try {
                    array.add(response.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setMerchandiseList(array);
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LinearLayout layout = getView().findViewById(R.id.merchandiseListLayout);
                layout.removeView(getView().findViewById(R.id.loading));

                TextView loadingError = getView().findViewById(R.id.loadingError);
                loadingError.setVisibility(View.VISIBLE);
            }
        });
        DataSource.getInstance().addToRequestQueue(request, getContext());
    }

    // Use EventsJSON file to fill in ScrollView
    public void setMerchandiseList(ArrayList<JSONObject> json)
    {
        try {
            //link layout and JSON file
            LinearLayout linearLayout = getView().findViewById(R.id.merchandiseListLayout);

            //For each event in the database, create a new item for it in ScrollView
            for(int i=0; i < json.size(); i++)
            {
                JSONObject merch = json.get(i);
                // get the title of the event
                String name = merch.getString("name");
                String buttonType = merch.getString("buttontype");
                NumberFormat formatter = NumberFormat.getCurrencyInstance();
                String price = formatter.format(merch.getDouble("price"));

                // gets the name and sets its values
                TextView merchInfoTextView = new TextView(getContext());
                merchInfoTextView.setId(View.generateViewId());
                merchInfoTextView.setTextSize(22);
                merchInfoTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                merchInfoTextView.setLayoutParams(new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
                merchInfoTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                merchInfoTextView.setText(Html.fromHtml("<b>" + name + "</b><br> <font color=#7F7F7F>" + price + "</font>"));


                Button purchaseButton = new Button(getContext());
                purchaseButton.setId(View.generateViewId());
                purchaseButton.setBackground(getActivity().getDrawable(R.drawable.rounded_button));
                purchaseButton.setTextSize(18);
                purchaseButton.setTextColor(Color.WHITE);
                purchaseButton.setText("Edit");
                purchaseButton.setPadding(100,0,100,0);
                purchaseButton.setAllCaps(false);

                purchaseButton.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(this.getContext(), AddMerchandiseActivity.class);
                        intent.putExtra("edit", true);
                        intent.putExtra("name", merch.getString("name"));
                        intent.putExtra("buttontype", merch.getString("buttontype"));
                        intent.putExtra("price", merch.getDouble("price"));
                        intent.putExtra("desc", merch.getString("desc"));
                        intent.putExtra("deadline", merch.getString("deadline"));
                        intent.putExtra("id", merch.getInt("id"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                ConstraintLayout constraintLayout = new ConstraintLayout(getContext());
                constraintLayout.addView(merchInfoTextView);
                constraintLayout.addView(purchaseButton);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(purchaseButton.getId(), ConstraintSet.TOP, merchInfoTextView.getId(), ConstraintSet.BOTTOM,8);
                constraintSet.connect(purchaseButton.getId(), ConstraintSet.START, merchInfoTextView.getId(), ConstraintSet.START,0);
                constraintSet.connect(purchaseButton.getId(), ConstraintSet.END, merchInfoTextView.getId(), ConstraintSet.END,0);
                constraintSet.applyTo(constraintLayout);

                // Add simple divider to put in between ConstraintLayouts (ie events)
                View viewDivider = new View(getContext());
                viewDivider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2));
                viewDivider.setBackgroundColor(Color.LTGRAY);

                linearLayout.addView(constraintLayout);
                linearLayout.addView(viewDivider);

                constraintLayout.setPadding(50,50,50,50);
                constraintLayout.setLayoutParams(new LinearLayout.LayoutParams (ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
            }
        } catch (JSONException e) { e.printStackTrace(); }
    }
}
