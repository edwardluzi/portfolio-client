package org.goldenroute.portfolioclient.rest;

import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.model.Transaction;

import java.util.Set;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TransactionService {
    @POST("portfolios/{pid}/transactions")
    Call<Portfolio> create(@Path("pid") Long pid, @Body Transaction transaction);

    @PUT("portfolios/{pid}/transactions/{tid}")
    Call<Portfolio> update(@Path("pid") Long pid, @Path("tid") Long tid, @Body Transaction transaction);

    @DELETE("portfolios/{pid}/transactions/{tids}")
    Call<Portfolio> delete(@Path("pid") Long pid, @Path("tids") String tids);
}
