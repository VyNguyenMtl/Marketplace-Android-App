package com.example.marketplace_app.activities.buyer;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketplace_app.R;
import com.example.marketplace_app.firebase.CartRepository;
import com.example.marketplace_app.firebase.DataCallback;
import com.example.marketplace_app.firebase.FirebaseManager;
import com.example.marketplace_app.firebase.ProductRepository;
import com.example.marketplace_app.models.CartItem;
import com.example.marketplace_app.models.Product;
import com.example.marketplace_app.utils.WindowUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    /** Key used to pass the product id from the list to this screen. */
    public static final String EXTRA_PRODUCT_ID = "extra_product_id";

    private final CartRepository cartRepository = new CartRepository();

    private Product product;
    private int quantity = 1;

    private TextView nameText, priceText, descriptionText, quantityText;
    private MaterialButton addToCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        WindowUtils.applyInsets(findViewById(R.id.main), findViewById(R.id.topBar));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        nameText = findViewById(R.id.detailName);
        priceText = findViewById(R.id.detailPrice);
        descriptionText = findViewById(R.id.detailDescription);
        quantityText = findViewById(R.id.quantityText);
        addToCartButton = findViewById(R.id.addToCartButton);

        findViewById(R.id.minusButton).setOnClickListener(v -> changeQuantity(-1));
        findViewById(R.id.plusButton).setOnClickListener(v -> changeQuantity(1));
        addToCartButton.setOnClickListener(v -> addToCart());

        String productId = getIntent().getStringExtra(EXTRA_PRODUCT_ID);
        if (productId == null) {
            finish();
            return;
        }
        loadProduct(productId);
    }

    private void loadProduct(String productId) {
        new ProductRepository().getProduct(productId, new DataCallback<Product>() {
            @Override
            public void onSuccess(Product loaded) {
                if (loaded == null) { // the seller may have deleted it
                    Toast.makeText(ProductDetailActivity.this,
                            "This product is no longer available", Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
                product = loaded;
                nameText.setText(loaded.getProductName());
                priceText.setText(String.format(Locale.US, "$%.2f", loaded.getPrice()));
                descriptionText.setText(loaded.getDescription());
                addToCartButton.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProductDetailActivity.this,
                        "Could not load product: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeQuantity(int change) {
        int newQuantity = quantity + change;
        if (newQuantity < 1 || newQuantity > 99) {
            return;
        }
        quantity = newQuantity;
        quantityText.setText(String.valueOf(quantity));
    }

    private void addToCart() {
        String uid = FirebaseManager.getInstance().getCurrentUid();
        if (product == null || uid == null) {
            return;
        }

        addToCartButton.setEnabled(false);
        CartItem item = new CartItem(product.getProductId(), product.getProductName(),
                product.getPrice(), product.getSellerId(), quantity);

        cartRepository.addToCart(uid, item, new DataCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Toast.makeText(ProductDetailActivity.this, "Added to cart",
                        Toast.LENGTH_SHORT).show();
                finish(); // back to the product list
            }

            @Override
            public void onError(String message) {
                addToCartButton.setEnabled(true);
                Toast.makeText(ProductDetailActivity.this,
                        "Could not add to cart: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
