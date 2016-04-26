package org.goldenroute.portfolioclient.rest;


import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;

import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PortfolioService {
    @POST("accounts/{uid}/portfolios")
    Call<Portfolio> create(@Path("uid") Long uid, @Body Portfolio portfolio);

    @PUT("accounts/{uid}/portfolios/{pid}")
    Call<Portfolio> update(@Path("uid") Long uid, @Path("pid") Long pid, @Body Portfolio portfolio);

    @DELETE("accounts/{uid}/portfolios/{pids}")
    Call<Boolean> delete(@Path("uid") Long uid, @Path("pids") String pids);
}
