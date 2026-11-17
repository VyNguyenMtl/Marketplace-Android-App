package com.example.marketplace_app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace_app.R;
import com.example.marketplace_app.activities.buyer.ProductDetailActivity;
import com.example.marketplace_app.adapters.ProductAdapter;
import com.example.marketplace_app.firebase.DataCallback;
import com.example.marketplace_app.firebase.ProductRepository;
import com.example.marketplace_app.models.Product;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * ProductListFragment displays the live list of products from Firebase
 * and allows buyers to search products in real time.
 */
public class ProductListFragment extends Fragment {

    private final ProductRepository productRepository = new ProductRepository();
    private ValueEventListener productsListener;

    private final List<Product> allProducts = new ArrayList<>();
    private ProductAdapter adapter;
    private TextInputEditText searchInput;
    private TextView emptyText;
    private ProgressBar progressBar;

    public ProductListFragment() {
        // Required empty public constructor
    }

    // 1. Inflates the Fragment layout (fragment_product_list.xml)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    // 2. Binds UI views, sets up RecyclerView adapter, and starts listening to Firebase
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchInput = view.findViewById(R.id.searchInput);
        emptyText = view.findViewById(R.id.emptyText);
        progressBar = view.findViewById(R.id.progressBar);
        RecyclerView recycler = view.findViewById(R.id.productsRecycler);

        // Setup RecyclerView Adapter with a click listener to open ProductDetailActivity
        adapter = new ProductAdapter(this::openProductDetail);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);

        // Real-time search filter: filters the product list as the user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Start listening live to Firebase Products node
        productsListener = productRepository.listenToProducts(new DataCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                allProducts.clear();
                allProducts.addAll(products);
                progressBar.setVisibility(View.GONE); // Hide loading spinner
                applyFilter(); // Display products matching current search
            }

            @Override
            public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Could not load products: " + message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Filters allProducts based on what the user typed in searchInput
    private void applyFilter() {
        String query = searchInput.getText() == null
                ? "" : searchInput.getText().toString().trim().toLowerCase(Locale.ROOT);

        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            String name = product.getProductName() == null
                    ? "" : product.getProductName().toLowerCase(Locale.ROOT);
            String description = product.getDescription() == null
                    ? "" : product.getDescription().toLowerCase(Locale.ROOT);
            if (query.isEmpty() || name.contains(query) || description.contains(query)) {
                filtered.add(product);
            }
        }

        adapter.setProducts(filtered);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // Opens ProductDetailActivity when a user clicks on a product card
    private void openProductDetail(Product product) {
        Intent intent = new Intent(requireContext(), ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }

    // Stops Firebase listener when Fragment is destroyed to prevent memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        productRepository.stopListening(productsListener);
        productsListener = null;
    }
}
