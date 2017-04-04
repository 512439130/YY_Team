package com.somust.yy.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yy.R;

/**
 * Created by tangyangkai on 16/5/10.
 */
public class SecondFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_second;
    }

    public static String SECOND_FRAGMENT = "second_fragment";
    private String msg;
    private TextView secondTxt;
    private ImageView backIMg;

    public static SecondFragment newInstance(String msg) {
        SecondFragment fragment = new SecondFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SECOND_FRAGMENT, msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getArguments()) {
            msg = (String) getArguments().getSerializable(SECOND_FRAGMENT);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

        backIMg = (ImageView) view.findViewById(R.id.second_back);
        backIMg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });
        secondTxt= (TextView) view.findViewById(R.id.second_txt);
        secondTxt.setText(msg);
    }


}
