package org.goldenroute.portfolioclient.rest;


import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.goldenroute.portfolioclient.ClientContext;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.signin.SignInManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestOperations {

    private static RestOperations mInstance;

    public static RestOperations getInstance() {
        if (mInstance == null) {
            throw new RuntimeException("Please invoke initialize method first.");
        }

        return mInstance;
    }

    public static void initialize(Activity activity) {
        mInstance = new RestOperations();
        mInstance.create(activity);
    }

    private AccountService mAccountService;
    private PortfolioService mPortfolioService;
    private QRCodeService mQRCodeService;
    private TransactionService mTransactionService;
    private ProfileService mProfileService;

    private String mAccessToken;

    private RestOperations() {
    }

    private void create(Activity activity) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String baseUrl = prefs.getString("pref_key_server_address", activity.getString(R.string.pref_default_server_address)) + "/api/v1/";
        ClientContext clientContext = ClientContext.getInstance();
        SignInManager signInManager = clientContext.getSignInManager();
        mAccessToken = signInManager.getToken().getAccessToken();

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("Authorization", "Bearer " + mAccessToken).build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        builder.readTimeout(90, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong());
                    }
                })
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        return src == null ? null : new JsonPrimitive(src.getTime());
                    }
                })
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        mAccountService = retrofit.create(AccountService.class);
        mPortfolioService = retrofit.create(PortfolioService.class);
        mTransactionService = retrofit.create(TransactionService.class);
        mQRCodeService = retrofit.create(QRCodeService.class);
        mProfileService = retrofit.create(ProfileService.class);
    }

    public AccountService getAccountService() {
        return mAccountService;
    }

    public PortfolioService getPortfolioService() {
        return mPortfolioService;
    }

    public TransactionService getTransactionService() {
        return mTransactionService;
    }

    public QRCodeService getQRCodeService() {
        return mQRCodeService;
    }

    public ProfileService getProfileService() {
        return mProfileService;
    }
}
