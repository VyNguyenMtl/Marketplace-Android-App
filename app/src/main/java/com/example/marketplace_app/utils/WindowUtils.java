package com.example.marketplace_app.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * With EdgeToEdge the app draws under the status bar and navigation bar.
 * This keeps the content out of those bars while letting the coloured top bar
 * extend behind the status bar (it looks nicer than a grey gap).
 */
public class WindowUtils {

    public static void applyInsets(View root, View topBar) {
        final int topBarPaddingTop = topBar.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, 0, bars.right, bars.bottom);
            topBar.setPadding(topBar.getPaddingLeft(), topBarPaddingTop + bars.top,
                    topBar.getPaddingRight(), topBar.getPaddingBottom());
            return insets;
        });
    }
}
