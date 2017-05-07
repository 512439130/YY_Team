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

import com.somust.yyteam.R;


/**
 * Created by MMZB-YY on 2017/4/25.
 * 选择查询域的popup_window
 */

public class SelectSearchPopupWindowDialog implements View.OnClickListener {
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
        void selectUserClick(View v);  //选择搜索用户点击事件

        void selectTeamClick(View v); //选择搜索社团点击事件
    }


    public SelectSearchPopupWindowDialog(Context context, View parentView, Activity mActivity, Callback callback) {
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
        View popupView = inflater.inflate(R.layout.pop_search_select, null);
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

        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, -230);


        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.15f;
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
        LinearLayout select_search_user = (LinearLayout) view.findViewById(R.id.select_search_user);
        LinearLayout select_search_team = (LinearLayout) view.findViewById(R.id.select_search_team);
        select_search_user.setOnClickListener(this);
        select_search_team.setOnClickListener(this);

    }

    //响应按钮点击事件,调用子定义接口，并传入View
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.select_search_user:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                mCallback.selectUserClick(v);
                break;
            case R.id.select_search_team:
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                mCallback.selectTeamClick(v);
                break;
        }
    }

}
