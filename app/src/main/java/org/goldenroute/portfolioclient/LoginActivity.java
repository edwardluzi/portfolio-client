package org.goldenroute.portfolioclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import org.goldenroute.portfolioclient.rest.RestOperations;
import org.goldenroute.portfolioclient.signin.SignInListener;
import org.goldenroute.portfolioclient.signin.SignInManager;
import org.goldenroute.portfolioclient.signin.social.FacebookSignInManager;
import org.goldenroute.portfolioclient.signin.social.WeiboSignInManager;

import java.util.Hashtable;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener, SignInListener {

    private static final String SIGN_IN_PROVIDER = "sign_in_provider";

    private Hashtable<String, SignInManager> mSignInManagers = new Hashtable<>();

    @Bind(R.id.button_explore_as_a_guest)
    protected Button mButtonExplore;

    @Bind(R.id.image_button_weibo_sign_in)
    protected ImageButton mButtonWeiboSignIn;

    @Bind(R.id.image_button_facebook_sign_in)
    protected LoginButton mLoginButtonFacebookSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        SpannableString content = new SpannableString(this.getString(R.string.label_explore_as_a_guest));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mButtonExplore.setText(content);

        mButtonWeiboSignIn.setOnClickListener(this);
        mButtonExplore.setOnClickListener(this);

        mLoginButtonFacebookSignIn.setReadPermissions("public_profile");

        initialize();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String provider = sharedPreferences.getString(SIGN_IN_PROVIDER, "");
        if (mSignInManagers.containsKey(provider)) {
            SignInManager signInManager = mSignInManagers.get(provider);
            signInManager.loadToken();
            if (signInManager.isValidToken()) {
                gotoMain(signInManager, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_button_weibo_sign_in:
                onWeiboSignInClicked();
                break;

            case R.id.button_explore_as_a_guest:
                onExploreAsGuestClicked();
                break;
        }
    }

    @Override
    public void onComplete(String provider) {
        Toast.makeText(this,
                provider + " authorization succeeded.", Toast.LENGTH_SHORT).show();

        gotoMain(mSignInManagers.get(provider), true);
    }

    @Override
    public void onCancel(String provider) {
        Toast.makeText(this,
                provider + " authorization canceled.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String provider, String message) {
        Toast.makeText(this,
                provider + " authorization error : " + message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (SignInManager signInManager : mSignInManagers.values()) {
            signInManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onWeiboSignInClicked() {
        mSignInManagers.get(WeiboSignInManager.PROVIDER).SignIn();
    }

    private void onExploreAsGuestClicked() {
        Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show();
    }

    private void initialize() {
        mSignInManagers.put(WeiboSignInManager.PROVIDER, new WeiboSignInManager(LoginActivity.this, LoginActivity.this));
        mSignInManagers.put(FacebookSignInManager.PROVIDER, new FacebookSignInManager(LoginActivity.this, LoginActivity.this));
    }

    private ClientContext getClientContext() {
        return (ClientContext) this.getApplication();
    }

    private void gotoMain(SignInManager signInManage, boolean saveToken) {
        if (saveToken) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPreferences.edit().putString(SIGN_IN_PROVIDER, signInManage.getProvider()).apply();
            signInManage.saveToken();
        }

        this.getClientContext().setSignInManager(signInManage);
        RestOperations.initialize(this);
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
        LoginActivity.this.finish();
    }
}