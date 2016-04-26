package org.goldenroute.portfolioclient.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.goldenroute.portfolioclient.ClientContext;
import org.goldenroute.portfolioclient.CreateTransactionActivity;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.adapter.TransactionListAdapter;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.model.Transaction;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;


public class PortfolioTransactionFragment extends RefreshableFragment implements ListView.OnItemClickListener {
    public static final String ARG_PID = "pid";
    private static final int RC_EDIT = 101;
    private static final String TAG = PortfolioTransactionFragment.class.getName();

    private Long mPortfolioId;

    @Bind(R.id.list_view_transactions)
    protected ListView mListViewTransactions;

    private TransactionListAdapter mTransactionListAdapter;

    public PortfolioTransactionFragment() {
    }

    public static PortfolioTransactionFragment newInstance(Long portfolioId) {
        PortfolioTransactionFragment fragment = new PortfolioTransactionFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PID, portfolioId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPortfolioId = getArguments().getLong(ARG_PID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_portfolio_transaction, container, false);
        ButterKnife.bind(this, fragment);

        mTransactionListAdapter = new TransactionListAdapter(this.getActivity(), new ArrayList<Transaction>());
        mListViewTransactions.setAdapter(mTransactionListAdapter);
        mListViewTransactions.setOnItemClickListener(this);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        mListViewTransactions.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode,
                                                  int position, long id, boolean checked) {
                // Capture total checked items
                final int checkedCount = mListViewTransactions.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                mTransactionListAdapter.toggleSelection(id);
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete: {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure to delete these transactions？");
                        builder.setTitle("Delete Portfolios");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new DeleteTransactionTask(getActivity(), mTransactionListAdapter.getSelectedIds()).execute((Void) null);
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
                mTransactionListAdapter.removeSelection();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Transaction transaction = mTransactionListAdapter.getData().get(position);
        Intent intent = new Intent(getActivity(), CreateTransactionActivity.class);
        intent.putExtra(CreateTransactionActivity.ARG_PID, mPortfolioId);
        intent.putExtra(CreateTransactionActivity.ARG_TID, transaction.getId());
        startActivityForResult(intent, RC_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_EDIT) {
            refresh();
        }
    }

    @Override
    public void refresh() {
        mListViewTransactions.post(new Runnable() {
            public void run() {
                Account account = getClientContext().getAccount();
                if (account != null) {
                    Portfolio portfolio = account.find(mPortfolioId);
                    if (portfolio != null) {
                        List<Transaction> transactions = portfolio.getTransactions();
                        mTransactionListAdapter.getData().clear();
                        if (transactions != null) {
                            mTransactionListAdapter.getData().addAll(transactions);
                        }
                        mTransactionListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
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

    public class DeleteTransactionTask extends RestAsyncTask<Void, Void, Boolean> {
        private Set<Long> tids;
        private Portfolio mReturned;

        public DeleteTransactionTask(Activity activity, Set<Long> tids) {
            super(activity, true);
            this.tids = tids;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<Portfolio> call = RestOperations.getInstance().getTransactionService().delete(mPortfolioId,   TextUtils.join(",",this.tids ));
                mReturned = call.execute().body();
                if (mReturned != null) {
                    getClientContext().getAccount().addOrUpdate(mReturned);
                }

            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mTransactionListAdapter.removeSelection();

            if (success && mReturned != null) {

                refresh();
            } else {
                Toast.makeText(this.getParentActivity(), "Failed to operate transaction.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
