package org.goldenroute.portfolioclient.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.goldenroute.portfolioclient.R;


public class MainMarketFragment extends MainBaseFragment {


    public MainMarketFragment() {

    }


    public static MainMarketFragment newInstance() {
        MainMarketFragment fragment = new MainMarketFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_market, container, false);
    }


    @Override
    public void refresh() {

    }
}
