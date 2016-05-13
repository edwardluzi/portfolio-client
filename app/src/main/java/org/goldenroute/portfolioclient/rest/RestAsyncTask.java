package org.goldenroute.portfolioclient.rest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.goldenroute.portfolioclient.R;

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
        this.mParentActivity = activity;
        this.mError = "";
        if (showDialog) {
            this.mProgressDialog = new ProgressDialog(activity);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
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
            this.mProgressDialog.setMessage("Please waiting...");
            this.mProgressDialog.show();
        }
    }


    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (this.mProgressDialog != null) {
            this.mProgressDialog.dismiss();
            this.mProgressDialog = null;
        }
    }

    protected String parseError(Response<?> response) {
        HashMap<String, String> map = null;
        try {
            map = new Gson().fromJson(response.errorBody().string(),
                    new TypeToken<HashMap<String, String>>() {
                    }.getType());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        if (map != null && map.containsKey("error_description")) {
            mError = map.get("error_description");
        } else if (map != null && map.containsKey("error")) {
            mError = map.get("error");
        } else {
            mError = response.message();
            if (mError.length() == 0) {
                mError = mParentActivity.getString(R.string.message_unknown_error);
            }
        }

        return mError;
    }

    protected String parseError(Exception e) {
        mError = e.getMessage();
        return mError;
    }
}

