package org.goldenroute.portfolioclient.rest;

import org.goldenroute.portfolioclient.model.Account;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface AccountService {
    @GET("accounts/{uid}")
    Call<Account> getAccount(@Path("uid") Long uid);
}
