package com.example.chillguy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chillguy.R;
import com.example.chillguy.helper.SharedPrefHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout   tilUsername, tilPassword;
    private TextInputEditText etUsername, etPassword;
    private TextView          tvError;
    private SharedPrefHelper  prefHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prefHelper  = new SharedPrefHelper(this);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        etUsername  = findViewById(R.id.etUsername);
        etPassword  = findViewById(R.id.etPassword);
        tvError     = findViewById(R.id.tvError);

        ImageButton    btnBack        = findViewById(R.id.btnBack);
        MaterialButton btnSignIn      = findViewById(R.id.btnSignIn);
        TextView       tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnBack.setOnClickListener(v -> finish());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnSignIn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        tvError.setVisibility(View.GONE);
        tilUsername.setError(null);
        tilPassword.setError(null);

        String username = etUsername.getText() != null ? etUsername.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString()        : "";

        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("Enter your username");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Enter your password");
            return;
        }

        if (!prefHelper.checkLogin(username, password)) {
            String savedUsername = prefHelper.getUsername();
            if (TextUtils.isEmpty(savedUsername)) {
                tvError.setText("No account found. Please register first.");
            } else {
                tvError.setText("Wrong username or password.");
            }
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        prefHelper.setLoggedIn(true);

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}