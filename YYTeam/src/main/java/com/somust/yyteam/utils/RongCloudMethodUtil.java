package com.somust.yyteam.utils;








import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
/**
 * Created by 13160677911 on 2016-12-1.
 */

public class RongCloudMethodUtil {
    /**
     * 获取token
     *
     * @param userId 用户 Id，最大长度 64 字节。是用户在 App 中的唯一标识码，必须保证在同一个 App 内不重复，重复的用户 Id 将被当作是同一用户
     * @param name 用户名称，最大长度 128 字节。用来在 Push 推送时显示用户的名称。（必传）
     * @param portraitUri 用户头像 URI，最大长度 1024 字节。用来在 Push 推送时显示用户的头像。（必传）
     */
    public static String getToken(String userId, String name, String portraitUri) {
        String getToken = "http://api.cn.ronghub.com/user/getToken.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("name", name);
        params.put("portraitUri", portraitUri);
        byte[] resultArray;
        String token = null;
        try {
            resultArray = RongCloudUtil.post(getToken, params, "UTF-8", 20000);
            String result = new String(resultArray);
            //这里使用阿里json工具包，用parseObject替换之前的fromObject
            JSONObject obj = JSONObject.parseObject(result);
            token = obj.get("token").toString();
            System.out.println("获取token成功—token="+token);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("没获取到token");
        }
        return token;
    }

