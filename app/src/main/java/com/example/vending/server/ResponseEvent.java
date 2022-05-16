package com.example.vending.server;

import androidx.annotation.Nullable;

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
public class ResponseEvent<T> {

    private final T mResponse;
    private boolean hasBeenHandled = false;

    public ResponseEvent(T response) {
        if (response == null) {
            throw new IllegalArgumentException("null values in Event are not allowed.");
        }
        mResponse = response;
    }

    @Nullable
    public T getResponseIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return mResponse;
        }
    }

    @SuppressWarnings("unused")
    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}