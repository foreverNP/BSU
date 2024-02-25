package com.example.project1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button btnLogin;
    private SharedPreferences authPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authPrefs = getSharedPreferences("auth_settings", MODE_PRIVATE);

        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.buttonLogin);

        if (authPrefs.contains("email") && authPrefs.contains("password")) {
            startActivity(new Intent(LoginActivity.this, GameActivity.class));
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = authPrefs.edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, GameActivity.class));
                    finish();
                }
            }
        });
    }
}
