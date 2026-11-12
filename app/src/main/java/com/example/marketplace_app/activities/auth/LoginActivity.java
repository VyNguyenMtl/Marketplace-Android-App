package com.example.marketplace_app.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace_app.R;
import com.example.marketplace_app.firebase.DataCallback;
import com.example.marketplace_app.firebase.FirebaseManager;
import com.example.marketplace_app.firebase.UserRepository;
import com.example.marketplace_app.models.User;
import com.example.marketplace_app.utils.Navigator;
import com.example.marketplace_app.utils.ValidationUtils;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private ProgressBar progressBar;

    private final FirebaseManager firebase = FirebaseManager.getInstance();
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        TextView signupLink = findViewById(R.id.signupLink);
        TextView forgotPasswordLink = findViewById(R.id.forgotPasswordLink);

        loginButton.setOnClickListener(v -> attemptLogin());
        forgotPasswordLink.setOnClickListener(v -> resetPassword());
        signupLink.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Remember user: If user is already logged in, automatically go to their dashboard
        String currentUid = firebase.getCurrentUid();
        if (currentUid != null) {
            setLoading(true);
            loadRoleAndContinue(currentUid);
        }
    }

    private void attemptLogin() {
        String email = emailInput.getText() == null ? "" : emailInput.getText().toString().trim();
        String password = passwordInput.getText() == null ? "" : passwordInput.getText().toString();

        // Validate fields
        boolean valid = true;
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Enter a valid email");
            valid = false;
        }
        if (!ValidationUtils.isNotEmpty(password)) {
            passwordInput.setError("Enter your password");
            valid = false;
        }
        if (!valid) {
            return;
        }

        // Authenticate with Firebase
        setLoading(true);
        firebase.getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    if (result.getUser() != null) {
                        loadRoleAndContinue(result.getUser().getUid());
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void resetPassword() {
        String email = emailInput.getText() == null ? "" : emailInput.getText().toString().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Enter your email to reset password");
            emailInput.requestFocus();
            return;
        }

        setLoading(true);
        firebase.getAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, "Password reset email sent! Check your inbox.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Reset failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void loadRoleAndContinue(String uid) {
        userRepository.getUser(uid, new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user == null) {
                    firebase.logout();
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, "No user profile found", Toast.LENGTH_LONG).show();
                    return;
                }
                Navigator.goToDashboard(LoginActivity.this, user.getRole());
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Error loading profile: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!loading);
    }
}
