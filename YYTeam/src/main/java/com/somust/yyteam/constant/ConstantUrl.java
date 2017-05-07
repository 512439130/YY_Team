package com.somust.yyteam.constant;

/**
 * Created by 13160677911 on 2017-4-1.
 */

public class ConstantUrl {
    //test
    public static final String mTestUrl = "http://139.199.171.79/renren-security/";  //gethtml


    //user
    public static final String userUrl = "http://139.199.171.79/renren-security/user/";

    //friend
    public static final String friendUrl = "http://139.199.171.79/renren-security/friend/";


    //team
    public static final String teamUrl = "http://139.199.171.79/renren-security/team/";

    //teamMember
    public static final String teamMemberUrl = "http://139.199.171.79/renren-security/teamMember/";


    //file
    public static final String FileImageUrl = "http://139.199.171.79/renren-security/file/";




    //default image
    //man
    public static final String imageDefaultManUrl = "http://139.199.171.79/image/man_default.png";
    //woman
    public static final String imageDefaultWomanUrl = "http://139.199.171.79/image/woman_default.png";







    //interface user
    public static final String userRegister_interface = "yy_register?Json=";  //注册
    public static final String userLogin_interface = "yy_login?";  //登录
    public static final String userUpdatePass_interface = "yy_update_pass?";  //修改密码
    public static final String getUserInfo_interface = "yy_get_userinfo?";  //查询用户信息
    public static final String getAllUserInfo_interface = "yy_get_alluserinfo"; //查询系统所有用户



    //interface friend
    public static final String friend_interface = "yy_obtain_friend?";
    public static final String addFriendRequest_interface = "yy_friend_request?Json=";  //添加好友的请求
    public static final String obtainFriendRequest_interface = "yy_obtain_friendrequest?";  //查询哪个好友加我
    public static final String updateFriendRequest_interface = "yy_operate_friendrequest?";  //处理好友请求
    public static final String addFriend_interface = "yy_add_friend?";  //互相加入好友列表



    //interface team
    public static final String getTeamNews_interface = "yy_obtain_team_news"; //社团新闻

    public static final String getTeam_interface = "yy_obtain_team"; //获取全部社团

    public static final String createTeam_interface = "yy_add_team?Json=";  //创建社团

    public static final String addTeam_interface = "yy_add_team_member?Json="; //加入社团的处理

    public static final String getTeamId_interface = "yy_obtain_team_byuser?"; //查询我的社团的编号




    //interface teamMember
    public static final String addTeamMemberRequest_interface = "yy_team_member_request?Json=";  //申请加入社团的请求
    public static final String obtainTeamMemberRequest_interface = "yy_obtain_team_member_request?";  //查询哪个用户加社团
    public static final String updateTeamMemberRequest_interface = "yy_operate_team_member_request?";  //处理社团添加请求
    public static final String obtainTeamMember_interface = "yy_obtain_team_member?";//获取社团成员列表
    /**
     * 社团添加请求
     * http://localhost:8080/renren-security/teamMember/yy_team_member_request json
     *
     */

    /**
     * 查询哪个用户添加我的社团
     * http://localhost:8080/renren-security/teamMember/yy_obtain_friendrequest?receiveId=9&teamMemberRequestState=insert
     */
    /**
     * 处理社团添加请求
     *http://localhost:8080/renren-security/teamMember/yy_operate_team_member_request?requestUserId=4&receiveTeamId=9&teamMemberRequestState=agree
     */
    /**
     * 加入社团处理
     *
     * http://localhost:8080/renren-security/team/yy_add_team_member?Json=(包含User的Json)
     */

    //文件上传
    public static final String uploadImage_interface = "yy_upload_file?";  //上传图片到服务器

    //更换头像
    public static final String updateUserImage_interface = "yy_update_userImage?";  //上传图片到服务器





}
