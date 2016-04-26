package org.goldenroute.portfolioclient.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.goldenroute.portfolioclient.AboutActivity;
import org.goldenroute.portfolioclient.PortfolioActivity;
import org.goldenroute.portfolioclient.ProfileActivity;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.SettingsActivity;
import org.goldenroute.portfolioclient.model.Account;
import org.goldenroute.portfolioclient.model.Portfolio;
import org.goldenroute.portfolioclient.rest.RestAsyncTask;
import org.goldenroute.portfolioclient.rest.RestOperations;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public abstract class MainBaseFragment extends RefreshableFragment {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.getActivity().startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        } else if (id == R.id.action_profile) {
            this.getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            this.getActivity().startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
