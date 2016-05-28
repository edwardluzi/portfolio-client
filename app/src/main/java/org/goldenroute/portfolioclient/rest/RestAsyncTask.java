package org.goldenroute.portfolioclient.rest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.goldenroute.portfolioclient.R;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;

public abstract class RestAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final String TAG = RestAsyncTask.class.getName();

    private ProgressDialog mProgressDialog;
    private Activity mParentActivity;
    private String mError;

    protected Activity getParentActivity() {
        return mParentActivity;
    }

    protected ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    protected String getError() {
        return mError;
    }

    protected RestAsyncTask(Activity activity, boolean showDialog) {
        mParentActivity = activity;
        mError = "";
        if (showDialog) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null) {
            mProgressDialog.setMessage( mParentActivity.getString(R.string.message_please_wait));
            mProgressDialog.show();
        }
    }


    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected String parseError(Response<?> response) {
        HashMap<String, String> map = null;
        String body = null;
        try {
            body = response.errorBody().string();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (!TextUtils.isEmpty(body)) {
            try {
                map = new Gson().fromJson(body,
                        new TypeToken<HashMap<String, String>>() {
                        }.getType());
            } catch (JsonSyntaxException e) {
                Log.e(TAG, e.getMessage());
            }

            if (map == null) {
                try {
                    Elements elements = Jsoup.parse(body).getElementsByTag("title");
                    if (elements.size() > 0) {
                        map = new HashMap<>();
                        map.put("error_description", elements.get(0).text());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }

        if (map != null && map.containsKey("error_description")) {
            mError = map.get("error_description");
        } else if (map != null && map.containsKey("error")) {
            mError = map.get("error");
        } else {
            mError = response.message();
            if (TextUtils.isEmpty(mError)) {
                mError = mParentActivity.getString(R.string.message_unknown_error);
            }
        }

        return mError;
    }

    protected String parseError(Exception e) {
        Throwable throwable = e.getCause();
        if (throwable != null) {
            mError = throwable.getMessage();
            if (!TextUtils.isEmpty(mError)) {
                return mError;
            }
            mError = throwable.getLocalizedMessage();
            if (!TextUtils.isEmpty(mError)) {
                return mError;
            }
        }

        mError = e.getMessage();
        if (!TextUtils.isEmpty(mError)) {
            return mError;
        }

        mError = e.getLocalizedMessage();
        if (!TextUtils.isEmpty(mError)) {
            return mError;
        }

        mError = e.toString();
        return mError;
    }
}

