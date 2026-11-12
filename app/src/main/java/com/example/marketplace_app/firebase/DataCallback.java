package com.example.marketplace_app.firebase;

/**
 * Firebase answers later (it needs the internet), so repositories can't just
 * "return" data. Instead they call one of these two methods when they are done.
 */
public interface DataCallback<T> {
    void onSuccess(T data);

    void onError(String message);
}
