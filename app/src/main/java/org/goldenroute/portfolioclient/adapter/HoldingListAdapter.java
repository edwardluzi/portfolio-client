package org.goldenroute.portfolioclient.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.Holding;
import org.goldenroute.portfolioclient.utils.PriceFormatter;

import java.util.List;

public class HoldingListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<Holding> mHoldings;

    public HoldingListAdapter(Activity activity, List<Holding> holdings) {
        super();
        this.mActivity = activity;
        this.mHoldings = holdings;
    }

    public List<Holding> getData() {
        return mHoldings;
    }

    @Override
    public int getCount() {
        return mHoldings.size();
    }

    @Override
    public Object getItem(int position) {
        return mHoldings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mHoldings.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_holding, null);

            viewHolder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_ticker),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_amount),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_value),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_cost),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_daily_change),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_daily_change_percentage),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_total_change),
                    (TextView) convertView.findViewById(R.id.list_text_view_holding_total_change_percentage)
            );
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Holding summary = mHoldings.get(position);

        viewHolder.getTextViewTicker().setText(summary.getTicker());
        PriceFormatter.setDecimalText(viewHolder.getTextViewAmount(), summary.getAmount());
        PriceFormatter.setDecimalText(viewHolder.getTextViewValue(), summary.getValue());
        PriceFormatter.setDecimalText(viewHolder.getTextViewCost(), summary.getCost());
        PriceFormatter.setChangeText(viewHolder.getTextViewDailyChange(), summary.getDailyChange());
        PriceFormatter.setChangePercentageText(viewHolder.getTextViewDailyChangePercentage(), summary.getDailyChangePercentage());
        PriceFormatter.setChangeText(viewHolder.getTextViewTotalChange(), summary.getTotalChange());
        PriceFormatter.setChangePercentageText(viewHolder.getTextViewTotalChangePercentage(), summary.getTotalChangePercentage());

        return convertView;
    }

    public final class ViewHolder {
        private TextView mTextViewTicker;
        private TextView mTextViewAmount;
        private TextView mTextViewValue;
        private TextView mTextViewCost;
        private TextView mTextViewDailyChange;
        private TextView mTextViewDailyChangePercentage;
        private TextView mTextViewTotalChange;
        private TextView mTextViewTotalChangePercentage;

        public ViewHolder(TextView textViewTicker, TextView textViewAmount, TextView textViewValue, TextView textViewCost, TextView textViewDailyChange, TextView textViewDailyChangePercentage, TextView textViewTotalChange, TextView textViewTotalChangePercentage) {
            this.mTextViewTicker = textViewTicker;
            this.mTextViewAmount = textViewAmount;
            this.mTextViewValue = textViewValue;
            this.mTextViewCost = textViewCost;
            this.mTextViewDailyChange = textViewDailyChange;
            this.mTextViewDailyChangePercentage = textViewDailyChangePercentage;
            this.mTextViewTotalChange = textViewTotalChange;
            this.mTextViewTotalChangePercentage = textViewTotalChangePercentage;
        }

        public TextView getTextViewTicker() {
            return mTextViewTicker;
        }

        public TextView getTextViewAmount() {
            return mTextViewAmount;
        }

        public TextView getTextViewValue() {
            return mTextViewValue;
        }

        public TextView getTextViewCost() {
            return mTextViewCost;
        }

        public TextView getTextViewDailyChange() {
            return mTextViewDailyChange;
        }

        public TextView getTextViewDailyChangePercentage() {
            return mTextViewDailyChangePercentage;
        }

        public TextView getTextViewTotalChange() {
            return mTextViewTotalChange;
        }

        public TextView getTextViewTotalChangePercentage() {
            return mTextViewTotalChangePercentage;
        }
    }
}
