package org.goldenroute.portfolioclient.signin;

public class OAuth2Token {
    private String mUser;
    private String mAccessToken;
    private String mRefreshToken;
    private Long mExpiresIn;

    public OAuth2Token(String user, String accessToken, String refreshToken, Long expiresIn) {
        mUser = user;
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
        mExpiresIn = expiresIn;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        mUser = user;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        mExpiresIn = expiresIn;
    }
}
