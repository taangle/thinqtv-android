package com.thinqtv.thinqtv_android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class AddEventActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        setContentView(R.layout.activity_add_event);
        Button setTimeButton = findViewById(R.id.time);
        setTimeButton.setOnClickListener(view -> {
            DialogFragment newFragment = new TimePickerFragment((view1, hour, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                setTimeButton.setText(calendar.getTime().toString().substring(11, 16));
            });
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });
        Button setDateButton = findViewById(R.id.date);
        setDateButton.setOnClickListener(view -> {
            DialogFragment newFragment = new DatePickerFragment((datePicker, year, month, day) -> {
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DATE, day);
                calendar.set(Calendar.YEAR, year);
                setDateButton.setText(calendar.getTime().toString().substring(0, 10));
            });
            newFragment.show(getSupportFragmentManager(), "datePicker");
        });

        SeekBar lengthSlider = findViewById(R.id.length_slider);
        TextView lengthDisplay = findViewById(R.id.length_display);
        lengthSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                    @Override
                                                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                                        i += 1;
                                                        int hours = i / 4;
                                                        int minutes = (i % 4) * 15;
                                                        String time = hours + ":" + minutes;
                                                        if (minutes == 0) {
                                                            time += "0";
                                                        }
                                                        lengthDisplay.setText(time);
                                                    }

                                                    @Override
                                                    public void onStartTrackingTouch(SeekBar seekBar) {}

                                                    @Override
                                                    public void onStopTrackingTouch(SeekBar seekBar) {}
                                                }
        );

        Button sendButton = findViewById(R.id.send);
        Context context = this;
        sendButton.setOnClickListener(view -> {
            String name = ((EditText) findViewById(R.id.event_title)).getText().toString();
            String desc = ((EditText) findViewById(R.id.description)).getText().toString();
            Date date = calendar.getTime();
            LocalDateTime start = DateTimeUtils.toInstant(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            int length = (lengthSlider.getProgress() + 1) * 15;
            ZonedDateTime zoned_start = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/Phoenix"));
            ZonedDateTime zoned_end = zoned_start.plusMinutes(length);

            String start_string = zoned_start.toLocalDateTime().toString();
            String end_string = zoned_start.plusMinutes(length).toLocalDateTime().toString();
            makeRequest(context, name, start_string, end_string, desc);
        });
    }

    public static class TimePickerFragment extends DialogFragment {
        private TimePickerDialog.OnTimeSetListener listener;
        public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener) {
            super();
            this.listener = listener;
        }
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // default values are the current time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), listener, hour, minute, DateFormat.is24HourFormat(getActivity()));
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

    public void makeRequest(Context context, String name, String start, String end, String desc) {
        final String url = "api/v1/events";
        JSONObject event = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            event.put("name", name);
            event.put("start_at", start);
            event.put("end_at", end);
            event.put("desc", desc);
            params.put("event", event);
            params.put("user_email", UserRepository.getInstance().getLoggedInUser().getEmail());
            params.put("user_token", UserRepository.getInstance().getLoggedInUser().getAuthToken());
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getString(R.string.events_url), params, response -> {
            try {
                UserRepository.getInstance().getLoggedInUser().updateToken(response.getString("token"));
                ((Activity)context).finish();
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.e("create event", "server error");
        });
        DataSource.getInstance().addToRequestQueue(request, context);
    }
}

