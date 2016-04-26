package org.goldenroute.portfolioclient.signin.social;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import org.goldenroute.portfolioclient.signin.OAuth2Token;
import org.goldenroute.portfolioclient.signin.SignInListener;
import org.goldenroute.portfolioclient.signin.SignInManager;

public class WeiboSignInManager extends SignInManager {
    public static final String PROVIDER = "Weibo";

    private Activity mActivity;
    private AuthInfo mAuthInfo;
    private Oauth2AccessToken mAccessToken;
    private OAuth2Token mOAuth2Token;
    private SsoHandler mSsoHandler;

    public WeiboSignInManager(SignInListener listener, Activity activity) {
        super(listener);
        mActivity = activity;
    }

    public String getProvider() {
        return PROVIDER;
    }

    public void SignIn() {
        mAuthInfo = new AuthInfo(mActivity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        mSsoHandler = new SsoHandler(mActivity, mAuthInfo);
        mSsoHandler.authorize(new WeiboAuthListenerImpl());
    }

    public boolean isValidToken() {
        return mAccessToken != null && mAccessToken.isSessionValid();
    }

    public OAuth2Token getToken() {
        if (mAccessToken == null) {
            return null;
        } else {
            if (mOAuth2Token == null) {
                mOAuth2Token = new OAuth2Token(mAccessToken.getUid(), mAccessToken.getToken(),
                        mAccessToken.getRefreshToken(), mAccessToken.getExpiresTime());
            }
            return mOAuth2Token;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public void saveToken() {
        if (isValidToken()) {
            WeiboTokenStore.writeAccessToken(mActivity, mAccessToken);
        }
    }

    public void loadToken() {
        mAccessToken = WeiboTokenStore.readAccessToken(mActivity);
    }

    interface Constants {
        String APP_KEY = "2081019661";
        String REDIRECT_URL = "http://grpm.applinzi.com/oauth2/weibo";
        String SCOPE = "email";
    }

    class WeiboAuthListenerImpl implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {

            mAccessToken = Oauth2AccessToken.parseAccessToken(values);

            if (mAccessToken.isSessionValid()) {
                WeiboSignInManager.this.getListener().onComplete(PROVIDER);
            } else {
                String code = values.getString("code");
                String message = TextUtils.isEmpty(code) ? "" : "Obtained the code - " + code;
                getListener().onError(PROVIDER, message);
            }
        }

        @Override
        public void onCancel() {
            WeiboSignInManager.this.getListener().onCancel(PROVIDER);
        }

        @Override
        public void onWeiboException(WeiboException e) {
            WeiboSignInManager.this.getListener().onError(PROVIDER, e.getMessage());
        }
    }
}
