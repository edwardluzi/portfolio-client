package org.goldenroute.portfolioclient.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.AssetRiskReturnLevel;
import org.goldenroute.portfolioclient.utils.PriceFormatter;

import java.math.BigDecimal;
import java.util.List;

public class AssetAllocationListAdapter extends BaseAdapter {
    private Activity mActivity;
    private List<AssetRiskReturnLevel> mAssets;

    public AssetAllocationListAdapter(Activity activity, List<AssetRiskReturnLevel> assets) {
        super();
        mActivity = activity;
        mAssets = assets;
    }

    public List<AssetRiskReturnLevel> getData() {
        return mAssets;
    }

    @Override
    public int getCount() {
        return mAssets.size();
    }

    @Override
    public Object getItem(int position) {
        return mAssets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mAssets.get(position).getTicker().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item_asset_allocation, null);

            viewHolder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.list_text_view_asset_allocation_ticker),
                    (TextView) convertView.findViewById(R.id.list_text_view_asset_allocation_weight),
                    (TextView) convertView.findViewById(R.id.list_text_view_asset_allocation_return),
                    (TextView) convertView.findViewById(R.id.list_text_view_asset_allocation_standard_deviation),
                    (TextView) convertView.findViewById(R.id.list_text_view_asset_allocation_sharpe)
            );
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AssetRiskReturnLevel summary = mAssets.get(position);

        viewHolder.getTextViewTicker().setText(summary.getTicker());

        PriceFormatter.setPercentageText(viewHolder.getTextViewWeight(), BigDecimal.valueOf(summary.getWeight()));
        PriceFormatter.setPercentageText(viewHolder.getTextViewReturn(), BigDecimal.valueOf(summary.getExpectedReturn()));
        PriceFormatter.setPercentageText(viewHolder.getTextViewStandardDeviation(), BigDecimal.valueOf(summary.getStandardDeviation()));
        PriceFormatter.setDecimalText(viewHolder.getTextViewSharpe(), BigDecimal.valueOf(summary.getSharpe()), 4);

        return convertView;
    }

    public final class ViewHolder {
        private TextView mTextViewTicker;
        private TextView mTextViewWeight;
        private TextView mTextViewReturn;
        private TextView mTextViewStandardDeviation;
        private TextView mTextViewSharpe;

        public ViewHolder(TextView textViewTicker, TextView textViewWeight, TextView textViewReturn, TextView textViewStandardDeviation, TextView textViewSharpe) {
            this.mTextViewTicker = textViewTicker;
            this.mTextViewWeight = textViewWeight;
            this.mTextViewReturn = textViewReturn;
            this.mTextViewStandardDeviation = textViewStandardDeviation;
            this.mTextViewSharpe = textViewSharpe;
        }

        public TextView getTextViewTicker() {
            return mTextViewTicker;
        }

        public TextView getTextViewWeight() {
            return mTextViewWeight;
        }

        public TextView getTextViewReturn() {
            return mTextViewReturn;
        }

        public TextView getTextViewStandardDeviation() {
            return mTextViewStandardDeviation;
        }

        public TextView getTextViewSharpe() {
            return mTextViewSharpe;
        }
    }
}
