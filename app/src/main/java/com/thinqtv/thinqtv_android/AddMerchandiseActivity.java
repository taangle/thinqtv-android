package com.thinqtv.thinqtv_android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.thinqtv.thinqtv_android.data.DataSource;
import com.thinqtv.thinqtv_android.data.UserRepository;

import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class AddMerchandiseActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        setContentView(R.layout.activity_add_merchandise);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button setDateButton = findViewById(R.id.date);
        setDateButton.setOnClickListener(view -> {
            DialogFragment newFragment = new AddMerchandiseActivity.DatePickerFragment((datePicker, year, month, day) -> {
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);
                calendar.set(Calendar.YEAR, year);
                setDateButton.setText(calendar.getTime().toString().substring(0, 10));
            });
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        Button sendButton = findViewById(R.id.send);
        Context context = this;
        sendButton.setOnClickListener(view -> {
            String name = ((EditText) findViewById(R.id.merchandise_title)).getText().toString();
            int selectedButtonId = ((RadioGroup)findViewById(R.id.merchandise_type)).getCheckedRadioButtonId();
            String type = "";
            if (selectedButtonId == R.id.donation_button) {
                type = "Donate";
            } else if (selectedButtonId == R.id.reward_button) {
                type = "Buy";
            }
            String price = ((EditText)findViewById(R.id.merchandise_price)).getText().toString();
            String desc = ((EditText) findViewById(R.id.description)).getText().toString();
            Date date = calendar.getTime();
            LocalDateTime deadline = DateTimeUtils.toInstant(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            deadline = deadline.minusMinutes(deadline.getMinute());
            deadline = deadline.minusSeconds(deadline.getSecond());
            deadline = deadline.minusNanos(deadline.getNano());
            ZonedDateTime zoned_deadline = deadline.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/Phoenix"));

            String deadline_string = zoned_deadline.toLocalDateTime().toString();
            makeRequest(context, name, type, price, desc, deadline_string);
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("edit")) {
            TextView addHeader = findViewById(R.id.add_header);
            TextView editHeader = findViewById(R.id.edit_header);
            addHeader.setVisibility(View.INVISIBLE);
            editHeader.setVisibility(View.VISIBLE);

            ((EditText)findViewById(R.id.merchandise_title)).setText(bundle.getString("name"));
            if (bundle.getString("buttontype").equals("Donate")) {
                ((RadioButton)findViewById(R.id.donation_button)).toggle();
            } else if (bundle.getString("buttontype").equals("Buy")) {
                ((RadioButton)findViewById(R.id.reward_button)).toggle();
            }
            ((EditText)findViewById(R.id.merchandise_price)).setText(bundle.getDouble("price") + "");
            ((EditText) findViewById(R.id.description)).setText(bundle.getString("desc"));

            String[] dateSegments = bundle.getString("deadline").split("-");
            calendar.set(Integer.parseInt(dateSegments[0]), Integer.parseInt(dateSegments[1]) - 1, Integer.parseInt(dateSegments[2].substring(0, 2)));
            setDateButton.setText(calendar.getTime().toString().substring(0, 10));
        }
    }
    public static class DatePickerFragment extends DialogFragment {
        private DatePickerDialog.OnDateSetListener listener;
        public DatePickerFragment(DatePickerDialog.OnDateSetListener listener) {
            super();
            this.listener = listener;
        }
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // default values are the current date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }

    public void makeRequest(Context context, String name, String type, String price, String desc, String deadline) {
        JSONObject params = new JSONObject();
        try {
            params.put("name", name);
            params.put("buttontype", type);
            params.put("price", price);
            params.put("desc", desc);
            params.put("deadline", deadline);
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }

        int requestMethod = Request.Method.POST;
        String url = getString(R.string.merchandise_url);
        if (getIntent().getExtras().getBoolean("edit")) {
            requestMethod = Request.Method.PUT;
            url = url + "/" + getIntent().getExtras().getInt("id");
        }
        JsonObjectRequest request = new JsonObjectRequest(requestMethod,
                url, params, response -> {
            this.finish();
        }, error -> {
            try {
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    JSONObject errors = new JSONObject(new String(error.networkResponse.data)).getJSONObject("errors");
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (UserRepository.getInstance().getLoggedInUser() != null) {
                    headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + UserRepository.getInstance().getLoggedInUser().getUserInfo().get("token"));
                }
                return headers;
            }
        };
        DataSource.getInstance().addToRequestQueue(request, context);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
