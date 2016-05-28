package org.goldenroute.portfolioclient.rest;


import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.model.PortfolioReport;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PortfolioService {
    @POST("portfolios")
    Call<Portfolio> create(@Body Portfolio portfolio);

    @PUT("portfolios/{pid}")
    Call<Portfolio> update(@Path("pid") Long pid, @Body Portfolio portfolio);

    @DELETE("portfolios/{pids}")
    Call<Boolean> delete(@Path("pids") String pids);

    @GET("portfolios/{pid}/analysis")
    Call<PortfolioReport> analysis(@Path("pid") Long pid);
}
