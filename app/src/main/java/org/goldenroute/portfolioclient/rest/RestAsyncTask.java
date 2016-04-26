package org.goldenroute.portfolioclient.rest;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public abstract class RestAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private ProgressDialog mProgressDialog;
    private Activity mParentActivity;

    protected Activity getParentActivity() {
        return mParentActivity;
    }

    protected ProgressDialog getmProgressDialog() {
        return mProgressDialog;
    }

    protected RestAsyncTask(Activity activity, boolean showDialog) {
        this.mParentActivity = activity;
        if(showDialog) {
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
        if(mProgressDialog != null) {
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
}

