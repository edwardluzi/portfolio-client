package org.goldenroute.portfolioclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Profile;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProfileActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    private ClientContext getClientContext() {
        return (ClientContext) this.getApplication();
    }


    @Override
    protected void onStart() {
        super.onStart();

        Account account = getClientContext().getAccount();

        if (account != null) {
            Profile profile = account.getProfile();

            mEditTextUserId.setText(String.format(Locale.getDefault(), "%d", profile.getId()));
            mEditTextScreenName.setText(profile.getScreenName());
            mEditTextLocation.setText(profile.getLocation());

            Picasso.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.user_place_holder)
                    .error(R.drawable.user_place_holder)
                    .into(mImageViewAvatar);
        }
    }
}
