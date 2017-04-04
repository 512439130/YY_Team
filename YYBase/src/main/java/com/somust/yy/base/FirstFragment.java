package com.somust.yy.base;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.somust.yy.R;

/**
 * Created by tangyangkai on 16/5/10.
 */
public class FirstFragment extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_first;
    }

    public static String FIRST_FRAGMENT = "first_fragment";
    private String msg;
    private EditText usernameEdt;
    private TextView registerTxt, promiseTxt;
    private ImageView backImg;

    public static FirstFragment newInstance(String msg) {
        FirstFragment fragment = new FirstFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(FIRST_FRAGMENT, msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != getArguments()) {
            msg = (String) getArguments().getSerializable(FIRST_FRAGMENT);
        }
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        usernameEdt = (EditText) view.findViewById(R.id.username_edt);
        usernameEdt.setText(msg);
        registerTxt = (TextView) view.findViewById(R.id.register_txt);
        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(SecondFragment.newInstance("从登录界面跳转过来的"));
            }
        });

        backImg = (ImageView) view.findViewById(R.id.first_back);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment();
            }
        });

        promiseTxt = (TextView) view.findViewById(R.id.promise_txt);
        promiseTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(ThirdFragment.newInstance());
            }
        });

    }


}
