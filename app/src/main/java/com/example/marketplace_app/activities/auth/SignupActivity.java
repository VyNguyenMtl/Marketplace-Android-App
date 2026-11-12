package com.example.marketplace_app.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

public class SignupActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmInput;
    private RadioButton radioSeller;
    private Button signupButton;
    private ProgressBar progressBar;

    private final FirebaseManager firebase = FirebaseManager.getInstance();
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmInput = findViewById(R.id.confirmInput);
        radioSeller = findViewById(R.id.radioSeller);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);
        TextView loginLink = findViewById(R.id.loginLink);

        signupButton.setOnClickListener(v -> attemptSignup());
        loginLink.setOnClickListener(v -> Navigator.goToLogin(SignupActivity.this));
    }

    private String textOf(EditText input) {
        return input.getText() == null ? "" : input.getText().toString();
    }

    private void attemptSignup() {
        String name = textOf(nameInput).trim();
        String email = textOf(emailInput).trim();
        String password = textOf(passwordInput);
        String confirm = textOf(confirmInput);
        String role = radioSeller.isChecked() ? User.ROLE_SELLER : User.ROLE_BUYER;

        // Form Validation
        boolean valid = true;
        if (!ValidationUtils.isNotEmpty(name)) {
            nameInput.setError("Enter your name");
            valid = false;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Enter a valid email");
            valid = false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            passwordInput.setError("At least " + ValidationUtils.MIN_PASSWORD_LENGTH + " characters");
            valid = false;
        }
        if (!ValidationUtils.passwordsMatch(password, confirm)) {
            confirmInput.setError("Passwords do not match");
            valid = false;
        }
        if (!valid) {
            return;
        }

        // Create Account in Firebase Authentication
        setLoading(true);
        firebase.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    if (result.getUser() != null) {
                        String uid = result.getUser().getUid();
                        saveProfile(new User(uid, name, email, role));
                    }
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void saveProfile(User user) {
        userRepository.saveUser(user, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                // Logout after creation so LoginActivity won't auto-redirect to Dashboard
                firebase.logout();
                Toast.makeText(SignupActivity.this, "Account created! Please log in.", Toast.LENGTH_SHORT).show();
                Navigator.goToLogin(SignupActivity.this);
            }

            @Override
            public void onError(String message) {
                setLoading(false);
                Toast.makeText(SignupActivity.this, "Could not save profile: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        signupButton.setEnabled(!loading);
    }
}
