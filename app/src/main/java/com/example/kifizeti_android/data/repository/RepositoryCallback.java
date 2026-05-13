package com.example.kifizeti_android.data.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);
    void onError(String message);
}
