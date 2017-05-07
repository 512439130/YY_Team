package com.somust.yyteam.view.searchview;

import android.app.Activity;
import android.content.Context;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.somust.yyteam.R;

/**
 * Created by 13160677911 on 2017-4-22.
 */

public class SearchView extends LinearLayout implements View.OnClickListener {
    /**
     * 输入框
     */
    private EditText etInput;

    /**
     * 删除键
     */
    private ImageView ivDelete;

    /**
     * 返回按钮
     */
    private ImageView iv_reutrn;

    /**
     * 上下文对象
     */
    private Context mContext;


    /**
     * 搜索回调接口
     */
    private SearchViewListener mListener;


    /**
     * 设置搜索回调接口
     *
     * @param listener 监听者
     */
    public void setSearchViewListener(SearchViewListener listener) {
        mListener = listener;
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_search_user_view, this);
        initViews();
    }

    private void initViews() {
        etInput = (EditText) findViewById(R.id.search_et_input);


        ivDelete = (ImageView) findViewById(R.id.search_iv_delete);
        iv_reutrn = (ImageView) findViewById(R.id.id_title_back);


        ivDelete.setOnClickListener(this);
        iv_reutrn.setOnClickListener(this);
        etInput.addTextChangedListener(new EditChangedListener());
        etInput.setOnClickListener(this);
        etInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    notifyStartSearching(etInput.getText().toString());
                }
                return true;
            }
        });
    }

    /**
     * 通知监听者 进行搜索操作
     *
     * @param text
     */
    private void notifyStartSearching(String text) {
        if (mListener != null) {
            mListener.onSearch(etInput.getText().toString());
        }
        HideKeyboard(0);
    }

    /**
     * 隐藏软键盘,如果键盘隐藏，那就显示，如果显示，那就隐藏
     */
    private void HideKeyboard(int showFlags) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(showFlags, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 强制隐藏软键盘
     *
     * @param view
     */
    private void ForceHidekeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 检查软键盘是否打开
     */
    private boolean CheckKeyboard() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        if (isOpen) {
            System.out.println("软键盘打开");
        } else {
            System.out.println("软键盘关闭");
        }
        return isOpen;
    }

    private class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!"".equals(charSequence.toString())) {
                ivDelete.setVisibility(VISIBLE);

            } else {
                ivDelete.setVisibility(GONE);

            }

        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_iv_delete:
                etInput.setText("");
                ivDelete.setVisibility(GONE);

                break;
            case R.id.id_title_back:
                ForceHidekeyboard(view);  //强制隐藏软键盘

                ((Activity) mContext).finish();
                break;
        }
    }


    /**
     * search view回调方法
     */
    public interface SearchViewListener {


        /**
         * 开始搜索
         *
         * @param text 传入输入框的文本
         */
        void onSearch(String text);

    }
}
