package org.goldenroute.portfolioclient.signin.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;


public class WeiboTokenStore {

    private static final String KEY_UID = "uid";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
        if (null == context || null == token) {
            return;
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(KEY_UID, token.getUid());
        editor.putString(KEY_ACCESS_TOKEN, token.getToken());
        editor.putString(KEY_REFRESH_TOKEN, token.getRefreshToken());
        editor.putLong(KEY_EXPIRES_IN, token.getExpiresTime());
        editor.apply();
    }

    public static Oauth2AccessToken readAccessToken(Context context) {
        if (null == context) {
            return null;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Oauth2AccessToken token = new Oauth2AccessToken();

        token.setUid(sharedPreferences.getString(KEY_UID, ""));
        token.setToken(sharedPreferences.getString(KEY_ACCESS_TOKEN, ""));
        token.setRefreshToken(sharedPreferences.getString(KEY_REFRESH_TOKEN, ""));
        token.setExpiresTime(sharedPreferences.getLong(KEY_EXPIRES_IN, 0));

        return token;
    }

    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }
}
