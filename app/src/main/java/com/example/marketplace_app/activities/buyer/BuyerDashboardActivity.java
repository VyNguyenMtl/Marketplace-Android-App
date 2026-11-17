package com.example.marketplace_app.activities.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace_app.R;
import com.example.marketplace_app.firebase.DataCallback;
import com.example.marketplace_app.firebase.FirebaseManager;
import com.example.marketplace_app.firebase.UserRepository;
import com.example.marketplace_app.models.User;
import com.example.marketplace_app.utils.Navigator;
import com.example.marketplace_app.utils.WindowUtils;

/**
 * Buyer home. The product list itself is the ProductListFragment declared in
 * activity_buyer_dashboard.xml; this activity only handles the greeting and
 * the buttons to the other buyer screens.
 */
public class BuyerDashboardActivity extends AppCompatActivity {

    private final FirebaseManager firebase = FirebaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buyer_dashboard);
        WindowUtils.applyInsets(findViewById(R.id.main), findViewById(R.id.topBar));

        String uid = firebase.getCurrentUid();
        if (uid == null) {
            Navigator.goToLogin(this);
            return;
        }

        TextView greetingText = findViewById(R.id.greetingText);
        new UserRepository().getUser(uid, new DataCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (user != null && user.getName() != null) {
                    greetingText.setText("Hello, " + user.getName());
                }
            }

            @Override
            public void onError(String message) {
            }
        });

        findViewById(R.id.cartButton).setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
        findViewById(R.id.ordersButton).setOnClickListener(v ->
                startActivity(new Intent(this, OrdersActivity.class)));
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            firebase.logout();
            Navigator.goToLogin(this);
        });
    }
}
