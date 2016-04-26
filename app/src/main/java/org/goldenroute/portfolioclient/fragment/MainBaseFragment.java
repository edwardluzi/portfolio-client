package org.goldenroute.portfolioclient.fragment;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.goldenroute.portfolioclient.AboutActivity;
import org.goldenroute.portfolioclient.ProfileActivity;
import org.goldenroute.portfolioclient.R;
import org.goldenroute.portfolioclient.SettingsActivity;

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

    public void setTitle(String title) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null) {
            activity.setTitle(title);
            ActionBar toolbar = activity.getSupportActionBar();
            if (toolbar != null) {
                toolbar.setTitle(title);
            }
        }
    }
}
