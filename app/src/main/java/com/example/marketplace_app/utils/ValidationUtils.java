package com.example.marketplace_app.utils;

import android.util.Patterns;

/** Small checks used by the Login and Sign up forms. */
public class ValidationUtils {

    // Firebase Auth refuses passwords shorter than 6 characters
    public static final int MIN_PASSWORD_LENGTH = 6;

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static boolean passwordsMatch(String password, String confirm) {
        return password != null && password.equals(confirm);
    }
}
