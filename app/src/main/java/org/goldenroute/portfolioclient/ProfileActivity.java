package org.goldenroute.portfolioclient;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Profile;

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

        Account account =getClientContext().getAccount();

        if(account != null)
        {
            Profile profile = account.getProfile();
            mEditTextUserId.setText(profile.getId().toString());
            mEditTextScreenName.setText(profile.getScreenName());
            mEditTextLocation.setText(profile.getLocaltion());

            Picasso.with(this)
                    .load(profile.getAvatarUrl())
                    .placeholder(R.drawable.user_place_holder)
                    .error(R.drawable.user_place_holder)
                    .into(mImageViewAvatar);
        }
    }




    private class HttpRequestTask extends AsyncTask<Void, Void, Account> {
        @Override
        protected Account doInBackground(Void... params) {
            try {
   /*             final String url = "http://10.35.12.99:8080/api/account/1";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

                Account account = restTemplate.getForObject(url, Account.class);
*/                return null;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Account greeting) {

       //     ProfileActivity.this.setTitle(greeting.getSocialId());
            //       TextView greetingIdText = (TextView) findViewById(R.id.id_value);
            //     TextView greetingContentText = (TextView) findViewById(R.id.content_value);
            //    greetingIdText.setText(greeting.getId());
            //    greetingContentText.setText(greeting.getContent());
        }

    }

}
