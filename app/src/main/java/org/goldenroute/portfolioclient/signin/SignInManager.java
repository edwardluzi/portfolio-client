package org.goldenroute.portfolioclient.signin;

import android.content.Intent;

public abstract class SignInManager {
    SignInListener mListener;

    public SignInManager(SignInListener listener) {
        mListener = listener;
    }

    public SignInListener getListener() {
        return mListener;
    }

    public abstract String getProvider();

    public abstract void SignIn();

    public abstract boolean isValidToken();

    public abstract OAuth2Token getToken();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void saveToken();

    public abstract void loadToken();
}
