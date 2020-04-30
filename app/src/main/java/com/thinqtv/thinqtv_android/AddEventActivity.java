package com.thinqtv.thinqtv_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
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
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
            DialogFragment newFragment = new TimePickerFragment(new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    setTimeButton.setText(calendar.getTime().toString().substring(11, 16));
                }
            });
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });
        Button setDateButton = findViewById(R.id.date);
        setDateButton.setOnClickListener(view -> {
            DialogFragment newFragment = new DatePickerFragment(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DATE, day);
                    calendar.set(Calendar.YEAR, year);
                    setDateButton.setText(calendar.getTime().toString().substring(0, 10));
                }
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
        sendButton.setOnClickListener(view -> {
            Date date = calendar.getTime();
            LocalDateTime start = DateTimeUtils.toInstant(date).atZone(ZoneId.systemDefault()).toLocalDateTime();
            int length = (lengthSlider.getProgress() + 1) * 15;
            ZonedDateTime zoned_start = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("America/Phoenix"));

            Log.e("start_datetime", zoned_start.toString());
            Log.e("end_datetime", zoned_start.plusMinutes(length).toString());
        });
    }

    public static class TimePickerFragment extends DialogFragment {
        private TimePickerDialog.OnTimeSetListener listener;
        public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener) {
            super();
            this.listener = listener;
        }
        @Override
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
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // default values are the current date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), listener, year, month, day);
        }
    }

    public void makeRequest(Context context) {
        final String url = "api/v1/events";
        JSONObject event = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            event.put("name", "");
            event.put("start_at", "");
            event.put("end_at", "");
            event.put("desc", "");
            params.put("event", event);
        } catch(JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + url, params, response -> {
            try {
                UserRepository.getInstance().getLoggedInUser().setAuthToken(response.getString("token"));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (error.networkResponse != null) {
                Log.e("errrr", error.networkResponse.toString());
            }
            else {
                Log.e("er", error.toString());
            }
        });
        DataSource.getInstance().addToRequestQueue(request, context);
    }
    /*public void register(String email, String name, String permalink, String password,
                         Context context, RegisterViewModel registerViewModel) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                DataSource.getServerUrl() + registerUrl, userRegister,
                response -> {
                    try {
                        setLoggedInUser(new LoggedInUser(response.getString("name"), response.getString("token"), response.getString("permalink"), response.getString("email")));
                        registerViewModel.setResult(new Result<>(null, true));
                    } catch(JSONException e) {
                        e.printStackTrace();
                        registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                    }
                }, error -> {
            if (error.networkResponse != null) { // There was a problem with one of the user-provided inputs.
                if (error.networkResponse.statusCode == 422 && error.networkResponse.data != null) {
                    List<Integer> errorMessages = new ArrayList<>();
                    // The server should have sent a list of errors
                    try {
                        JSONObject response = new JSONObject(new String(error.networkResponse.data));
                        JSONObject errors = response.getJSONObject("errors");
                        JSONArray errorArray = errors.names();
                        if (errorArray == null) {
                            registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                            return;
                        }
                        for (int i = 0; i < errorArray.length(); i++) {
                            switch(errorArray.get(i).toString()) {
                                case "email":
                                    errorMessages.add(R.string.email_taken);
                                    break;
                                case "permalink":
                                    errorMessages.add(R.string.permalink_taken);
                                    break;
                                default:
                                    errorMessages.add(R.string.generic_input_error);
                                    break;
                            }
                        }
                        registerViewModel.setResult(new Result<>(errorMessages, false));

                    } catch (JSONException e) {
                        registerViewModel.setResult(new Result<>(R.string.server_response_error, false));
                    }

                }
                else {
                    registerViewModel.setResult(new Result<>(R.string.register_failed, false));
                }
            } else {
                registerViewModel.setResult(new Result<>(R.string.could_not_reach_server, false));
            }
        });
        DataSource.getInstance().addToRequestQueue(request, context);
    }*/
}

