package org.goldenroute.portfolioclient.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.utils.PriceFormatter;

import java.util.List;


public class PortfolioListAdapter extends ToggleAdapter {
    private Activity mActivity;
    private List<Portfolio> mPortfolios;

    public PortfolioListAdapter(Activity activity, List<Portfolio> portfolios) {
        super();
        this.mActivity = activity;
        this.mPortfolios = portfolios;
    }

    public List<Portfolio> getData() {
        return mPortfolios;
    }

    @Override
    public int getCount() {
        return mPortfolios.size();
    }

    @Override
    public Object getItem(int position) {
        return mPortfolios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mPortfolios.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_portfolio, null);

            viewHolder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_name),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_weight),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_value),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_cost),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_daily_change),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_daily_change_percentage),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_total_change),
                    (TextView) convertView.findViewById(R.id.list_text_view_portfolio_total_change_percentage)
            );

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Portfolio summary = mPortfolios.get(position);

        if (getSelectedIds().contains(summary.getId())) {
            SpannableString spanString = new SpannableString("+" + summary.getName());
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
            spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
            viewHolder.getTextViewName().setText(spanString);
        } else {
            viewHolder.getTextViewName().setText(summary.getName());
        }

        PriceFormatter.setDecimalText(viewHolder.getTextViewWeight(), summary.getWeight());
        PriceFormatter.setDecimalText(viewHolder.getTextViewValue(), summary.getValue());
        PriceFormatter.setDecimalText(viewHolder.getTextViewCost(), summary.getCost());
        PriceFormatter.setChangeText(viewHolder.getTextViewDailyChange(), summary.getDailyChange());
        PriceFormatter.setChangePercentageText(viewHolder.getTextViewDailyChangePercentage(), summary.getDailyChangePercentage());
        PriceFormatter.setChangeText(viewHolder.getTextViewTotalChange(), summary.getTotalChange());
        PriceFormatter.setChangePercentageText(viewHolder.getTextViewTotalChangePercentage(), summary.getTotalChangePercentage());

        return convertView;
    }

    public final class ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewWeight;
        private TextView mTextViewValue;
        private TextView mTextViewCost;
        private TextView mTextViewDailyChange;
        private TextView mTextViewDailyChangePercentage;
        private TextView mTextViewTotalChange;
        private TextView mTextViewTotalChangePercentage;

        public ViewHolder(TextView textViewName, TextView textViewWeight, TextView textViewValue, TextView textViewCost, TextView textViewDailyChange, TextView textViewDailyChangePercentage, TextView textViewTotalChange, TextView textViewTotalChangePercentage) {
            this.mTextViewName = textViewName;
            this.mTextViewWeight = textViewWeight;
            this.mTextViewValue = textViewValue;
            this.mTextViewCost = textViewCost;
            this.mTextViewDailyChange = textViewDailyChange;
            this.mTextViewDailyChangePercentage = textViewDailyChangePercentage;
            this.mTextViewTotalChange = textViewTotalChange;
            this.mTextViewTotalChangePercentage = textViewTotalChangePercentage;
        }

        public TextView getTextViewName() {
            return mTextViewName;
        }

        public TextView getTextViewWeight() {
            return mTextViewWeight;
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