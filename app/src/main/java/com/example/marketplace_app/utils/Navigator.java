package com.example.marketplace_app.utils;

import android.app.Activity;
import android.content.Intent;

import com.example.marketplace_app.activities.auth.LoginActivity;
import com.example.marketplace_app.activities.buyer.BuyerDashboardActivity;
import com.example.marketplace_app.activities.seller.SellerDashboardActivity;
import com.example.marketplace_app.models.User;

/**
 * Decides which screen to open. Splash, Login and Sign up all use it, so the
 * "buyer goes here, seller goes there" rule lives in exactly one place.
 */
public class Navigator {

    public static void goToDashboard(Activity from, String role) {
        Class<?> target = User.ROLE_SELLER.equals(role)
                ? SellerDashboardActivity.class
                : BuyerDashboardActivity.class;
        openAsNewStart(from, target);
    }

    public static void goToLogin(Activity from) {
        openAsNewStart(from, LoginActivity.class);
    }

    // CLEAR_TASK removes the old screens, so "Back" does not return to Login/Splash
    private static void openAsNewStart(Activity from, Class<?> target) {
        Intent intent = new Intent(from, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        from.startActivity(intent);
        from.finish();
    }
}
