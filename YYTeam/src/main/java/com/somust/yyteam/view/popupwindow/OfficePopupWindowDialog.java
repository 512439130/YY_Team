package com.somust.yyteam.view.popupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.somust.yyteam.R;


/**
 * Created by MMZB-YY on 2017/4/25.
 * 选择查询域的popup_window
 */

public class OfficePopupWindowDialog implements View.OnClickListener {
    /**
     * popupWindow
     */
    private PopupWindow popupWindow;

    /**
     * 确定dialog需要的context
     */
    private Context context;


    /**
     * 确定触发该dialog的view组件，也是创建popupwindow必须的参数
     */
    private View parentView;

    /**
     * Activity
     */
    private Activity mActivity;

    private Callback mCallback;

    /**
     * 自定义接口，用于回调按钮点击事件到Activity
     */
    public interface Callback {
        void cadreClick(View v);  //选择搜索用户点击事件
        void mohamedClick(View v); //选择搜索社团点击事件
    }


    public OfficePopupWindowDialog(Context context, View parentView, Activity mActivity, Callback callback) {
        this.context = context;
        this.parentView = parentView;
        this.mActivity = mActivity;
        this.mCallback = callback;
    }

    /**
     * 创建PopupWindow
     */
    public void create() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View popupView = inflater.inflate(R.layout.pop_list_window, null);
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //该属性设置为true则你在点击屏幕的空白位置也会退出
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //添加弹出、弹入的动画
        popupWindow.setAnimationStyle(R.style.Popupwindow);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int[] location = new int[2];
        popupView.getLocationOnScreen(location);
        //显示popupwindow

        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);


        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.8f;
        mActivity.getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });

        //添加按键事件监听
        setButtonListeners(popupView);

    }


    private void setButtonListeners(View view) {
        TextView team_member_cadre = (TextView) view.findViewById(R.id.team_member_cadre);
        TextView team_member_mohamed = (TextView) view.findViewById(R.id.team_member_mohamed);
        team_member_cadre.setOnClickListener(this);
        team_member_mohamed.setOnClickListener(this);

    }

    //响应按钮点击事件,调用子定义接口，并传入View
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.team_member_cadre:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                mCallback.cadreClick(v);
                break;
            case R.id.team_member_mohamed:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                mCallback.mohamedClick(v);
                break;
        }
    }

}
