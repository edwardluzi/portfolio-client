package org.goldenroute.portfolioclient.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.Holding;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.utils.PriceFormater;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class HoldingListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Holding> holdings;

    public HoldingListAdapter(Activity activity, List<Holding> holdings) {
        super();
        this.activity = activity;
        this.holdings = holdings;
    }

    public List<Holding> getData() {
        return holdings;
    }

    @Override
    public int getCount() {
        return holdings.size();
    }

    @Override
    public Object getItem(int position) {
        return holdings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return holdings.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
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

        Holding summary = holdings.get(position);

        viewHolder.getTextViewTicker().setText(summary.getTicker());
        PriceFormater.setDecimalText(viewHolder.getTextViewAmount(), summary.getAmount());
        PriceFormater.setDecimalText(viewHolder.getTextViewValue(), summary.getValue());
        PriceFormater.setDecimalText(viewHolder.getTextViewCost(), summary.getCost());
        PriceFormater.setChangeText(viewHolder.getTextViewDailyChange(), summary.getDailyChange());
        PriceFormater.setChangePercentageText(viewHolder.getTextViewDailyChangePercentage(), summary.getDailyChangePercentage());
        PriceFormater.setChangeText(viewHolder.getTextViewTotalChange(), summary.getTotalChange());
        PriceFormater.setChangePercentageText(viewHolder.getTextViewTotalChangePercentage(), summary.getTotalChangePercentage());

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
