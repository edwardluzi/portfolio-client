package org.goldenroute.portfolioclient;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.goldenroute.portfolioclient.model.MarkowitzPortfolio;
import org.goldenroute.portfolioclient.model.PortfolioReport;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
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
import retrofit2.Call;
import retrofit2.Response;

public class AnalysisPortfolioListActivity extends AppCompatActivity {
    private static final String TAG = AnalysisPortfolioListActivity.class.getName();

    private Long mPortfolioId;
    private PortfolioReport mReport;

    private LineChartView mLineChart;
    private Map<String, MarkowitzPortfolio> mDetails;

    private PieChartView mPieChartCurrent;
    private PieChartView mPieChartSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_portfolio_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_analysis_portfolio_list);
        setSupportActionBar(toolbar);

        mLineChart = (LineChartView) findViewById(R.id.chart_portfolio_analysis_efficient_frontier);
        mLineChart.setOnValueTouchListener(new LineChartValueSelectListener());
        mLineChart.setViewportCalculationEnabled(false);
        mLineChart.setLineChartData(createFakeLineChartData());

        final Viewport v = new Viewport(mLineChart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = 100;
        mLineChart.setMaximumViewport(v);
        mLineChart.setCurrentViewport(v);

        mPieChartCurrent = (PieChartView) findViewById(R.id.chart_portfolio_current_analysis_allocation);
        mPieChartSelected = (PieChartView) findViewById(R.id.chart_portfolio_analysis_selected_allocation);
        mPieChartCurrent.setOnValueTouchListener(new PieChartValueTouchListener());
        mPieChartSelected.setOnValueTouchListener(new PieChartValueTouchListener());

        PieChartData fakePieChartData = createFakePieChartData();
        mPieChartCurrent.setPieChartData(fakePieChartData);
        mPieChartSelected.setPieChartData(fakePieChartData);

        mPortfolioId = getIntent().getLongExtra(IntentConstants.ARG_PID, 0L);
        new ReadingPortfolioReportTask(this).execute((Void) null);
    }

    private LineChartData createFakeLineChartData() {
        mDetails = new HashMap<>();
        List<Line> lines = new ArrayList<>();
        LineChartData lineChartData = new LineChartData(lines);

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);
            axisX.setName("Risk Level (Standard Deviation) x1");
            axisY.setName("Return Level x1");

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);

        return lineChartData;
    }

    private PieChartData createFakePieChartData() {
        List<SliceValue> values = new ArrayList<SliceValue>();
        float[] weights = new float[]{25, 35, 40};

        for (int i = 0; i < weights.length; ++i) {
            SliceValue sliceValue = new SliceValue(weights[i], ChartUtils.COLORS[i % ChartUtils.COLORS.length]);
            sliceValue.setLabel(String.format("%.2f%%", weights[i]));
            values.add(sliceValue);
        }

        PieChartData pieChartData = new PieChartData(values);

        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOnlyForSelected(false);
        pieChartData.setHasLabelsOutside(false);
        pieChartData.setHasCenterCircle(false);

        return pieChartData;
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
            float var = (float) portfolio.getVariance();
            float ret = (float) portfolio.getExpectedReturn();

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

        float increaseX = (right - left) * 0.1f;
        float increaseY = (top - bottom) * 0.1f;

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

    private int calculateScaleX(Viewport viewport) {
        int scaleX = 1;
        float middle = (viewport.left + viewport.right) / 2;

        while (middle * scaleX < 1) {
            scaleX *= 10;
        }

        return scaleX;
    }

    private int calculateScaleY(Viewport viewport) {
        int scaleY = 1;
        float middle = (viewport.top + viewport.bottom) / 2;

        while (middle * scaleY < 1) {
            scaleY *= 10;
        }

        return scaleY;
    }

    private void adjustViewport(Viewport viewport, int scaleX, int scaleY) {
        viewport.left *= scaleX;
        viewport.right *= scaleX;
        viewport.top *= scaleY;
        viewport.bottom *= scaleY;
    }

    private LineChartData loadLineChartData(PortfolioReport report, int scaleX, int scaleY) {

        mDetails = new HashMap<>();

        List<Line> lines = new ArrayList<>();
        lines.add(loadEfficientFrontierLine(report, scaleX, scaleY));
        lines.add(loadIndividualLine(report, scaleX, scaleY));

        LineChartData lineChartData = new LineChartData(lines);

        Axis axisX = new Axis().setHasLines(true);
        Axis axisY = new Axis().setHasLines(true);

        if (scaleX == 1) {
            axisX.setName("Risk Level (Standard Deviation) x1");
        } else {
            axisX.setName(String.format("Risk Level (Standard Deviation) x%." + Integer.toString((int) Math.log10(scaleX)) + "f", 1.0 / (float) scaleX));
        }

        if (scaleY == 1) {
            axisY.setName("Return Level x1");
        } else {
            axisY.setName(String.format("Return Level x%." + Integer.toString((int) Math.log10(scaleY)) + "f", 1.0 / (float) scaleY));
        }

        lineChartData.setAxisXBottom(axisX);
        lineChartData.setAxisYLeft(axisY);
        lineChartData.setBaseValue(Float.NEGATIVE_INFINITY);

        return lineChartData;
    }

    private String formatWeights(List<String> symbols, MarkowitzPortfolio portfolio) {
        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < symbols.size() && index < portfolio.getWeights().length; index++) {
            builder.append(String.format("[%s: %.3f]", symbols.get(index), portfolio.getWeights()[index]));
        }

        return builder.toString();
    }

    private Line loadEfficientFrontierLine(PortfolioReport report, int scaleX, int scaleY) {
        List<PointValue> values = new ArrayList<>();

        for (MarkowitzPortfolio portfolio : report.getEfficientFrontier().getFrontiers()) {
            float var = (float) portfolio.getVariance() * scaleX;
            float ret = (float) portfolio.getExpectedReturn() * scaleY;
            String label = String.format("%.3f, %.3f", ret, var);
            values.add(new PointValue(var, ret).setLabel(label));
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

    private Line loadIndividualLine(PortfolioReport report, int scaleX, int scaleY) {
        List<PointValue> values = new ArrayList<>();

        for (Map.Entry<String, MarkowitzPortfolio> portfolio : report.getIndividuals().entrySet()) {
            float var = (float) portfolio.getValue().getVariance() * scaleX;
            float ret = (float) portfolio.getValue().getExpectedReturn() * scaleY;
            values.add(new PointValue(var, ret).setLabel(portfolio.getKey()));
            mDetails.put(portfolio.getKey(), portfolio.getValue());
        }

        float var = (float) report.getOverall().getVariance() * scaleX;
        float ret = (float) report.getOverall().getExpectedReturn() * scaleY;
        values.add(new PointValue(var, ret).setLabel("#CUR"));
        mDetails.put("#CUR", report.getOverall());

        var = (float) report.getEfficientFrontier().getGlobalMinimumVariance().getVariance() * scaleX;
        ret = (float) report.getEfficientFrontier().getGlobalMinimumVariance().getExpectedReturn() * scaleY;
        values.add(new PointValue(var, ret).setLabel("#GMV"));
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


    private PieChartData loadPieChartData(PortfolioReport report, MarkowitzPortfolio portfolio) {
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

    private void refresh(PortfolioReport report) {
        mReport = report;

        final Viewport viewport = calculateViewPort(report);
        int scaleX = calculateScaleX(viewport);
        int scaleY = calculateScaleX(viewport);
        adjustViewport(viewport, scaleX, scaleY);

        final LineChartData lineChartData = loadLineChartData(report, scaleX, scaleY);
        final PieChartData currentPieChartData = loadPieChartData(report, report.getOverall());
        final String portfolioName = report.getPortfolioName();

        mLineChart.post(new Runnable() {
            public void run() {
                mLineChart.setLineChartData(lineChartData);
                mLineChart.setMaximumViewport(viewport);
                mLineChart.setCurrentViewportWithAnimation(viewport);
                mPieChartCurrent.setPieChartData(currentPieChartData);
                mPieChartSelected.setPieChartData(currentPieChartData);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(portfolioName);
                }
            }
        });
    }

    private class LineChartValueSelectListener implements LineChartOnValueSelectListener {
        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            String label = new String(value.getLabelAsChars());
            if (mDetails.containsKey(label)) {
                final PieChartData selectedPieChartData = loadPieChartData(mReport, mDetails.get(label));
                mPieChartSelected.post(new Runnable() {
                    public void run() {
                        mPieChartSelected.setPieChartData(selectedPieChartData);
                    }
                });
            }
        }

        @Override
        public void onValueDeselected() {
        }
    }

    private class PieChartValueTouchListener implements PieChartOnValueSelectListener {
        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            if (mReport != null) {
                Toast.makeText(AnalysisPortfolioListActivity.this, mReport.getSymbols().get(arcIndex), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onValueDeselected() {
        }
    }

    public class ReadingPortfolioReportTask extends RestAsyncTask<Void, Void, Boolean> {
        private PortfolioReport mReport;

        public ReadingPortfolioReportTask(Activity activity) {
            super(activity, true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Call<PortfolioReport> call = RestOperations.getInstance().getPortfolioService().analysis(mPortfolioId);
                Response<PortfolioReport> response = call.execute();
                if (response.isSuccessful()) {
                    mReport = response.body();
                }
                if (mReport == null) {
                    parseError(response);
                }
            } catch (Exception e) {
                mReport = null;
                parseError(e);
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);
            if (success && mReport != null) {
                refresh(mReport);
            } else {
                Toast.makeText(getParentActivity(),
                        String.format(Locale.getDefault(), getString(R.string.message_retrieving_portfolio_report_failed), getError()),
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
