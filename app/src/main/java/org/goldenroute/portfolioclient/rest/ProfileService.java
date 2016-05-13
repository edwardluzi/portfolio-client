package org.goldenroute.portfolioclient.rest;

import org.goldenroute.portfolioclient.model.QRCodeTicket;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ProfileService {
    @POST("profile/{pid}/bind")
    Call<Boolean> bindWechat(@Path("pid") Long pid, @Body Integer parameter);
}
