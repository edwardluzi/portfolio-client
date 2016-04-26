package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;

public class CreateTransactionActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String ARG_PID = "pid";
    public static final String ARG_TID = "tid";
    private static final String TAG = CreateTransactionActivity.class.getName();

    private Long mPortfolioId;
    private Long mTransactionId;
    private AddingTransactionTask mAsyncTask;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat mDateFormatter;

    @Bind(R.id.toolbar_transaction)
    protected Toolbar mToolbar;

    @Bind(R.id.edit_text_transaction_date)
    protected EditText nEditTextDate;

    @Bind(R.id.edit_text_transaction_ticker)
    protected EditText nEditTextTicker;

    @Bind(R.id.spinner_transaction_type)
    protected Spinner mSpinnerType;

    @Bind(R.id.edit_text_transaction_price)
    protected EditText nEditTextPrice;

    @Bind(R.id.edit_text_transaction_amount)
    protected EditText nEditTextAmount;

    @Bind(R.id.edit_text_transaction_commission)
    protected EditText nEditTextCommission;

    @Bind(R.id.edit_text_transaction_other_charges)
    protected EditText nEditTextOtherCharges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_transaction);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        setupDateText();
        setupSpinnerContext(mSpinnerType, R.array.transaction_types);

        mPortfolioId = getIntent().getExtras().getLong(ARG_PID);
        mTransactionId = getIntent().getExtras().getLong(ARG_TID);

        if (mTransactionId != 0) {
            Transaction transaction = getClientContext().getAccount().find(mPortfolioId).find(mTransactionId);

            nEditTextDate.setText(mDateFormatter.format(transaction.getDate()));
            nEditTextTicker.setText(transaction.getTicker());
            mSpinnerType.setSelection(transaction.getType().ordinal());

            setupDecimalText(nEditTextPrice, transaction.getPrice());
            setupDecimalText(nEditTextAmount, transaction.getAmount());
            setupDecimalText(nEditTextCommission, transaction.getCommission());
            setupDecimalText(nEditTextOtherCharges, transaction.getOtherCharges());
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
        if (view == nEditTextDate) {
            mDatePickerDialog.show();
        }
    }

    private void setupDateText() {
        nEditTextDate.setInputType(InputType.TYPE_NULL);
        nEditTextDate.requestFocus();
        mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        nEditTextDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        nEditTextDate.setText(mDateFormatter.format(newCalendar.getTime()));

        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                nEditTextDate.setText(mDateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void setupDecimalText(EditText editText, BigDecimal value) {
        if (value != null) {
            editText.setText(value.toString());
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
            date = mDateFormatter.parse(nEditTextDate.getText().toString());
        } catch (ParseException e) {
            nEditTextDate.setError(e.getMessage());
            return;
        }

        String ticker = nEditTextTicker.getText().toString().trim();
        if (ticker.length() == 0) {
            nEditTextTicker.setError("Must provide a ticker.");
            return;
        }

        Transaction.Type type = Transaction.Type.values()[mSpinnerType.getSelectedItemPosition()];

        BigDecimal price = loadDecimalValue(nEditTextPrice, true);
        if (price == null) {
            return;
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            nEditTextPrice.setError("Price must be larger than 0.00.");
            return;
        }

        BigDecimal amount = loadDecimalValue(nEditTextAmount, true);
        if (amount == null) {
            return;
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            nEditTextAmount.setError("Amount must be larger than 0.00.");
            return;
        }

        BigDecimal commission = loadDecimalValue(nEditTextCommission, false);
        BigDecimal otherCharges = loadDecimalValue(nEditTextOtherCharges, false);

        Transaction transaction;

        if (mTransactionId != 0) {
            transaction = getClientContext().getAccount().find(mPortfolioId).find(mTransactionId);
        } else {
            transaction = new Transaction();
        }

        transaction.setDate(date);
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

    private ClientContext getClientContext() {
        return (ClientContext) this.getApplication();
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

                if (mTransactionId == 0) {
                    Call<Portfolio> call = RestOperations.getInstance().getTransactionService().create(mPortfolioId, mTransaction);
                    mReturned = call.execute().body();
                } else {
                    Call<Portfolio> call = RestOperations.getInstance().getTransactionService().update(mPortfolioId, mTransaction.getId(), mTransaction);
                    mReturned = call.execute().body();
                }

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

            mAsyncTask = null;

            if (success && mReturned != null) {
                Toast.makeText(this.getParentActivity(), "Succeed in operating transaction", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.putExtra(ARG_TID, mTransactionId);
                setResult(RESULT_OK, intent);
                finish();

            } else {
                Toast.makeText(this.getParentActivity(), "Failed to operate transaction", Toast.LENGTH_LONG).show();
            }
        }
    }
}
