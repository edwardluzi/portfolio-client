package org.goldenroute.portfolioclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.goldenroute.portfolioclient.ClientContext;
import org.goldenroute.portfolioclient.R;


public class MainAlertsFragment extends MainBaseFragment {

    public MainAlertsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_alerts, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.setTitle(getString(R.string.label_nav_alerts));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuItemProfile = menu.findItem(R.id.action_profile);

        if (ClientContext.getInstance().getAccount() != null) {
            menuItemProfile.setEnabled(true);
        } else {
            menuItemProfile.setEnabled(false);
        }
    }


    @Override
    public void refresh() {
    }
}
