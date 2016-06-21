package org.goldenroute.portfolioclient.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.goldenroute.portfolioclient.ClientContext;
import org.goldenroute.portfolioclient.CreatePortfolioListActivity;
import org.goldenroute.portfolioclient.IntentConstants;
import org.goldenroute.portfolioclient.PortfolioActivity;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.adapter.PortfolioListAdapter;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class MainPortfolioFragment extends MainBaseFragment implements ListView.OnItemClickListener {
    private static final String TAG = MainPortfolioFragment.class.getName();

    @Bind(R.id.list_view_portfolios)
    protected ListView mListViewPortfolios;

    @Bind(R.id.list_view_portfolio_total)
    protected ListView mListViewTotal;

    private PortfolioListAdapter mPortfolioListAdapter;
    private PortfolioListAdapter mTotalListAdapter;

    public MainPortfolioFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_main_portfolio, container, false);
        ButterKnife.bind(this, fragment);

        mPortfolioListAdapter = new PortfolioListAdapter(getActivity(), new ArrayList<Portfolio>());
        mListViewPortfolios.setAdapter(mPortfolioListAdapter);
        mListViewPortfolios.setOnItemClickListener(this);

        mTotalListAdapter = new PortfolioListAdapter(getActivity(), new ArrayList<Portfolio>());
        mListViewTotal.setAdapter(mTotalListAdapter);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        setTitle(getString(R.string.label_nav_portfolios));

        mListViewPortfolios.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = mListViewPortfolios.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(String.format(Locale.getDefault(), getString(R.string.label_items_selected), checkedCount));
                // Calls toggleSelection method from ListViewAdapter Class
                mPortfolioListAdapter.toggleSelection(id);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(getString(R.string.message_confirm_for_deleting));
                        builder.setTitle(getString(R.string.title_delete_portfolio));
                        builder.setPositiveButton(getString(R.string.label_delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new DeletingPortfolioTask(getActivity(), mPortfolioListAdapter.getSelectedIds()).execute((Void) null);
                            }
                        });
                        builder.setNegativeButton(getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                        mode.finish();
                        showToolbar(true);
                        return true;
                    }
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.delete, menu);
                mPortfolioListAdapter.removeSelection();
                showToolbar(false);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                showToolbar(true);
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
        });

        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.portfolios, menu);
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemProfile = menu.findItem(R.id.action_profile);
        MenuItem menuItemAddPortfolio = menu.findItem(R.id.action_add_portfolio);

        if (ClientContext.getInstance().getAccount() != null) {
            menuItemProfile.setEnabled(true);
            menuItemAddPortfolio.setEnabled(true);
        } else {
            menuItemProfile.setEnabled(false);
            menuItemAddPortfolio.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_portfolio) {
            Intent intent = new Intent(getActivity(), CreatePortfolioListActivity.class);
            intent.putExtra(IntentConstants.ARG_TYPE, 0L);
            intent.putExtra(IntentConstants.ARG_PID, 0L);
            startActivityForResult(intent, IntentConstants.RC_ADD_PORTFOLIO);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Portfolio portfolio = mPortfolioListAdapter.getData().get(position);
        Intent intent = new Intent(getActivity(), PortfolioActivity.class);
        intent.putExtra(IntentConstants.ARG_PID, portfolio.getId());
        startActivityForResult(intent, IntentConstants.RC_EDIT_PORTFOLIO);
    }

    @Override
    public void refresh() {
        mListViewPortfolios.post(new Runnable() {
            public void run() {
                Account account = ClientContext.getInstance().getAccount();
                if (account != null) {
                    List<Portfolio> portfolios = account.getPortfolios();
                    mPortfolioListAdapter.getData().clear();

                    if (portfolios != null) {
                        mPortfolioListAdapter.getData().addAll(portfolios);
                    }

                    double value = 0;
                    double cost = 0;
                    double dailyChange = 0;
                    double totalChange = 0;

                    double dailyChangePercentage = 0;
                    double totalChangePercentage = 0;

                    if(portfolios != null) {
                        for (Portfolio p : portfolios) {
                            value += p.getValue() != null ? p.getValue().doubleValue() : 0;
                            cost += p.getCost() != null ? p.getCost().doubleValue() : 0;
                            dailyChange += p.getDailyChange() != null ? p.getDailyChange().doubleValue() : 0;
                            totalChange += p.getTotalChange() != null ? p.getTotalChange().doubleValue() : 0;
                        }
                    }

                    if (cost > 0.001) {
                        totalChangePercentage = totalChange / cost;
                        dailyChangePercentage = dailyChange / (value - dailyChange);
                    }

                    List<Portfolio> totals = new ArrayList<>();
                    Portfolio total = new Portfolio(0L);
                    total.setName("Total");
                    total.setCost(BigDecimal.valueOf(cost));
                    total.setValue(BigDecimal.valueOf(value));
                    total.setDailyChange(BigDecimal.valueOf(dailyChange));
                    total.setTotalChange(BigDecimal.valueOf(totalChange));
                    total.setDailyChangePercentage(BigDecimal.valueOf(dailyChangePercentage));
                    total.setTotalChangePercentage(BigDecimal.valueOf(totalChangePercentage));

                    totals.add(total);

                    mTotalListAdapter.getData().clear();
                    mTotalListAdapter.getData().addAll(totals);
                }
                mPortfolioListAdapter.notifyDataSetChanged();
                mTotalListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentConstants.RC_ADD_PORTFOLIO) {
            if (resultCode == Activity.RESULT_OK) {
                Long portfolioId = data.getLongExtra(IntentConstants.ARG_PID, 0L);

                if (portfolioId != 0) {
                    Intent intent = new Intent(getActivity(), PortfolioActivity.class);
                    intent.putExtra(IntentConstants.ARG_PID, portfolioId);
                    startActivityForResult(intent, IntentConstants.RC_EDIT_PORTFOLIO);
                }
            }
        } else if (requestCode == IntentConstants.RC_EDIT_PORTFOLIO) {
            refresh();
        }
    }

    private void showToolbar(boolean show) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            ActionBar toolbar = activity.getSupportActionBar();
            if (toolbar != null) {
                if (show) {
                    toolbar.show();
                } else {
                    toolbar.hide();
                }
            }
        }
    }

    public class DeletingPortfolioTask extends RestAsyncTask<Void, Void, Boolean> {
        private Set<Long> mPortfolioIds;
        private Boolean mResult;

        public DeletingPortfolioTask(Activity activity, Set<Long> pids) {
            super(activity, true);
            mPortfolioIds = pids;
            mResult = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<Boolean> call = RestOperations.getInstance().getPortfolioService().delete(TextUtils.join(",", mPortfolioIds));
                Response<Boolean> response = call.execute();
                if (response.isSuccessful()) {
                    mResult = response.body();
                }
                if (mResult == null || !mResult) {
                    parseError(response);
                    mResult = false;
                }
            } catch (Exception e) {
                mResult = false;
                parseError(e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mPortfolioListAdapter.removeSelection();

            if (success && mResult) {
                Toast.makeText(getContext(), getString(R.string.message_deleting_portfolio_succeeded), Toast.LENGTH_LONG).show();
                ClientContext.getInstance().getAccount().remove(mPortfolioIds);
                refresh();
            } else {
                Toast.makeText(getContext(),
                        String.format(Locale.getDefault(), getString(R.string.message_deleting_portfolio_failed), getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
