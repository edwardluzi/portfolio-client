package org.goldenroute.portfolioclient;

import android.app.Application;
import android.content.Intent;

import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.signin.SignInManager;

public class ClientContext extends Application {
    private SignInManager mSignInManager;
    private Account mAccount;

    public SignInManager getSignInManager() {
        return mSignInManager;
    }

    public void setSignInManager(SignInManager signInManager) {
        this.mSignInManager = signInManager;
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account mAccount) {
        this.mAccount = mAccount;
    }
}
