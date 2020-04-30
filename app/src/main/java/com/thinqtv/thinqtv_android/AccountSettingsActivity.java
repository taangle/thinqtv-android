package com.thinqtv.thinqtv_android;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.thinqtv.thinqtv_android.data.UserRepository;

import androidx.appcompat.app.AppCompatActivity;

public class AccountSettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        EditText email = findViewById(R.id.email);
        EditText currentPassword = findViewById(R.id.current_password);
        EditText newPassword = findViewById(R.id.new_password);
        EditText newPasswordConfirm = findViewById(R.id.new_password_confirm);
        EditText permalink = findViewById(R.id.permalink);
        Button sendButton = findViewById(R.id.send);
        Context context = this;
        sendButton.setOnClickListener(view -> {
            if (!currentPassword.getText().toString().equals("")) {
                UserRepository.getInstance().updateAccount(context, email.getText().toString(), currentPassword.getText().toString(),
                        newPassword.getText().toString(), newPasswordConfirm.getText().toString(), permalink.getText().toString());
            }
        });
    }
}
