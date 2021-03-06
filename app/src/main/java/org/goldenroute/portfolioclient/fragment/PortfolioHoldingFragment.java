package org.goldenroute.portfolioclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.goldenroute.portfolioclient.ClientContext;
import org.goldenroute.portfolioclient.IntentConstants;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.adapter.HoldingListAdapter;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Holding;
import org.goldenroute.portfolioclient.model.Portfolio;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PortfolioHoldingFragment extends RefreshableFragment {

    private Long mPortfolioId;

    @Bind(R.id.list_view_holdings)
    protected ListView mListViewHoldings;

    @Bind(R.id.list_view_holding_total)
    protected ListView mListViewTotal;

    private HoldingListAdapter mHoldingListAdapter;
    private HoldingListAdapter mTotalListAdapter;

    public PortfolioHoldingFragment() {
    }

    public static PortfolioHoldingFragment newInstance(Long portfolioId) {
        PortfolioHoldingFragment fragment = new PortfolioHoldingFragment();
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
        View fragment = inflater.inflate(R.layout.fragment_portfolio_holding, container, false);
        ButterKnife.bind(this, fragment);

        mHoldingListAdapter = new HoldingListAdapter(getActivity(), new ArrayList<Holding>());
        mListViewHoldings.setAdapter(mHoldingListAdapter);

        mTotalListAdapter = new HoldingListAdapter(getActivity(), new ArrayList<Holding>());
        mListViewTotal.setAdapter(mTotalListAdapter);

        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refresh();
    }

    @Override
    public void refresh() {
        mListViewHoldings.post(new Runnable() {
            public void run() {
                Account account = ClientContext.getInstance().getAccount();
                if (account != null) {
                    Portfolio portfolio = account.find(mPortfolioId);
                    if (portfolio != null) {
                        List<Holding> holdings = portfolio.getHoldings();
                        mHoldingListAdapter.getData().clear();
                        if (holdings != null) {
                            mHoldingListAdapter.getData().addAll(holdings);
                        }
                        List<Holding> totals = new ArrayList<>();
                        Holding total = new Holding(0L);

                        total.setTicker("Total");
                        total.setCost(portfolio.getCost());
                        total.setValue(portfolio.getValue());
                        total.setDailyChange(portfolio.getDailyChange());
                        total.setDailyChangePercentage(portfolio.getDailyChangePercentage());
                        total.setTotalChange(portfolio.getTotalChange());
                        total.setTotalChangePercentage(portfolio.getTotalChangePercentage());
                        totals.add(total);

                        mTotalListAdapter.getData().clear();
                        mTotalListAdapter.getData().addAll(totals);

                        mHoldingListAdapter.notifyDataSetChanged();
                        mTotalListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
