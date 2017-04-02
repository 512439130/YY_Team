package com.somust.yyteam.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabFragment extends Fragment {
    private String mTitle = "Default";

    public static final String TITLE = "title";


    public static TabFragment instance = null;//单例模式

    public static TabFragment getInstance() {
        if (instance == null) {
            instance = new TabFragment();

        }

        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if(getArguments() != null){
            mTitle = getArguments().getString(TITLE);
        }
        TextView tv = new TextView(getActivity());
        tv.setTextSize(30);
        tv.setBackgroundColor(Color.parseColor("#ffffff"));
        tv.setText(mTitle);
        return tv;
    }

}
