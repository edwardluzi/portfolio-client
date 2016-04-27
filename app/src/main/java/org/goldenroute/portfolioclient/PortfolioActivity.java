package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.goldenroute.portfolioclient.fragment.PortfolioHoldingFragment;
import org.goldenroute.portfolioclient.fragment.PortfolioTransactionFragment;
import org.goldenroute.portfolioclient.fragment.RefreshableFragment;
import org.goldenroute.portfolioclient.model.Portfolio;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PortfolioActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    public static final String POSITION = "pos";
    public static final String ARG_PID = "pid";
    private static final int RC_ADD = 1;
    private static final int RC_EDIT = 2;

    private Long mPortfolioId;

    @Bind(R.id.toolbar_portfolio)
    protected Toolbar mToolbar;

    @Bind(R.id.view_pager_portfolio)
    protected ViewPager mViewPager;

    @Bind(R.id.sliding_tabs_portfolio)
    protected TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mViewPager.setAdapter(new PortfolioFragmentPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        mTabLayout.setupWithViewPager(mViewPager);
        mPortfolioId = getIntent().getExtras().getLong(ARG_PID);

        setTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portfolio, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }

    @Override
    public void onPageSelected(int position) {
        refresh();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_transaction) {
            Intent intent = new Intent(this, CreateTransactionActivity.class);
            intent.putExtra(CreateTransactionActivity.ARG_PID, mPortfolioId);
            intent.putExtra(CreateTransactionActivity.ARG_TID, Long.valueOf(0));
            startActivityForResult(intent, RC_ADD);
            return true;
        } else if (id == R.id.action_edit_portfolio) {
            Intent intent = new Intent(this, CreatePortfolioListActivity.class);
            intent.putExtra(CreatePortfolioListActivity.ARG_PID, mPortfolioId);
            startActivityForResult(intent, RC_EDIT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                refresh();
            }
        } else if (requestCode == RC_EDIT) {
            mToolbar.post(new Runnable() {
                public void run() {
                    setTitle();
                }
            });
        }
    }

    private void refresh() {
        RefreshableFragment refreshableFragment = (RefreshableFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.view_pager_portfolio + ":" + mViewPager.getCurrentItem());
        if (refreshableFragment != null) {
            refreshableFragment.refresh();
        }
    }

    private void setTitle() {
        Portfolio portfolio = getClientContext().getAccount().find(mPortfolioId);
        if (portfolio != null) {
            this.setTitle(portfolio.getName());
            mToolbar.setTitle(portfolio.getName());
        }
    }

    private ClientContext getClientContext() {
        return (ClientContext) this.getApplication();
    }


    public class PortfolioFragmentPagerAdapter extends FragmentPagerAdapter {
        private static final int NUM_ITEMS = 2;

        public PortfolioFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PortfolioHoldingFragment.newInstance(mPortfolioId);
                case 1:
                    return PortfolioTransactionFragment.newInstance(mPortfolioId);
                default:
                    return null;
            }
        }

        private final int[] mPageTitleResourceIds = {
                R.string.title_portfolio_holdings,
                R.string.title_portfolio_transactions
        };

        @Override
        public CharSequence getPageTitle(int position) {
            return PortfolioActivity.this.getString(mPageTitleResourceIds[position]);
        }
    }
}
