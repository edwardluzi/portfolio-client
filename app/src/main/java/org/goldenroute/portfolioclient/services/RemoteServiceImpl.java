package org.goldenroute.portfolioclient.services;

import android.content.Context;

import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.model.PortfolioReport;
import org.goldenroute.portfolioclient.model.PortfolioReportParameters;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;

public class RemoteServiceImpl implements RemoteService {

    public void generatePortfolioReport(final Context context, final Long portfolioId, final double riskFree, final double riskAversion, final String intervals, final CallbackListener<PortfolioReport> listener) {
        if (portfolioId == null || portfolioId <= 0) {
            if (listener != null) {
                listener.onFailure("Invalid portfolio id.");
            }
        }
        new RestAsyncTask<Void, Void, Boolean>(context, true) {
            private PortfolioReport mReport;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    PortfolioReportParameters parameters = new PortfolioReportParameters(riskFree, riskAversion, false, intervals);
                    Call<PortfolioReport> call = RestOperations.getInstance().getPortfolioService().analysis(portfolioId, parameters);
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
                if (listener != null) {
                    if (success && mReport != null) {
                        listener.onSuccess(mReport);
                    } else {
                        listener.onFailure(
                                String.format(Locale.getDefault(), getContext().getString(R.string.message_retrieving_portfolio_report_failed), getError())
                        );
                    }
                }
            }
        }.execute();
    }
}
