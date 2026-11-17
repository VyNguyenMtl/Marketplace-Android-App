package com.example.marketplace_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketplace_app.R;
import com.example.marketplace_app.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An adapter connects a list of data to a RecyclerView:
 *  - onCreateViewHolder: inflate (build) ONE row layout
 *  - onBindViewHolder:   put the data of item #position into that row
 *  - getItemCount:       how many rows
 * RecyclerView re-uses the row views while you scroll, so it stays fast.
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    // Interface to handle user clicking on any product card
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products = new ArrayList<>();
    private final OnProductClickListener listener;

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    // Updates the adapter's product list and refreshes the RecyclerView UI
    public void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    // 1. Inflates (builds) ONE product row layout from item_product.xml
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    // 2. Fills ONE product row with name, description, and price at the given list position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.nameText.setText(product.getProductName());
        holder.descriptionText.setText(product.getDescription());
        holder.priceText.setText(String.format(Locale.US, "$%.2f", product.getPrice()));

        // When a user taps this product row, trigger onProductClick
        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    // 3. Returns the total number of products in the list
    @Override
    public int getItemCount() {
        return products.size();
    }

    // Holds references to the UI views inside item_product.xml to avoid repeated findViewById calls
    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView descriptionText;
        final TextView priceText;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.productName);
            descriptionText = itemView.findViewById(R.id.productDescription);
            priceText = itemView.findViewById(R.id.productPrice);
        }
    }
}
