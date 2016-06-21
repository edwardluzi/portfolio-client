package org.goldenroute.portfolioclient.services;

import android.content.Context;

import org.goldenroute.portfolioclient.model.PortfolioReport;

public interface RemoteService {
    void generatePortfolioReport(final Context context, final Long portfolioId, final double riskFree, final double riskAversion, final String intervals, final CallbackListener<PortfolioReport> listener);
}
