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
import org.goldenroute.portfolioclient.PortfolioActivity;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.adapter.PortfolioListAdapter;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;

public class MainPortfolioFragment extends MainBaseFragment implements ListView.OnItemClickListener {

    private static final int RC_ADD = 1;
    private static final int RC_EDIT = 2;

    @Bind(R.id.list_view_portfolios)
    protected ListView mListViewPortfolios;

    private PortfolioListAdapter mPortfolioListAdapter;

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

        mPortfolioListAdapter = new PortfolioListAdapter(this.getActivity(), new ArrayList<Portfolio>());
        mListViewPortfolios.setAdapter(mPortfolioListAdapter);
        mListViewPortfolios.setOnItemClickListener(this);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mListViewPortfolios.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = mListViewPortfolios.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                mPortfolioListAdapter.toggleSelection(id);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure to delete these portfolios？");
                        builder.setTitle("Delete Portfolios");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new DeletePortfolioTask(getActivity(), mPortfolioListAdapter.getSelectedIds()).execute((Void) null);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        inflater.inflate(R.menu.main_portfolios, menu);
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_portfolio) {
            Intent intent = new Intent(getActivity(), CreatePortfolioListActivity.class);
            intent.putExtra(CreatePortfolioListActivity.ARG_TYPE, 0L);
            intent.putExtra(CreatePortfolioListActivity.ARG_PID, 0L);
            startActivityForResult(intent, RC_ADD);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Portfolio portfolio = mPortfolioListAdapter.getData().get(position);
        Intent intent = new Intent(getActivity(), PortfolioActivity.class);
        intent.putExtra(PortfolioActivity.ARG_PID, portfolio.getId());
        startActivityForResult(intent, RC_EDIT);
    }

    @Override
    public void refresh() {
        mListViewPortfolios.post(new Runnable() {
            public void run() {
                Account account = getClientContext().getAccount();
                if (account != null) {
                    List<Portfolio> portfolios = account.getPortfolios();
                    mPortfolioListAdapter.getData().clear();

                    if (portfolios != null)
                        mPortfolioListAdapter.getData().addAll(portfolios);
                }
                mPortfolioListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_ADD) {
            if (resultCode == Activity.RESULT_OK) {
                Long portfolioId = data.getLongExtra(CreatePortfolioListActivity.ARG_PID, 0L);

                if (portfolioId != 0) {
                    Intent intent = new Intent(getActivity(), PortfolioActivity.class);
                    intent.putExtra(PortfolioActivity.ARG_PID, portfolioId);
                    this.startActivityForResult(intent, RC_EDIT);
                }
            }
        } else if (requestCode == RC_EDIT) {
            refresh();
        }
    }

    private ClientContext getClientContext() {
        return (ClientContext) this.getActivity().getApplication();
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

    public class DeletePortfolioTask extends RestAsyncTask<Void, Void, Boolean> {
        private Set<Long> pids;
private Boolean result;
        public DeletePortfolioTask(Activity activity, Set<Long> pids) {
            super(activity, true);
            this.pids = pids;
            this.result = false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<Boolean> call = RestOperations.getInstance().getPortfolioService().delete(getClientContext().getAccount().getId(), TextUtils.join(",", this.pids));
                this.result= call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mPortfolioListAdapter.removeSelection();

            if (success && this.result) {
                getClientContext().getAccount().remove(this.pids);
                refresh();
            } else {
                Toast.makeText(this.getParentActivity(), "Failed to retrieve account information from server.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
