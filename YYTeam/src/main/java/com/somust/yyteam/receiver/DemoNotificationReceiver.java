package com.somust.yyteam.receiver;

import android.content.Context;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by 13160677911 on 2016-11-30.
 */

public class DemoNotificationReceiver extends PushMessageReceiver {
    /**
     * onNotificationMessageArrived 用来接收服务器发来的通知栏消息(消息到达客户端时触发)，默认return false，
     * 通知消息会以融云 SDK 的默认形式展现。如果需要自定义通知栏的展示，
     * 在这里实现自己的通知栏展现代码，同时 return true 即可
     * @param context
     * @param pushNotificationMessage
     * @return
     */
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }

    /**
     * 是在用户点击通知栏消息时触发 (注意:如果自定义了通知栏的展现，则不会触发)，默认 return false 。
     * 如果需要自定义点击通知时的跳转，return true 即可。融云 SDK 默认跳转规则如下
     * 只有一个联系人发来一条或者多条消息时，会通过 intent 隐式启动会话 activity，intent 的 uri 如下
     * @param context
     * @param pushNotificationMessage
     * @return
     */
    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage pushNotificationMessage) {
        return false;
    }
}
