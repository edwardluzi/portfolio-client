package org.goldenroute.portfolioclient.signin;

public class OAuth2Token {
    private String mUser;
    private String mAccessToken;
    private String mRefreshToken;
    private Long mExpiresIn;

    public OAuth2Token(String user, String accessToken, String refreshToken, Long expiresIn) {
        this.mUser = user;
        this.mAccessToken = accessToken;
        this.mRefreshToken = refreshToken;
        this.mExpiresIn = expiresIn;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String user) {
        this.mUser = user;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        this.mAccessToken = accessToken;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }

    public Long getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.mExpiresIn = expiresIn;
    }
}
