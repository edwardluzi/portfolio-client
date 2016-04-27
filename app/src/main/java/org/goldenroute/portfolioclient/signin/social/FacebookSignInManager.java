package org.goldenroute.portfolioclient.signin.social;

import android.app.Activity;
import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.goldenroute.portfolioclient.signin.OAuth2Token;
import org.goldenroute.portfolioclient.signin.SignInListener;
import org.goldenroute.portfolioclient.signin.SignInManager;

public class FacebookSignInManager extends SignInManager {
    public static final String PROVIDER = "Facebook";

    private Activity mActivity;
    private AccessToken mAccessToken;
    private OAuth2Token mOAuth2Token;
    private CallbackManager mCallbackManager;

    public FacebookSignInManager(SignInListener listener, Activity activity) {
        super(listener);
        mActivity = activity;
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallbackLoginImpl());
    }

    public String getProvider() {
        return PROVIDER;
    }

    public void SignIn() {
        throw new UnsupportedOperationException("Not supported: sign in");
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    public boolean isValidToken() {
        return mAccessToken != null && !mAccessToken.isExpired();
    }

    public OAuth2Token getToken() {
        if (mAccessToken == null || mAccessToken.isExpired()) {
            return null;
        } else {
            if (mOAuth2Token == null) {
                mOAuth2Token = new OAuth2Token(mAccessToken.getUserId(), mAccessToken.getToken(), "", mAccessToken.getExpires().getTime());
            }
            return mOAuth2Token;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void saveToken() {
    }

    @Override
    public void loadToken() {
        mAccessToken = AccessToken.getCurrentAccessToken();
    }

    class FacebookCallbackLoginImpl implements FacebookCallback<LoginResult> {
        @Override
        public void onSuccess(LoginResult loginResult) {
            mAccessToken = loginResult.getAccessToken();
            FacebookSignInManager.this.getListener().onComplete(PROVIDER);
        }

        @Override
        public void onCancel() {
            FacebookSignInManager.this.getListener().onCancel(PROVIDER);
        }

        @Override
        public void onError(FacebookException e) {
            FacebookSignInManager.this.getListener().onError(PROVIDER, e.getMessage());
        }
    }
}

