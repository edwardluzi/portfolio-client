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
import org.goldenroute.portfolioclient.IntentConstants;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.adapter.TransactionListAdapter;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.model.Transaction;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;


public class PortfolioTransactionFragment extends RefreshableFragment implements ListView.OnItemClickListener {
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
        args.putLong(IntentConstants.ARG_PID, portfolioId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mPortfolioId = getArguments().getLong(IntentConstants.ARG_PID);
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
                        builder.setMessage(getString(R.string.message_confirm_for_deleting));
                        builder.setTitle(getString(R.string.title_delete_transaction));
                        builder.setPositiveButton(getString(R.string.label_delete), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new DeletingTransactionTask(getActivity(), mTransactionListAdapter.getSelectedIds()).execute((Void) null);
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
        intent.putExtra(IntentConstants.ARG_PID, mPortfolioId);
        intent.putExtra(IntentConstants.ARG_TID, transaction.getId());
        startActivityForResult(intent, IntentConstants.RC_EDIT_TRANSACTION);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstants.RC_EDIT_TRANSACTION) {
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

    public class DeletingTransactionTask extends RestAsyncTask<Void, Void, Boolean> {
        private Set<Long> mTransactionIds;
        private Portfolio mReturned;

        public DeletingTransactionTask(Activity activity, Set<Long> tids) {
            super(activity, true);
            this.mTransactionIds = tids;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<Portfolio> call = RestOperations.getInstance().getTransactionService().delete(mPortfolioId, TextUtils.join(",", this.mTransactionIds));
                Response<Portfolio> response = call.execute();
                mReturned = response.body();
                if (response.isSuccessful() && mReturned != null) {
                    getClientContext().getAccount().addOrUpdate(mReturned);
                } else {
                    parseError(response);
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
                Toast.makeText(this.getParentActivity(),
                        String.format(Locale.getDefault(),
                                getString(R.string.message_deleting_transaction_failed),
                                getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