    /**
     * 推送系统信息
     *
     * @param content 消息内容
     * @param fromUserId 1
     * @param toUserId  userId
     * @param objectName  RC:TxtMsg
     * @param pushContent 消息标题
     * @param pushData 空-安卓  非空：苹果
     */
    public static void pushSystemMessage(String content, String fromUserId,
                                         String toUserId, String objectName, String pushContent,
                                         String pushData) {

        String systemMessage = "https://api.cn.rong.io/message/system/publish.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("toUserId", toUserId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(systemMessage, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("发送信息出错了");
        }

    }

    /**
     * 刷新个人信息
     *
     * @param userId
     * @param name
     * @param portraitUri
     */
    public static void refreshUserInformation(String userId, String name,
                                              String portraitUri) {
        String refresh = "https://api.cn.rong.io/user/refresh.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("name", name);
        params.put("portraitUri", portraitUri);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(refresh, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("哎呀，刷新没成功");
        }
    }

    /**
     * 检查某人是否在线
     *
     * @param userId
     */
    public static void checkOnline(String userId) {
        String checkOnline = "https://api.cn.rong.io/user/checkOnline.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(checkOnline, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("系统维护");
        }
    }

    /**
     * 禁言
     */
    public static void block(String userId, String minute) {
        String block = "https://api.cn.rong.io/user/block.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("minute", minute);// 禁言时间，单位为分钟
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(block, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("禁言没成功");
        }
    }

    /**
     * 解禁用户
     *
     * @param userId
     */
    public static void unblock(String userId) {
        String unblock = "https://api.cn.rong.io/user/unblock.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(unblock, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("解禁没成功");
        }
    }

    /**
     * 查询被禁人员
     */
    public static void queryBlack() {
        String query = "https://api.cn.rong.io/user/block/query.json";
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(query, null, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("查询没成功");
        }
    }

    /**
     * 添加黑名单
     */
    public static void addBlack(String userId, String blackUserId) {
        String add = "https://api.cn.rong.io/user/blacklist/add.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("blackUserId", blackUserId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(add, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("添加没成功");
        }
    }

    /**
     * 删除黑名单
     *
     * @param userId
     * @param blackUserId
     */
    public static void removeBlack(String userId, String blackUserId) {
        String remove = "https://api.cn.rong.io/user/blacklist/remove.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("blackUserId", blackUserId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(remove, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("删除没成功");
        }
    }

    /**
     * 查询所有加黑用户
     */
    public static void blacklist(String userId, String blackUserId) {
        String blacklist = "https://api.cn.rong.io/user/blacklist/query.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("blackUserId", blackUserId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(blacklist, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("查询加黑名单异常");
        }
    }

    /**
     * 单聊
     */
    public static void oneToOneMessege(String content, String fromUserId,
                                       String toUserId, String objectName, String pushContent,
                                       String pushData) {
        String oneToOne = "https://api.cn.rong.io/message/private/publish.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("toUserId", toUserId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(oneToOne, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("单聊信息发送异常");
        }
    }

    /**
     * 发送群信息
     *
     * @param content
     * @param fromUserId
     * @param toGroupId
     * @param objectName
     * @param pushContent
     * @param pushData
     */
    public static void groupMessege(String content, String fromUserId,
                                    String toGroupId, String objectName, String pushContent,
                                    String pushData) {
        String groupMessege = "https://api.cn.rong.io/message/group/publish.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("toGroupId", toGroupId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(groupMessege, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("群组信息发送异常");
        }
    }

    /**
     * 发送聊天室消息
     *
     * @param content
     * @param fromUserId
     * @param toGroupId
     * @param objectName
     * @param pushContent
     * @param pushData
     */
    public static void chatroom(String content, String fromUserId,
                                String toGroupId, String objectName, String pushContent,
                                String pushData) {
        String chatroom = "https://api.cn.rong.io/message/chatroom/publish.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("toGroupId", toGroupId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(chatroom, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("聊天信息发送异常");
        }
    }

    /**
     * 发送广播消息
     */
    public static void broadcast(String content, String fromUserId,
                                 String objectName, String pushContent, String pushData) {
        String broadcast = "https://api.cn.rong.io/message/broadcast.json";
        Map<String, String> params = new HashMap<String, String>();
        // String content="{\"content\":\"2\"}";
        params.put("content", content);
        params.put("fromUserId", fromUserId);
        params.put("objectName", objectName);
        params.put("pushContent", pushContent);
        params.put("pushData", pushData);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(broadcast, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("广播发送异常");
        }
    }

    /**
     * 查某个时段会话历史
     */
    public static void history(String date) {
        String history = "https://api.cn.rong.io/message/history.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(history, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("查找历史记录异常");
        }
    }

    /**
     * 删除信息记录
     */
    public static void deleteHistory(String date) {
        String deleteHistory = "https://api.cn.rong.io/message/history/delete.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("date", date);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(deleteHistory, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("删除群组异常");
        }
    }

    /**
     * 同步群组信息
     *
     * @param group
     * @param userId
     */
    public static void groupSync(String[] group, String userId) {
        String groupSync = "https://api.cn.rong.io/group/sync.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", "1");
        for (int i = 0; i < group.length; i++) {
            params.put("group[" + i + "]", group[i]);
        }
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(groupSync, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("同步群组信息异常");
        }
    }

    /**
     * 创建群组
     */
    public static void createGroup(String userId, String groupId,
                                   String groupName) {
        String createGroup = "https://api.cn.rong.io/group/create.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("groupId", groupId);
        params.put("groupName", groupName);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(createGroup, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("创建群组异常");
        }
    }

    /**
     * 加入群组
     */
    public static void joinGroup(String userId, String groupId, String groupName) {
        String joinGroup = "https://api.cn.rong.io/group/join.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("groupId", groupId);
        params.put("groupName", groupName);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(joinGroup, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("加入群组异常");
        }
    }

    /**
     * 退出群组
     */
    public static void quitGroup(String userId, String groupId) {
        String quitGroup = "https://api.cn.rong.io/group/quit.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("groupId", groupId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(quitGroup, params, "UTF-8", 20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("退出群组异常");
        }
    }

    /**
     * 解散群组
     */
    public static void dismissGroup(String userId, String groupId) {
        String dismissGroup = "https://api.cn.rong.io/group/dismiss.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("groupId", groupId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(dismissGroup, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("解散群组异常");
        }
    }

    /**
     * 刷新群组
     */
    public static void refreshGroup(String userId, String groupId,
                                    String groupName) {
        String refreshGroup = "https://api.cn.rong.io/group/refresh.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", userId);
        params.put("groupId", groupId);
        params.put("groupName", groupName);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(refreshGroup, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("刷新群组异常");
        }
    }

    /**
     * 创建聊天室
     *
     * @param chartroom
     */
    public static void createChatroom(String[] chartroom) {
        String refreshGroup = "https://api.cn.rong.io/chatroom/create.json";
        Map<String, String> params = new HashMap<String, String>();
        for (int i = 0; i < chartroom.length; i++) {
            params.put("chartroom[" + i + "]", "testChartRoom" + i);
        }
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(refreshGroup, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("创建聊天室异常");
        }
    }

    /**
     * 注销聊天室
     */
    public static void destroyChatroom(String chatroomId) {
        String destroyChatroom = "https://api.cn.rong.io/chatroom/destroy.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("chatroomId", chatroomId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(destroyChatroom, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("注销异常");
        }
    }

    /**
     * 查询某个聊天室
     *
     * @param chatroomId
     */
    public static void queryChatroom(String chatroomId) {
        String queryChatroom = "https://api.cn.rong.io/chatroom/query.json";
        Map<String, String> params = new HashMap<String, String>();
        params.put("chatroomId", chatroomId);
        byte[] resultArray;
        try {
            resultArray = RongCloudUtil.post(queryChatroom, params, "UTF-8",
                    20000);
            String result = new String(resultArray);
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("查询聊天室异常");
        }
    }
}
