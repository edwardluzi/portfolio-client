package org.goldenroute.portfolioclient.services;

public interface CallbackListener<T> {
    void onSuccess(T data);

    void onFailure(String message);
}
