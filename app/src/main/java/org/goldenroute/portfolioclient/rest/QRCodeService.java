package org.goldenroute.portfolioclient.rest;


import org.goldenroute.portfolioclient.model.QRCodeTicket;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QRCodeService {
    @POST("accounts/{uid}/qrcode/wechat")
    Call<QRCodeTicket> create(@Path("uid") Long uid);
}
