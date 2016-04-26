package org.goldenroute.portfolioclient.signin;

public interface SignInListener {
    void onComplete(String provider);

    void onCancel(String provider);

    void onError(String provider, String message);
}
