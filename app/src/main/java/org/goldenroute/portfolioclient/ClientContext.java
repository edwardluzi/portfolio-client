package org.goldenroute.portfolioclient;

import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.signin.SignInManager;

public class ClientContext {
    private static ClientContext mInstance = null;

    public static synchronized ClientContext getInstance() {
        if (mInstance == null) {
            mInstance = new ClientContext();
        }
        return mInstance;
    }

    private SignInManager mSignInManager;
    private Account mAccount;

    public SignInManager getSignInManager() {
        return mSignInManager;
    }

    public void setSignInManager(SignInManager signInManager) {
        mSignInManager = signInManager;
    }

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        mAccount = account;
    }
}
