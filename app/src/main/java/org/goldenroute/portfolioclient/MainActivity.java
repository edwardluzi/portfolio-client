package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.goldenroute.portfolioclient.fragment.MainAlertsFragment;
import org.goldenroute.portfolioclient.fragment.MainBaseFragment;
import org.goldenroute.portfolioclient.fragment.MainListsFragment;
import org.goldenroute.portfolioclient.fragment.MainMarketFragment;
import org.goldenroute.portfolioclient.fragment.MainPortfolioFragment;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;
import org.goldenroute.portfolioclient.utils.TopExceptionHandler;

import java.io.IOException;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    @Bind(R.id.toolbar_portfolio)
    protected Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawer;

    @Bind(R.id.nav_view)
    protected NavigationView mNavigationView;

    protected ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mDrawerToggle = createDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
        }

        RestOperations.initialize(this);
        new ReadingAccountTask(this).execute((Void) null);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Class fragmentClass;

        switch (item.getItemId()) {
            case R.id.nav_lists:
                fragmentClass = MainListsFragment.class;
                break;
            case R.id.nav_market:
                fragmentClass = MainMarketFragment.class;
                break;
            case R.id.nav_alerts:
                fragmentClass = MainAlertsFragment.class;
                break;
            default:
                fragmentClass = MainPortfolioFragment.class;
                break;
        }
        Fragment fragment = null;

        try {
            fragment = (MainBaseFragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_main_fragment_container, fragment).commit();
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ActionBarDrawerToggle createDrawerToggle() {
        return new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    private void refresh(Account account) {
        ClientContext.getInstance().setAccount(account);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main_fragment_container);
        if (fragment instanceof MainBaseFragment)
            ((MainBaseFragment) fragment).refresh();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class ReadingAccountTask extends RestAsyncTask<Void, Void, Boolean> {
        private Account mAccount;

        public ReadingAccountTask(Activity activity) {
            super(activity, true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<Account> call = RestOperations.getInstance().getAccountService().getAccount(0L);
                Response<Account> response = call.execute();
                mAccount = response.body();
                if (!response.isSuccessful() || mAccount == null) {
                    parseError(response);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                mAccount = null;
                parseError(e);
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if (success && mAccount != null) {
                refresh(mAccount);
            } else {
                Toast.makeText(this.getParentActivity(),
                        String.format(Locale.getDefault(), getString(R.string.message_retrieving_account_failed), getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}

