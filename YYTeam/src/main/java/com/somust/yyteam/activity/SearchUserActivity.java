package com.somust.yyteam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;
import com.somust.yyteam.adapter.SearchUserAdapter;
import com.somust.yyteam.bean.AllUser;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.searchview.SearchUserView;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class SearchUserActivity extends Activity implements SearchUserView.SearchViewListener {
    private static final String TAG = "SearchUserActivity:";

    /**
     * 搜索结果列表view
     */
    private ListView lvResults;
    /**
     * 搜索view
     */
    private SearchUserView searchView;

    /**
     * 搜索结果列表adapter
     */
    private SearchUserAdapter searchUserAdapter;

    /**
     * 搜索结果的数据
     */
    private List<AllUser> resultData;




    /**
     * 全部用户的数据(查询出的数据，bitmap还未获取)
     */
    private List<User> users;  //数据库的bean数据
    /**
     * 保存网络获取的图片集合
     */
    private Bitmap[] portraitBitmaps;



    /**
     * 全部用户的转化数据
     */
    private List<AllUser> allUsers;  //通过 Users和 portraitBitmaps合并的数据


    /**
     * 保存登录用户的手机号
     * @param savedInstanceState
     */
    private String Own_id;



    //所有用户的信息
    private List<User> allUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        initViews();
        initData();
    }



    /**
     * 初始化视图
     */
    private void initViews() {
        lvResults = (ListView) findViewById(R.id.main_lv_search_results);
        searchView = (SearchUserView) findViewById(R.id.main_search_layout);
        //设置监听
        searchView.setSearchViewListener(this);

        lvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {  //item的点击事件

                String userId = resultData.get(position).getUserPhone();
                String userNickname = resultData.get(position).getUserNickname();
                Intent intent = new Intent(SearchUserActivity.this, PersionInformationActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("userNickname", userNickname);
                intent.putExtra("Own_id",Own_id);
                intent.putExtra("openState","stranger");  //好友
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = this.getIntent();
        Own_id = intent.getStringExtra("Own_id");
        obtainAllUserInfo();//获取全部用户
        getResultData(null);  //初始化listview
    }


    /**
     * 获取所有用户
     */
    public void obtainAllUserInfo() {
        final String url = ConstantUrl.userUrl + ConstantUrl.getAllUserInfo_interface;
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new MyAllUserInfoCallback());
    }

    public class MyAllUserInfoCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(SearchUserActivity.this, "所有用户获取失败");
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(SearchUserActivity.this, "所有用户为空");
            } else {
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                allUser = new ArrayList<User>();
                allUser = gson.fromJson(response, new TypeToken<List<User>>() {
                }.getType());
                L.v(TAG, allUser.toString());

                users = allUser;

                //获取网络图片
                portraitBitmaps = new Bitmap[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    obtainImage(users.get(i).getUserImage(), i);
                }

            }
        }
    }




    /**
     * 获取网络图片请求，并将网络图片显示到imageview中去(如果是多次请求，需要一个bitmap数组)
     *
     * @param url 每次请求的Url
     * @param i   需要保存在bitmaps的对应位置
     */
    public void obtainImage(String url, final int i) {
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
                        L.v("TAG", "onResponse：complete");
                        portraitBitmaps[i] = bitmap;
                        //网络请求成功后
                        //初始化搜索结果数据
                        allUsers = new ArrayList<AllUser>();
                        Transformation(allUsers,portraitBitmaps);
                        L.v(TAG, allUsers.toString());

                    }
                });
    }


    /**
     * 转换
     */
    public void Transformation(List<AllUser> mAllUser, Bitmap[] portraitBitmaps) {
        for (int i = 0;i<portraitBitmaps.length;i++ ){
            AllUser allUser = new AllUser();
            allUser.setUserId(users.get(i).getUserId());
            allUser.setUserPhone(users.get(i).getUserPhone());
            allUser.setUserNickname(users.get(i).getUserNickname());
            allUser.setUserPassword(users.get(i).getUserPassword());
            allUser.setUserSex(users.get(i).getUserSex());
            allUser.setUserToken(users.get(i).getUserToken());
            allUser.setUserImage(portraitBitmaps[i]);
            mAllUser.add(allUser);
        }

    }


    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData == null) {
            // 初始化
            resultData = new ArrayList<>();
        } else {
            resultData.clear();
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUserPhone().contains(text.trim()) && !allUsers.get(i).getUserPhone().equals(Own_id)) {  //模糊查询
                    resultData.add(allUsers.get(i));
                }
            }
        }
        if (searchUserAdapter == null) {
            searchUserAdapter = new SearchUserAdapter(this, resultData);  //搜索结果的listAdapter
        } else {
            searchUserAdapter.notifyDataSetChanged();
        }
    }


    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text
     */
    @Override
    public void onSearch(String text) {
        //更新result数据
        getResultData(text);
        lvResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (lvResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            lvResults.setAdapter(searchUserAdapter);
        } else {
            //更新搜索数据
            searchUserAdapter.notifyDataSetChanged();
        }
        T.testShowShort(this, "完成搜索");
    }


}
