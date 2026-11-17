package com.example.marketplace_app.firebase;

import androidx.annotation.NonNull;

import com.example.marketplace_app.models.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * ProductRepository: Handles reading product data from Firebase Realtime Database.
 */
public class ProductRepository {

    // Access the shared FirebaseManager instance
    private final FirebaseManager firebase = FirebaseManager.getInstance();

    /**
     * Listens live to the "Products/" node in Firebase.
     * Whenever a product is added, updated, or deleted in Firebase,
     * onDataChange() runs automatically and returns the updated product list.
     */
    public ValueEventListener listenToProducts(DataCallback<List<Product>> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Product> products = new ArrayList<>();
                // Loop through each product under the "Products" node in Firebase
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product product = child.getValue(Product.class);
                    if (product != null) {
                        // Set the unique Firebase key as the product ID
                        product.setProductId(child.getKey());
                        products.add(product);
                    }
                }
                // Send the full list back to ProductListFragment
                callback.onSuccess(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        };

        // Attach listener to Firebase Realtime Database at /Products node
        firebase.productsRef().addValueEventListener(listener);
        return listener;
    }

    /** Stops listening to Firebase when the Fragment/Activity is closed */
    public void stopListening(ValueEventListener listener) {
        if (listener != null) {
            firebase.productsRef().removeEventListener(listener);
        }
    }

    /** Reads a single product by ID (used when opening ProductDetailActivity) */
    public void getProduct(String productId, DataCallback<Product> callback) {
        firebase.productsRef().child(productId).get()
                .addOnSuccessListener(snapshot -> {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(snapshot.getKey());
                    }
                    callback.onSuccess(product);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
