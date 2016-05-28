package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.model.Transaction;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;

public class CreateTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CreateTransactionActivity.class.getName();

    private Long mPortfolioId;
    private Long mTransactionId;
    private AddingTransactionTask mAsyncTask;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat mDateFormatter;

    @Bind(R.id.toolbar_transaction)
    protected Toolbar mToolbar;

    @Bind(R.id.edit_text_transaction_date)
    protected EditText mEditTextDate;

    @Bind(R.id.edit_text_transaction_ticker)
    protected EditText mEditTextTicker;

    @Bind(R.id.spinner_transaction_type)
    protected Spinner mSpinnerType;

    @Bind(R.id.edit_text_transaction_price)
    protected EditText mEditTextPrice;

    @Bind(R.id.edit_text_transaction_amount)
    protected EditText mEditTextAmount;

    @Bind(R.id.edit_text_transaction_commission)
    protected EditText mEditTextCommission;

    @Bind(R.id.edit_text_transaction_other_charges)
    protected EditText mEditTextOtherCharges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupDateText();
        setupSpinnerContext(mSpinnerType, R.array.transaction_types);

        mPortfolioId = getIntent().getExtras().getLong(IntentConstants.ARG_PID);
        mTransactionId = getIntent().getExtras().getLong(IntentConstants.ARG_TID);

        if (mTransactionId != 0) {
            Transaction transaction = ClientContext.getInstance().getAccount().find(mPortfolioId).find(mTransactionId);

            mEditTextDate.setText(mDateFormatter.format(transaction.getTimestamp()));
            mEditTextTicker.setText(transaction.getTicker());
            mSpinnerType.setSelection(transaction.getType().ordinal());

            setupDecimalText(mEditTextPrice, transaction.getPrice());
            setupDecimalText(mEditTextAmount, transaction.getAmount());
            setupDecimalText(mEditTextCommission, transaction.getCommission());
            setupDecimalText(mEditTextOtherCharges, transaction.getOtherCharges());
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
            saveTransaction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == mEditTextDate) {
            mDatePickerDialog.show();
        }
    }

    private void setupDateText() {
        mEditTextDate.setInputType(InputType.TYPE_NULL);
        mEditTextDate.requestFocus();
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        mEditTextDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mEditTextDate.setText(mDateFormatter.format(newCalendar.getTime()));

        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mEditTextDate.setText(mDateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupDecimalText(EditText editText, BigDecimal value) {
        if (value != null) {
            editText.setText(String.format(Locale.getDefault(), "%.2f", value));
        }
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

    private void saveTransaction() {
        if (mAsyncTask != null) {
            return;
        }

        Date date;

        try {
            date = mDateFormatter.parse(mEditTextDate.getText().toString());
        } catch (ParseException e) {
            mEditTextDate.setError(e.getMessage());
            return;
        }

        String ticker = mEditTextTicker.getText().toString().trim();
        if (ticker.length() == 0) {
            mEditTextTicker.setError(getString(R.string.message_provide_a_valid_ticker));
            return;
        }

        Transaction.Type type = Transaction.Type.values()[mSpinnerType.getSelectedItemPosition()];

        BigDecimal price = loadDecimalValue(mEditTextPrice, true);
        if (price == null) {
            return;
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            mEditTextPrice.setError(getString(R.string.message_price_must_be_positive));
            return;
        }

        BigDecimal amount = loadDecimalValue(mEditTextAmount, true);
        if (amount == null) {
            return;
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            mEditTextAmount.setError(getString(R.string.message_amount_must_be_positive));
            return;
        }

        BigDecimal commission = loadDecimalValue(mEditTextCommission, false);
        BigDecimal otherCharges = loadDecimalValue(mEditTextOtherCharges, false);

        Transaction transaction;

        if (mTransactionId != 0) {
            transaction = ClientContext.getInstance().getAccount().find(mPortfolioId).find(mTransactionId);
        } else {
            transaction = new Transaction();
        }

        transaction.setTimestamp(date);
        transaction.setTicker(ticker);
        transaction.setType(type);
        transaction.setPrice(price);
        transaction.setAmount(amount);
        transaction.setCommission(commission);
        transaction.setOtherCharges(otherCharges);

        mAsyncTask = new AddingTransactionTask(this, transaction);
        mAsyncTask.execute((Void) null);
    }

    private BigDecimal loadDecimalValue(EditText editText, boolean required) {
        BigDecimal value = null;
        String text = editText.getText().toString().trim();

        try {
            if (text.length() != 0 || required) {
                value = new BigDecimal(text);
            }
        } catch (Exception e) {
            editText.setError(e.getMessage());
            value = null;
        }

        return value;
    }

    public class AddingTransactionTask extends RestAsyncTask<Void, Void, Boolean> {

        private Transaction mTransaction;
        private Portfolio mReturned;

        public AddingTransactionTask(Activity activity, Transaction transaction) {
            super(activity, true);
            mTransaction = transaction;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Call<Portfolio> call;
                Response<Portfolio> response;
                if (mTransactionId == 0) {
                    call = RestOperations.getInstance().getTransactionService().create(mPortfolioId, mTransaction);
                } else {
                    call = RestOperations.getInstance().getTransactionService().update(mPortfolioId, mTransaction.getId(), mTransaction);
                }
                response = call.execute();
                if (response.isSuccessful()) {
                    mReturned = response.body();
                }
                if (mReturned != null) {
                    ClientContext.getInstance().getAccount().addOrUpdate(mReturned);
                } else {
                    parseError(response);
                }

            } catch (Exception e) {
                mReturned = null;
                parseError(e);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            mAsyncTask = null;
            if (success && mReturned != null) {
                Toast.makeText(getParentActivity(),
                        getString(mTransactionId == 0 ?
                                R.string.message_adding_transaction_succeeded : R.string.message_modifying_transaction_succeeded),
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra(IntentConstants.ARG_TID, mTransactionId);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(getParentActivity(),
                        String.format(Locale.getDefault(),
                                getString(mTransactionId == 0 ? R.string.message_adding_transaction_failed : R.string.message_modifying_transaction_failed),
                                getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
