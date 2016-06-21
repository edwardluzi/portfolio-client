package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.goldenroute.portfolioclient.adapter.AssetAllocationListAdapter;
import org.goldenroute.portfolioclient.dagger2.DaggerApplication;
import org.goldenroute.portfolioclient.model.AssetRiskReturnLevel;
import org.goldenroute.portfolioclient.model.MarkowitzPortfolio;
import org.goldenroute.portfolioclient.model.PortfolioReport;
import org.goldenroute.portfolioclient.services.CallbackListener;
import org.goldenroute.portfolioclient.services.RemoteService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class AnalysisPortfolioListActivity extends AppCompatActivity {
    private static final String TAG = AnalysisPortfolioListActivity.class.getName();

    private Long mPortfolioId;
    private PortfolioReport mReport;

    @Bind(R.id.toolbar_analysis_portfolio_list)
    protected Toolbar mToolbar;

    @Bind(R.id.chart_portfolio_analysis_efficient_frontier)
    protected LineChartView mLineChart;

    private Map<String, MarkowitzPortfolio> mDetails;

    @Inject
    protected RemoteService mRemoteService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((DaggerApplication) getApplication())
                .getComponent()
                .inject(this);

        setContentView(R.layout.activity_analysis_portfolio_list);

        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mLineChart.setOnValueTouchListener(new LineChartValueSelectListener());
        setupLineChart();

        mPortfolioId = getIntent().getLongExtra(IntentConstants.ARG_PID, 0L);

        mRemoteService.generatePortfolioReport(this, mPortfolioId, 0.0025, 1.0, "M", new CallbackListener<PortfolioReport>() {
            @Override
            public void onSuccess(PortfolioReport data) {
                refresh(data);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AnalysisPortfolioListActivity.this,
                        message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupLineChart() {
        mLineChart.setViewportCalculationEnabled(false);
        mLineChart.setLineChartData(createFakeLineChartData());

        Viewport viewport = new Viewport(mLineChart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = 100;
        viewport.left = 0;
        viewport.right = 100;
        mLineChart.setMaximumViewport(viewport);
        mLineChart.setCurrentViewport(viewport);
    }

    private LineChartData createFakeLineChartData() {
        mDetails = new HashMap<>();
        List<Line> lines = new ArrayList<>();
        LineChartData lineChartData = new LineChartData(lines);

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Risk Level (Standard Deviation) %");
        axisY.setName("Return Level %");

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);

        return lineChartData;
    }

    private Viewport calculateViewPort(PortfolioReport report) {
        float left = Float.MAX_VALUE;
        float right = Float.MIN_VALUE;
        float top = Float.MIN_VALUE;
        float bottom = Float.MAX_VALUE;

        List<MarkowitzPortfolio> list = new ArrayList<>();
        list.addAll(report.getEfficientFrontier().getFrontiers());
        list.addAll(report.getIndividuals().values());
        list.add(report.getOverall());

        for (MarkowitzPortfolio portfolio : list) {
            float var = (float) portfolio.getStandardDeviationPercentage();
            float ret = (float) portfolio.getExpectedReturnPercentage();

            if (var < left) {
                left = var;
            }

            if (var > right) {
                right = var;
            }
            if (ret > top) {
                top = ret;
            }

            if (ret < bottom) {
                bottom = ret;
            }
        }

        float increaseX = (right - left) * 0.3f;
        float increaseY = (top - bottom) * 0.3f;

        left -= increaseX;
        right += increaseX;
        bottom -= increaseY;
        top += increaseY;

        Viewport viewport = new Viewport();
        viewport.left = left;
        viewport.right = right;
        viewport.top = top;
        viewport.bottom = bottom;

        return viewport;
    }

    private LineChartData loadLineChartData(PortfolioReport report) {
        mDetails = new HashMap<>();
        List<Line> lines = new ArrayList<>();
        lines.add(loadEfficientFrontierLine(report));
        lines.add(loadIndividualLine(report));
        lines.add(loadTangencyLine(report));
        LineChartData lineChartData = new LineChartData(lines);

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("Risk Level (Standard Deviation) %");
        axisY.setName("Return Level %");

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);

        return lineChartData;
    }


    private Line loadEfficientFrontierLine(PortfolioReport report) {
        List<PointValue> values = new ArrayList<>();

        for (MarkowitzPortfolio portfolio : report.getEfficientFrontier().getFrontiers()) {
            float sd = (float) portfolio.getStandardDeviationPercentage();
            float ret = (float) portfolio.getExpectedReturnPercentage();
            String label = String.format("%.3f, %.3f", ret, sd);
            values.add(new PointValue(sd, ret).setLabel(label));
            mDetails.put(label, portfolio);
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[0]);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);
        line.setFilled(false);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(true);
        line.setHasLines(true);
        line.setHasPoints(true);

        return line;
    }

    private Line loadIndividualLine(PortfolioReport report) {
        List<PointValue> values = new ArrayList<>();

        for (Map.Entry<String, MarkowitzPortfolio> portfolio : report.getIndividuals().entrySet()) {
            values.add(new PointValue((float) portfolio.getValue().getStandardDeviationPercentage(), (float) portfolio.getValue().getExpectedReturnPercentage()).setLabel(portfolio.getKey()));
        }

        values.add(new PointValue((float) report.getOverall().getStandardDeviationPercentage(), (float) report.getOverall().getExpectedReturnPercentage()).setLabel("#CUR"));
        mDetails.put("#CUR", report.getOverall());

        values.add(new PointValue((float) report.getEfficientFrontier().getGlobalMinimumVariance().getStandardDeviationPercentage(), (float) report.getEfficientFrontier().getGlobalMinimumVariance().getExpectedReturnPercentage()).setLabel("#GMV"));
        mDetails.put("#GMV", report.getEfficientFrontier().getGlobalMinimumVariance());

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[1]);
        line.setShape(ValueShape.SQUARE);
        line.setCubic(true);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(false);
        line.setHasPoints(true);

        return line;
    }

    private Line loadTangencyLine(PortfolioReport report) {
        List<PointValue> values = new ArrayList<>();
        values.add(new PointValue(0, (float) report.getRiskFree() * 100).setLabel("RF"));

        values.add(new PointValue((float) report.getTangency().getStandardDeviationPercentage(), (float) report.getTangency().getExpectedReturnPercentage()).setLabel("#TAN"));
        mDetails.put("#TAN", report.getTangency());

        float change = values.get(1).getY() - values.get(0).getY();
        for (int i = 2; i < 5; i++) {
            values.add(new PointValue(values.get(1).getX() * i, values.get(0).getY() + change * i));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLORS[2]);
        line.setShape(ValueShape.SQUARE);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(true);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(true);
        line.setHasPoints(false);

        return line;
    }

    private void refresh(PortfolioReport report) {
        mReport = report;

        final Viewport viewport = calculateViewPort(report);
        final LineChartData lineChartData = loadLineChartData(report);
        final String portfolioName = report.getPortfolioName();

        mLineChart.post(new Runnable() {
            public void run() {
                mLineChart.setLineChartData(lineChartData);
                mLineChart.setMaximumViewport(viewport);
                mLineChart.setCurrentViewportWithAnimation(viewport);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(portfolioName);
                }
            }
        });
    }

    private class LineChartValueSelectListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            char[] labelArray = value.getLabelAsChars();
            if (labelArray != null && labelArray.length > 0) {
                String label = new String(value.getLabelAsChars());
                if (mDetails.containsKey(label)) {
                    new AssetAllocationPopupWindow(AnalysisPortfolioListActivity.this, mReport, mDetails.get(label)).show();
                }
            }
        }

        @Override
        public void onValueDeselected() {
        }
    }

    private class AssetAllocationPopupWindow {
        private Activity mActivity;
        private PortfolioReport mReport;
        private MarkowitzPortfolio mPortfolio;

        private PieChartView mPieChartCurrent;
        private PieChartView mPieChartSelected;
        private ListView mListViewAssetAllocations;
        private AssetAllocationListAdapter mAssetAllocationAdapter;

        public AssetAllocationPopupWindow(Activity activity, PortfolioReport report, MarkowitzPortfolio portfolio) {
            mActivity = activity;
            mReport = report;
            mPortfolio = portfolio;
        }

        public void show() {
            LayoutInflater layoutInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View popupView = layoutInflater.inflate(R.layout.popup_asset_allocation, null);
            final PopupWindow popupWindow = new PopupWindow(mActivity);
            popupWindow.setContentView(popupView);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            setupPieChart(popupView);
            setupListView(popupView);

            Button close = (Button) popupView.findViewById(R.id.button_asset_allocation_close);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }

        private void setupListView(View popupView) {
            mListViewAssetAllocations = (ListView) popupView.findViewById(R.id.list_view_asset_allocation);
            mAssetAllocationAdapter = new AssetAllocationListAdapter(mActivity, loadAssetRiskReturnLevels());
            mListViewAssetAllocations.setAdapter(mAssetAllocationAdapter);
        }

        private void setupPieChart(View popupView) {
            mPieChartCurrent = (PieChartView) popupView.findViewById(R.id.chart_portfolio_current_analysis_allocation);
            mPieChartSelected = (PieChartView) popupView.findViewById(R.id.chart_portfolio_analysis_selected_allocation);
            mPieChartCurrent.setPieChartData(loadPieChartData(mReport.getOverall()));
            mPieChartSelected.setPieChartData(loadPieChartData(mPortfolio));
        }

        private PieChartData loadPieChartData(MarkowitzPortfolio portfolio) {
            List<SliceValue> values = new ArrayList<SliceValue>();
            double[] weights = portfolio.getWeights();
            if (weights != null) {
                for (int i = 0; i < weights.length; ++i) {
                    float percentage = (float) weights[i] * 100f;
                    SliceValue sliceValue = new SliceValue(percentage, ChartUtils.COLORS[i % ChartUtils.COLORS.length]);
                    sliceValue.setLabel(String.format("%.2f%%", percentage));
                    values.add(sliceValue);
                }
            } else {
                SliceValue sliceValue = new SliceValue(100f, ChartUtils.COLORS[0]);
                sliceValue.setLabel("100.00%");
                values.add(sliceValue);
            }

            PieChartData pieChartData = new PieChartData(values);
            pieChartData.setHasLabels(true);
            pieChartData.setHasLabelsOnlyForSelected(false);
            pieChartData.setHasLabelsOutside(false);
            pieChartData.setHasCenterCircle(false);
            return pieChartData;
        }

        private List<AssetRiskReturnLevel> loadAssetRiskReturnLevels() {
            List<AssetRiskReturnLevel> assetRiskReturnLevels = new ArrayList<>();

            for (int index = 0; index < mReport.getSymbols().size(); index++) {
                String symbol = mReport.getSymbols().get(index);
                double weight = mPortfolio.getWeights()[index];
                double expectedReturn = mReport.getIndividuals().get(symbol).getExpectedReturn();
                double standardDeviation = mReport.getIndividuals().get(symbol).getStandardDeviation();
                double sharpe = mReport.getIndividuals().get(symbol).getSharpe();
                assetRiskReturnLevels.add(new AssetRiskReturnLevel(symbol, weight, expectedReturn, standardDeviation, sharpe));
            }

            assetRiskReturnLevels.add(new AssetRiskReturnLevel("Total", 1, mPortfolio.getExpectedReturn(), mPortfolio.getStandardDeviation(), mPortfolio.getSharpe()));
            return assetRiskReturnLevels;
        }
    }
}
