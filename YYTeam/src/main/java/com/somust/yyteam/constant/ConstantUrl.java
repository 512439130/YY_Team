package com.somust.yyteam.constant;

/**
 * Created by 13160677911 on 2017-4-1.
 */

public class ConstantUrl extends Constant{

    //user
    public static final String userUrl = BASE_URL+"/renren-security/user/";

    //friend
    public static final String friendUrl = BASE_URL+"/renren-security/friend/";


    //team
    public static final String teamUrl = BASE_URL+"/renren-security/team/";

    //teamMember
    public static final String teamMemberUrl = BASE_URL+"/renren-security/teamMember/";
    //teamTask
    public static final String teamTaskUrl = BASE_URL+"/renren-security/teamTask/";

    //taskMembers
    public static final String taskMemberUrl = BASE_URL+"/renren-security/taskMember/";

    //file
    public static final String FileImageUrl = BASE_URL+"/renren-security/file/";




    //default image
    //man
    public static final String imageDefaultManUrl = BASE_URL+"/image/man_default.png";
    //woman
    public static final String imageDefaultWomanUrl = BASE_URL+"/image/woman_default.png";







    //interface user
    public static final String userRegister_interface = "yy_register?Json=";  //注册
    public static final String userLogin_interface = "yy_login?";  //登录
    public static final String userUpdatePass_interface = "yy_update_pass?";  //修改密码
    public static final String getUserInfo_interface = "yy_get_userinfo?";  //查询用户信息
    public static final String getAllUserInfo_interface = "yy_get_alluserinfo"; //查询系统所有用户


    public static final String updateUserInfo_interface = "yy_update_userInfo?Json="; //完善用户信息


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

    public static final String getMyTeam_interface = "yy_obtain_team_byuser?"; //查询我的社团的编号

    public static final String createTeamNews_interface = "yy_add_team_news?Json=";  //发表社团新闻


    public static final String updateTeamInfo_interface = "yy_update_teamInfo?Json="; //完善社团信息


    //interface teamMember
    public static final String addTeamMemberRequest_interface = "yy_team_member_request?Json=";  //申请加入社团的请求
    public static final String obtainTeamMemberRequest_interface = "yy_obtain_team_member_request?";  //查询哪个用户加社团
    public static final String updateTeamMemberRequest_interface = "yy_operate_team_member_request?";  //处理社团添加请求
    public static final String obtainTeamMember_interface = "yy_obtain_team_member?";//获取社团成员列表


    public static final String updateTeamMemberState_interface = "yy_operate_team_state?";  //处理退出社团

    /**
     * 处理社团成员退出
     *http://localhost:8080/renren-security/teamMember/yy_operate_team_state?teamMemberId=4&teamMemberState = '1'
     */

    //interface teamTask
    public static final String getTeamTask_interface = "yy_obtain_team_task";//获取活动列表
    public static final String getTeamMemberTask_interface = "yy_obtain_team_member_task?";//负责人获取活动列表
    public static final String addTeamTask_interface = "yy_add_team_task?Json=";  //发布社团任务

    public static final String setTeamTaskSummary_interface = "yy_operate_team_task_summary?";  //填写活动总结
    public static final String auditTeamTaskSummary_interface = "yy_audit_team_task_summary?";  //审核活动总结



    public static final String getTeamTaskCount_interface = "yy_obtain_task_count_bytask?";  //查询活动报名人数


    public static final String getTaskByUserId_interface = "yy_obtain_task_byuser?";  //查询我参与的活动


    public static final String getTeamMemberTaskByTeamId_interface = "yy_obtain_team_task_summary?";//负责人获取活动列表
    //社长审核任务
    /**
     * 查询社团的所有任务
     * http://localhost:8080/renren-security/teamTask/yy_obtain_team_task_summary?teamId=1
     */


    //interface teamMember

    public static final String addTaskMemberRequest_interface = "yy_task_member_request?Json=";  //申请报名活动的请求
    public static final String obtainTaskMemberRequest_interface = "yy_obtain_task_member_request?";  //查询哪个用户报名活动
    public static final String updateTaskMemberRequest_interface = "yy_operate_task_member_request?";  //处理用户报名活动请求
    public static final String obtainTaskMember_interface = "yy_obtain_task_member?";//获取活动成员列表
    public static final String addTask_interface = "yy_add_task_member?Json="; //加入活动的处理

    public static final String getTeamMemberByResponsible_interface = "yy_obtain_team_member_by_responsible";   //查询负责人的信息







    //文件上传
    public static final String uploadImage_interface = "yy_upload_file?";  //上传图片到服务器

    //更换头像
    public static final String updateUserImage_interface = "yy_update_userImage?";  //上传图片到服务器





}
