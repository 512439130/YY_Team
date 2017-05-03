package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.somust.yyteam.R;
import com.somust.yyteam.bean.FriendRequestUser;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.Constant;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.DateUtil;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.utils.photo.PhotoUtils;
import com.somust.yyteam.view.ImageViewPlus;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;


/**
 * Created by 13160677911 on 2017-5-3.
 * 创建社团
 */

public class CreateTeamActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CreateTeamActivity:";

    private Team team = new Team();

    private User user;  //保存传递的用户数据
    private Intent intent;

    private ImageView mImg_Background;   //动态背景


    //创建人
    private TextView tv_create_team_president;
    private ImageViewPlus iv_create_team_president;

    //创建时间
    private TextView tv_create_team_time;

    //社团名称
    private EditText et_create_team_name;

    //社团类型
    private Spinner spinner_create_team_type;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList;
    private String[] titleList = {"学习", "技术", "公益"};
    private int[] imageList = {R.mipmap.ic_team_study, R.mipmap.ic_team_technology, R.mipmap.ic_team_welfare};

    //社团Logo
    private ImageViewPlus iv_create_team_image;
    //社团介绍
    private EditText et_create_team_introduce;

    //创建button
    private Button bt_create_team;

    private String jsonString;

    //相册
    private PhotoUtils photoUtils;
    private Uri selectUri;
    private ProgressDialog dialog;
    private String teamSdPath;


    //photoUtils相关
    private String fileName;  //剪切后的文件名
    private String filePath; //文件路径(在手机中)
    private String fileTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        initViews();

        dynamaicBackground();

        initListeners();
        initDatas();

    }

    /**
     * 动态背景
     */
    private void dynamaicBackground() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(CreateTeamActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }


    private void initViews() {
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        tv_create_team_president = (TextView) findViewById(R.id.create_team_president_tv);
        iv_create_team_president = (ImageViewPlus) findViewById(R.id.create_team_president_image);
        //创建时间
        tv_create_team_time = (TextView) findViewById(R.id.create_team_time_tv);
        //社团名称
        et_create_team_name = (EditText) findViewById(R.id.create_team_name_et);
        //社团类型
        spinner_create_team_type = (Spinner) findViewById(R.id.create_team_type_sp);
        //社团Logo
        iv_create_team_image = (ImageViewPlus) findViewById(R.id.create_team_image_iv);
        //社团介绍
        et_create_team_introduce = (EditText) findViewById(R.id.create_team_introduce_et);

        bt_create_team = (Button) findViewById(R.id.create_team_btn);

        setPortraitChangeListener();

    }

    private void initListeners() {
        iv_create_team_image.setOnClickListener(this);
        bt_create_team.setOnClickListener(this);
    }

    private void initDatas() {
        obtainPresidentImage(user.getUserImage());
        //获取创建时间
        Date nowTime = new Date();
        String createTime = DateUtil.dateToString(nowTime, Constant.formatType);  //创建社团的时间

        fileTime = String.valueOf(new Date().getTime());  //剪切图片的时间

        tv_create_team_president.setText(user.getUserNickname());
        tv_create_team_time.setText(createTime);

        //team的部分值通过传递intent获取
        team.setTeamPresident(user);  //创建人
        team.setTeamTime(createTime);  //创建时间
        team.setTeamType("学习");

        initSpinner();  //初始化社团类型的选择器

    }

    /**
     * 初始化Spinner
     */
    private void initSpinner() {
        // 1. 设置数据源
        dataList = new ArrayList<Map<String, Object>>();

        // 2.创建数据
        for (int i = 0; i < imageList.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageList[i]);
            map.put("title", titleList[i]);
            dataList.add(map);
        }

        // 3. 新建ArrayAdapter
        simpleAdapter = new SimpleAdapter(this, dataList, R.layout.item_team_type, new String[]{"image", "title"}, new int[]{R.id.item_img, R.id.item_title});
        // 4. 为Spinner加载适配器
        spinner_create_team_type.setAdapter(simpleAdapter);
        // 5. 为Spinner设置监听器
        spinner_create_team_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                L.e(TAG, "你选择了第" + position);
                switch (position) {
                    case 0:  //学习
                        team.setTeamType("学习");
                        break;
                    case 1:  //技术
                        team.setTeamType("技术");
                        break;
                    case 2:  //公益
                        team.setTeamType("公益");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     * 获取创建人头像
     *
     * @param url 每次请求的Url
     */
    public void obtainPresidentImage(String url) {
        OkHttpUtils
                .get()
                .url(url)
                .tag(this)
                .build()
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("onError:" + e.getMessage());
                    }
                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        iv_create_team_president.setImageBitmap(bitmap);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_team_image_iv:   //点击社团logo
                //打开照片选择器
                photoUtils.selectPicture(CreateTeamActivity.this, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
                break;
            case R.id.create_team_btn:   //创建社团
                //发送创建社团请求
                //先获取et的值，再判空，再请求
                obtainEtValue();
                break;
            default:
                break;
        }
    }

    /**
     * 获取et的值，如果不为空则执行网络请求
     */
    private void obtainEtValue() {
        String teamName = et_create_team_name.getText().toString().trim();
        String teamIntroduce = et_create_team_introduce.getText().toString().trim();
        String teamImage = Constant.TEAM_IMAGE_PATH+fileName;

        if(TextUtils.isEmpty(teamName)){
            T.testShowShort(CreateTeamActivity.this,"社团名称不能为空");
        }else if(TextUtils.isEmpty(teamIntroduce)){
            T.testShowShort(CreateTeamActivity.this,"社团介绍不能为空");
        }else if(TextUtils.isEmpty(teamSdPath)){
            T.testShowShort(CreateTeamActivity.this,"请选择社团logo");
        }else if(!TextUtils.isEmpty(teamName)&&!TextUtils.isEmpty(teamIntroduce)&&!TextUtils.isEmpty(teamSdPath)){
            team.setTeamName(teamName);
            team.setTeamIntroduce(teamIntroduce);
            team.setTeamImage(teamImage);

            CreateTeamRequest(team);
        }

    }


    /**
     * 拍照或本地选择图片成功后，生成图片在sd卡中，并得到图片位置
     */
    private void setPortraitChangeListener() {
        photoUtils = new PhotoUtils(new PhotoUtils.OnPhotoResultListener() {
            @Override
            public void onPhotoResult(Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    selectUri = uri;
                    dialog = ProgressDialog.show(CreateTeamActivity.this, "提示", Constant.mProgressDialog_success, true, true);
                    //根据图片位置发送上传文件的网络请求
                    teamSdPath = selectUri.getPath();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            uploadImage(teamSdPath);
                        }
                    }, 500);

                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case PhotoUtils.INTENT_CROP:

                photoUtils.onActivityResult(CreateTeamActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(CreateTeamActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
                break;
        }
    }


    public void uploadImage(String path) {
        filePath = Constant.FILE_PATH;             //文件路径(在手机中)
        fileName = path.substring(path.lastIndexOf("/") + 1, path.length());

        L.v(TAG + "filePath=", filePath);
        L.v(TAG + "fileName=", fileName);
        File file = new File(filePath, fileName);
        if (!file.exists()) {
            T.testShowShort(CreateTeamActivity.this, "文件不存在，请修改文件路径");
            return;
        }

        OkHttpUtils.post()
                .addFile("image", fileName, file)
                .url(ConstantUrl.FileImageUrl + ConstantUrl.uploadImage_interface)
                .addParams("serverPath", "C:/websoft/image/team/")
                .build()
                .execute(new MyUploadTeamImageCallback());
    }

    public class MyUploadTeamImageCallback extends StringCallback {
        @Override
        public boolean validateReponse(Response response, int id) {
            L.v(TAG, "validateReponse");
            L.v(TAG, "上传成功");
            dialog.cancel();//关闭圆形进度条
            setSdImage(teamSdPath);
            return super.validateReponse(response, id);

        }

        @Override
        public void onError(Call call, Exception e, int id) {

        }

        @Override
        public void onResponse(String response, int id) {
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
            L.v(TAG + "上传进度=", (progress * 100) + "%");
            if (progress == 1.0f) {

                L.v(TAG, "上传成功");

            }
        }

    }

    /**
     * 剪切后的图片保存在sd卡中，imageview设置sd的image
     */
    private void setSdImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        iv_create_team_image.setImageBitmap(bitmap);
    }


    public void CreateTeamRequest(Team team){
        jsonString = new Gson().toJson(team);

        dialog = ProgressDialog.show(CreateTeamActivity.this, "提示", Constant.mProgressDialog_success, true, true);
        //根据图片位置发送上传文件的网络请求
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.TeamUrl + ConstantUrl.createTeam_interface)
                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
                        .content(jsonString)
                        .build()
                        .execute(new MyRequestCreateTeamCallback());
            }
        }, 2000);
        //发起添加请求

    }
    public class MyRequestCreateTeamCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dialog.cancel();//关闭圆形进度条
            e.printStackTrace();
            L.v(TAG, "请求失败");
            T.testShowShort(CreateTeamActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(CreateTeamActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(CreateTeamActivity.this, TeamHomeActivity.class));  //跳转回主页面


        }
    }
}
