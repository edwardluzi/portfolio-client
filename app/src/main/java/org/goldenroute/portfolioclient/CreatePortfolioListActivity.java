package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.goldenroute.portfolioclient.model.AssetClass;
import org.goldenroute.portfolioclient.model.Currency;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;

public class CreatePortfolioListActivity extends AppCompatActivity {

    public static final String ARG_PID = "pid";
    public static final String ARG_TYPE = "type";

    @Bind(R.id.toolbar_create_portfolio_list)
    protected Toolbar mToolbar;

    @Bind(R.id.spinner_portfolio_list_type)
    protected Spinner mSpinnerType;

    @Bind(R.id.edit_text_portfolio_list_name)
    protected EditText m_EditTextName;

    @Bind(R.id.spinner_portfolio_list_asset_class)
    protected Spinner mSpinnerAssetClass;

    @Bind(R.id.edit_text_portfolio_list_benchmark)
    protected EditText m_EditTextBenchmark;

    @Bind(R.id.spinner_portfolio_list_currency)
    protected Spinner mSpinnerCurrency;

    @Bind(R.id.edit_text_portfolio_list_description)
    protected EditText m_EditTextDescription;

    private Long mPortfolioId;
    private CreatePortfolioTask mCreatePortfolioTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_portfolio_list);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupSpinnerContext(mSpinnerType, R.array.portfolio_list_types);
        setupSpinnerContext(mSpinnerAssetClass, R.array.portfolio_list_assert_classes);
        setupSpinnerContext(mSpinnerCurrency, R.array.portfolio_list_currencies);

        mPortfolioId = getIntent().getLongExtra(ARG_PID, 0L);

        if (mPortfolioId != 0) {
            Portfolio portfolio = getClientContext().getAccount().find(mPortfolioId);
            mSpinnerType.setSelection(0);
            m_EditTextName.setText(portfolio.getName());
            mSpinnerAssetClass.setSelection(portfolio.getPrimaryAssetClass().ordinal());
            m_EditTextBenchmark.setText(portfolio.getBenchmark());
            mSpinnerCurrency.setSelection(portfolio.getCurrency().ordinal());
            m_EditTextDescription.setText(portfolio.getDescription());
            mToolbar.setTitle(getString(R.string.title_activity_edit_portfolio));
        } else {
            mSpinnerType.setSelection(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
            savePortfolio();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSpinnerContext(Spinner spinner, int stringArrayId) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                stringArrayId, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    private void savePortfolio() {

        if (mCreatePortfolioTask != null) {
            return;
        }

        if (m_EditTextName.getText().toString().length() == 0) {
            m_EditTextName.setError("Please provide a valid name.");
            return;
        }

        Portfolio portfolio = mPortfolioId != 0 ? getClientContext().getAccount().find(mPortfolioId) : new Portfolio();

        portfolio.setName(m_EditTextName.getText().toString());
        portfolio.setPrimaryAssetClass(AssetClass.values()[mSpinnerAssetClass.getSelectedItemPosition()]);
        portfolio.setBenchmark(m_EditTextBenchmark.getText().toString());
        portfolio.setCurrency(Currency.values()[mSpinnerCurrency.getSelectedItemPosition()]);
        portfolio.setDescription(m_EditTextDescription.getText().toString());

        mCreatePortfolioTask = new CreatePortfolioTask(this, portfolio);
        mCreatePortfolioTask.execute((Void) null);
    }

    private ClientContext getClientContext() {
        return (ClientContext) this.getApplication();
    }

    public class CreatePortfolioTask extends RestAsyncTask<Void, Void, Boolean> {

        private Portfolio mPortfolio;
        private Portfolio mReturned;

        public CreatePortfolioTask(Activity activity, Portfolio portfolio) {
            super(activity, true);
            mPortfolio = portfolio;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Long accountId = getClientContext().getAccount().getId();

            try {
                if (mPortfolioId == 0) {
                    Call<Portfolio> call = RestOperations.getInstance().getPortfolioService().create(accountId, mPortfolio);
                    mReturned = call.execute().body();

                    if (mReturned != null) {
                        getClientContext().getAccount().addOrUpdate(mReturned);
                    }
                } else {
                    Call<Portfolio> call = RestOperations.getInstance().getPortfolioService().update(accountId, mPortfolio.getId(), mPortfolio);
                    mReturned = call.execute().body();

                    if (mReturned != null) {
                        getClientContext().getAccount().addOrUpdate(mReturned);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            super.onPostExecute(success);

            mCreatePortfolioTask = null;

            if (success && mReturned != null) {
                Toast.makeText(this.getParentActivity(), "Succeed in operating portfolio", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra(ARG_PID, mReturned.getId());
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(this.getParentActivity(), "Failed to operate portfolio", Toast.LENGTH_LONG).show();
            }
        }
    }
}
