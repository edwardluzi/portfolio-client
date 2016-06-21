package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Profile;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;
import org.goldenroute.portfolioclient.signin.SignInManager;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = ProfileActivity.class.getName();

    @Bind(R.id.toolbar_profile)
    protected Toolbar mToolbar;

    @Bind(R.id.image_view_avatar)
    protected ImageView mImageViewAvatar;

    @Bind(R.id.edit_text_profile_user_id)
    protected EditText mEditTextUserId;

    @Bind(R.id.edit_text_profile_screen_name)
    protected EditText mEditTextScreenName;

    @Bind(R.id.edit_text_profile_location)
    protected EditText mEditTextLocation;

    @Bind(R.id.button_bind_to_wechat)
    protected Button mButtonBindToWechat;

    @Bind(R.id.button_logout_and_exit)
    protected Button mButtonLogoutAndExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mButtonBindToWechat.setOnClickListener(this);
        mButtonLogoutAndExit.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Account account = ClientContext.getInstance().getAccount();
        if (account != null) {
            Profile profile = account.getProfile();

            mEditTextUserId.setText(String.format(Locale.getDefault(), "%d", profile.getId()));
            mEditTextScreenName.setText(profile.getScreenName());
            mEditTextLocation.setText(profile.getLocation());

            Picasso.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.iv_user_place_holder)
                    .error(R.drawable.iv_user_place_holder)
                    .into(mImageViewAvatar);

            if (profile.getWechatId() != null && profile.getWechatId().length() > 0) {
                mButtonBindToWechat.setVisibility(View.GONE);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_logout_and_exit:
                logout();
                break;
            case R.id.button_bind_to_wechat:
                bindToWechat();
                break;
        }
    }

    private void logout() {
        SignInManager signInManager = ClientContext.getInstance().getSignInManager();
        signInManager.logout();
        setResult(RESULT_OK, new Intent());
        finish();
    }


    private void bindToWechat() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_bing_to_webchat, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.label_bind_to_wechat));
        alertDialogBuilder.setView(promptView);
        final EditText userInput = (EditText) promptView
                .findViewById(R.id.edit_text_wechat_binding_key);

        alertDialogBuilder.setPositiveButton(getString(R.string.label_okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String text = userInput.getText().toString();
                if (text.length() > 0) {
                    int key = Integer.parseInt(text);
                    new BindingAccountTask(ProfileActivity.this, key).execute((Void) null);
                }
            }
        })
                .setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        alertDialogBuilder.create().show();
    }

    public class BindingAccountTask extends RestAsyncTask<Void, Void, Boolean> {

        private Boolean mResult;
        private Integer mParameter;

        public BindingAccountTask(Activity activity, Integer parameter) {
            super(activity, true);
            mParameter = parameter;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Profile profile = ClientContext.getInstance().getAccount().getProfile();
                Call<Boolean> call = RestOperations.getInstance().getProfileService().bindWechat(profile.getId(), mParameter);
                Response<Boolean> response = call.execute();
                if (response.isSuccessful()) {
                    mResult = response.body();
                }
                if (mResult == null) {
                    parseError(response);
                }
            } catch (Exception e) {
                mResult = null;
                parseError(e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if (success && mResult != null && mResult) {
                Toast.makeText(getContext(),
                        getString(R.string.message_binding_account_succeeded), Toast.LENGTH_LONG).show();

                mButtonBindToWechat.setVisibility(View.GONE);
            } else {
                Toast.makeText(getContext(),
                        String.format(Locale.getDefault(),
                                getString(R.string.message_binding_account_failed),
                                getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
