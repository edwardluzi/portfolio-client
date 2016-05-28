package org.goldenroute.portfolioclient.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.Transaction;
import org.goldenroute.portfolioclient.utils.PriceFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionListAdapter extends ToggleAdapter {
    private Activity mActivity;
    private List<Transaction> mTransactions;
    private SimpleDateFormat mDateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    public TransactionListAdapter(Activity activity, List<Transaction> transactions) {
        super();
        mActivity = activity;
        mTransactions = transactions;
    }

    public List<Transaction> getData() {
        return mTransactions;
    }

    @Override
    public int getCount() {
        return mTransactions.size();
    }

    @Override
    public Object getItem(int position) {
        return mTransactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTransactions.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_transaction, null);

            viewHolder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_date),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_ticker),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_type),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_price),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_amount),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_commission),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_transaction_other_charge),
                    (TextView) convertView.findViewById(R.id.list_text_view_transaction_total)
            );
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Transaction summary = mTransactions.get(position);

        viewHolder.getTextViewDate().setText(mDateFormatter.format(summary.getTimestamp()));

        if (position > 0 && mTransactions.get(position - 1).getTimestamp().compareTo(summary.getTimestamp()) == 0) {
            viewHolder.getTextViewDate().setVisibility(View.GONE);
        }

        viewHolder.getTextViewTicker().setText(summary.getTicker());
        viewHolder.getTextViewType().setText(summary.getType().toString());

        PriceFormatter.setDecimalText(viewHolder.getTextViewAmount(), summary.getAmount());
        PriceFormatter.setDecimalText(viewHolder.getTextViewPrice(), summary.getPrice());
        PriceFormatter.setDecimalText(viewHolder.getTextViewCommission(), summary.getCommission());
        PriceFormatter.setDecimalText(viewHolder.getTextViewOtherCharge(), summary.getOtherCharges());
        PriceFormatter.setDecimalText(viewHolder.getTextViewTotal(), summary.getTotal());

        return convertView;
    }

    public class ViewHolder {
        private TextView mTextViewDate;
        private TextView mTextViewTicker;
        private TextView mTextViewType;
        private TextView mTextViewPrice;
        private TextView mTextViewAmount;
        private TextView mTextViewCommission;
        private TextView mTextViewOtherCharge;
        private TextView mTextViewTotal;

        public ViewHolder(TextView textViewDate, TextView textViewTicker, TextView textViewType, TextView textViewPrice, TextView textViewAmount, TextView textViewCommission, TextView textViewOtherCharge, TextView textViewTotal) {
            mTextViewDate = textViewDate;
            mTextViewTicker = textViewTicker;
            mTextViewType = textViewType;
            mTextViewPrice = textViewPrice;
            mTextViewAmount = textViewAmount;
            mTextViewCommission = textViewCommission;
            mTextViewOtherCharge = textViewOtherCharge;
            mTextViewTotal = textViewTotal;
        }

        public TextView getTextViewDate() {
            return mTextViewDate;
        }

        public TextView getTextViewTicker() {
            return mTextViewTicker;
        }

        public TextView getTextViewType() {
            return mTextViewType;
        }

        public TextView getTextViewPrice() {
            return mTextViewPrice;
        }

        public TextView getTextViewAmount() {
            return mTextViewAmount;
        }

        public TextView getTextViewCommission() {
            return mTextViewCommission;
        }

        public TextView getTextViewOtherCharge() {
            return mTextViewOtherCharge;
        }

        public TextView getTextViewTotal() {
            return mTextViewTotal;
        }
    }
}