package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
 * 完善社团信息
 */

public class UpdateTeamInfoActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CreateTeamActivity:";



    private Team team;
    private Team updateTeam = new Team();
    private User user;

    private ImageView mImg_Background;   //动态背景




    //创建时间
    private TextView tv_update_team_info_time;

    //社团名称
    private TextView tv_update_team_info_name;

    //社团类型
    private Spinner spinner_update_team_info_type;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, Object>> dataList;
    private String[] titleList = {"学习", "技术", "公益"};
    private int[] imageList = {R.mipmap.ic_team_study, R.mipmap.ic_team_technology, R.mipmap.ic_team_welfare};

    //社团Logo
    private ImageViewPlus iv_update_team_info_image;
    //社团介绍
    private EditText et_update_team_info_introduce;

    //创建button
    private Button bt_update_team_info;

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
        setContentView(R.layout.activity_update_team_info);
        immersiveStatusBar();
        Intent intent = getIntent();
        team = (Team) intent.getSerializableExtra("team");
        user = (User) intent.getSerializableExtra("user");
        initViews();

        dynamaicBackground();

        initListeners();
        initDatas();

    }

    /**
     * 沉浸式状态栏（伪）
     */
    private void immersiveStatusBar() {
        //沉浸式状态栏（伪）
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 动态背景
     */
    private void dynamaicBackground() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(UpdateTeamInfoActivity.this, R.anim.translate_anim);
                mImg_Background.startAnimation(animation);
            }
        }, 200);
    }


    private void initViews() {
        mImg_Background = (ImageView) findViewById(R.id.de_img_backgroud);
        //创建时间
        tv_update_team_info_time = (TextView) findViewById(R.id.update_team_info_time_tv);
        //社团名称
        tv_update_team_info_name = (TextView) findViewById(R.id.update_team_info_name_tv);
        //社团类型
        spinner_update_team_info_type = (Spinner) findViewById(R.id.update_team_info_type_sp);
        //社团Logo
        iv_update_team_info_image = (ImageViewPlus) findViewById(R.id.update_team_info_image_iv);
        //社团介绍
        et_update_team_info_introduce = (EditText) findViewById(R.id.update_team_info_introduce_et);

        bt_update_team_info = (Button) findViewById(R.id.update_team_info_btn);

        setPortraitChangeListener();

    }

    private void initListeners() {
        iv_update_team_info_image.setOnClickListener(this);
        bt_update_team_info.setOnClickListener(this);
    }

    private void initDatas() {
        fileTime = String.valueOf(new Date().getTime());  //剪切图片的时间
        obtainTeamImage(team.getTeamImage());  //设置原来的数据
        tv_update_team_info_time.setText(team.getTeamTime());
        tv_update_team_info_name.setText(team.getTeamName());
        et_update_team_info_introduce.setText(team.getTeamIntroduce());
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
        spinner_update_team_info_type.setAdapter(simpleAdapter);
        // 5. 为Spinner设置监听器
        spinner_update_team_info_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                L.e(TAG, "你选择了第" + position);
                switch (position) {
                    case 0:  //学习
                        updateTeam.setTeamType("学习");
                        break;
                    case 1:  //技术
                        updateTeam.setTeamType("技术");
                        break;
                    case 2:  //公益
                        updateTeam.setTeamType("公益");
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //工具team的值设置初始值
        if(team.getTeamType().equals("学习")){
            spinner_update_team_info_type.setSelection(0,true);
        }
        if(team.getTeamType().equals("技术")){
            spinner_update_team_info_type.setSelection(1,true);
        }
        if(team.getTeamType().equals("公益")){
            spinner_update_team_info_type.setSelection(2,true);
        }

    }

    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     * 获取创建人头像
     *
     * @param url 每次请求的Url
     */
    public void obtainTeamImage(String url) {
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
                        iv_update_team_info_image.setImageBitmap(bitmap);
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_team_info_image_iv:   //点击社团logo
                //打开照片选择器
                photoUtils.selectPicture(UpdateTeamInfoActivity.this, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
                break;
            case R.id.update_team_info_btn:   //创建社团
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
        String teamIntroduce = et_update_team_info_introduce.getText().toString().trim();
        String teamImage = Constant.TEAM_IMAGE_PATH+fileName;

        if(TextUtils.isEmpty(teamIntroduce)){
            T.testShowShort(UpdateTeamInfoActivity.this,"社团介绍不能为空");
        }else if(TextUtils.isEmpty(teamSdPath)){
            T.testShowShort(UpdateTeamInfoActivity.this,"请选择社团logo");
        }else if(!TextUtils.isEmpty(teamIntroduce)&&!TextUtils.isEmpty(teamSdPath)){
            updateTeam.setTeamId(team.getTeamId());
            updateTeam.setTeamPresident(user);
            updateTeam.setTeamName(team.getTeamName());
            updateTeam.setTeamTime(team.getTeamTime());
            updateTeam.setTeamIntroduce(teamIntroduce);
            updateTeam.setTeamImage(teamImage);
            UpdateTeamInfoRequest(updateTeam);
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
                    dialog = ProgressDialog.show(UpdateTeamInfoActivity.this, "提示", Constant.mProgressDialog_success, true, true);
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
                photoUtils.onActivityResult(UpdateTeamInfoActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
                break;
            case PhotoUtils.INTENT_SELECT:
                photoUtils.onActivityResult(UpdateTeamInfoActivity.this, requestCode, resultCode, data, user.getUserPhone(), fileTime, Constant.CROP_TEAM_IMAGE_NAME);
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
            T.testShowShort(UpdateTeamInfoActivity.this, "文件不存在，请修改文件路径");
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
        iv_update_team_info_image.setImageBitmap(bitmap);
    }


    public void UpdateTeamInfoRequest(Team updateTeam){
        jsonString = new Gson().toJson(updateTeam);

        dialog = ProgressDialog.show(UpdateTeamInfoActivity.this, "提示", Constant.mProgressDialog_success, true, true);
        //根据图片位置发送上传文件的网络请求
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                OkHttpUtils
                        .postString()
                        .url(ConstantUrl.teamUrl + ConstantUrl.updateTeamInfo_interface)
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
            T.testShowShort(UpdateTeamInfoActivity.this, Constant.mProgressDialog_error);
            L.v(e.getMessage());
        }

        @Override
        public void onResponse(String response, int id) {
            dialog.cancel();//关闭圆形进度条
            L.v(response);
            L.v(TAG, "请求成功");
            T.testShowShort(UpdateTeamInfoActivity.this, Constant.mMessage_success);

            //数据保持问题(解决，采用singTask启动模式)
            startActivity(new Intent(UpdateTeamInfoActivity.this, TeamHomeActivity.class));  //跳转回主页面
            photoUtils.clearCropFile(selectUri);//清除手机内存中刚刚裁剪的图片   必须在上传文件成功后执行

        }
    }
}
