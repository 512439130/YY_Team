package com.somust.yyteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.somust.yyteam.R;

import com.somust.yyteam.adapter.SearchTeamAdapter;
import com.somust.yyteam.bean.AllTeam;
import com.somust.yyteam.bean.Team;
import com.somust.yyteam.bean.User;
import com.somust.yyteam.constant.ConstantUrl;
import com.somust.yyteam.utils.log.L;
import com.somust.yyteam.utils.log.T;
import com.somust.yyteam.view.searchview.SearchView;
import com.yy.http.okhttp.OkHttpUtils;
import com.yy.http.okhttp.callback.BitmapCallback;
import com.yy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SearchTeamActivity extends Activity implements SearchView.SearchViewListener {
    private static final String TAG = "SearchTeamActivity:";

    /**
     * 搜索结果列表view
     */
    private ListView searchTeamResults;
    /**
     * 搜索view
     */
    private SearchView searchTeamView;

    /**
     * 搜索结果列表adapter
     */
    private SearchTeamAdapter searchTeamAdapter;

    /**
     * 搜索结果的数据
     */
    private List<AllTeam> resultData;


    /**
     * 全部用户的数据(查询出的数据，bitmap还未获取)
     */
    private List<Team> teams;  //数据库的bean数据



    /**
     * 保存网络获取的图片集合
     */
    private Bitmap[] teamImageBitmaps;


    /**
     * 全部用户的转化数据
     */
    private List<AllTeam> allTeams;  //通过 Teams和 portraitBitmaps合并的数据




    private User user;


    private List<Team> NoBitmapTeamResult ;    //保存搜索结果的无Bitmap的team集合（类型为Team）









    private Handler teamImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();


            if (bundle.getString("team_success") == "team_success") {  //社团图片成功获取
                for(int i = 0;i<allTeams.size();i++){
                    allTeams.get(i).setTeamImage(teamImageBitmaps[i]);
                }
                searchTeamAdapter.notifyDataSetChanged();
            }



        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_team);

        initViews();
        obtainAllTeamInfo();//获取全部社团

    }

    /**
     * 初始化视图
     */
    private void initViews() {
        searchTeamResults = (ListView) findViewById(R.id.search_results_team_lv);
        searchTeamView = (SearchView) findViewById(R.id.search_layout_team_sv);


    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = this.getIntent();
        user = (User) intent.getSerializableExtra("user");

        getResultData(null);  //初始化listview
        initListener();
    }

    private void initListener() {
        //设置监听
        searchTeamView.setSearchViewListener(this);

        searchTeamResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {  //item的点击事件

                Team team = NoBitmapTeamResult.get(position);
                //必须使用结果的team数组

                Intent intent = new Intent(SearchTeamActivity.this, TeamInformationActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("teams", team);
                //intent.putExtra("team","stranger");  //好友
                startActivity(intent);
            }
        });
    }


    /**
     * 获取所有用户
     */
    public void obtainAllTeamInfo() {
        final String url = ConstantUrl.teamUrl + ConstantUrl.getTeam_interface;
        OkHttpUtils
                .post()
                .url(url)
                .build()
                .execute(new MyAllTeamInfoCallback());
    }


    public class MyAllTeamInfoCallback extends StringCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            e.printStackTrace();
            L.e(TAG, "onError:" + e.getMessage());
            T.testShowShort(SearchTeamActivity.this, "所有社团获取失败");
        }

        @Override
        public void onResponse(String response, int id) {
            if (response.equals("")) {
                T.testShowShort(SearchTeamActivity.this, "所有社团为空");
            } else {
                L.v(TAG, "onResponse:" + response);
                Gson gson = new Gson();
                teams = new ArrayList<Team>();
                teams = gson.fromJson(response, new TypeToken<List<Team>>() {
                }.getType());
                L.v(TAG, teams.toString());

                //网络请求成功后
                //初始化搜索结果数据
                allTeams = new ArrayList<AllTeam>();
                //获取网络图片
                teamImageBitmaps = new Bitmap[teams.size()];
                Transformation(allTeams);
                L.v(TAG, allTeams.toString());


                initData();

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
                        teamImageBitmaps[i] = bitmap;
                        UpdateUi(teamImageHandler, "team_success", "team_success");

                    }
                });
    }


    /**
     * 转换
     */
    public void Transformation(List<AllTeam> mAllTeam) {

        for (int i = 0; i < teams.size(); i++) {
            AllTeam allTeam = new AllTeam();
            allTeam.setTeamId(teams.get(i).getTeamId());
            allTeam.setTeamName(teams.get(i).getTeamName());
            allTeam.setTeamType(teams.get(i).getTeamType());
            allTeam.setTeamTime(teams.get(i).getTeamTime());
            allTeam.setTeamIntroduce(teams.get(i).getTeamIntroduce());
            obtainImage(teams.get(i).getTeamImage(), i);
            allTeam.setTeamImage(teamImageBitmaps[i]);
            mAllTeam.add(allTeam);
        }

    }


    /**
     * 获取搜索结果data和adapter
     */
    private void getResultData(String text) {
        if (resultData == null && NoBitmapTeamResult == null) {
            // 初始化
            resultData = new ArrayList<>();
            NoBitmapTeamResult = new ArrayList<>();
        } else {
            resultData.clear();
            NoBitmapTeamResult.clear();
            for (int i = 0; i < allTeams.size(); i++) {
                if (allTeams.get(i).getTeamName().contains(text.trim())) {  //模糊查询
                    resultData.add(allTeams.get(i));
                    NoBitmapTeamResult.add(teams.get(i));
                }
            }
        }
        if (searchTeamAdapter == null) {
            searchTeamAdapter = new SearchTeamAdapter(this, resultData);  //搜索结果的listAdapter
        } else {
            searchTeamAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onSearch(String text) {
        //更新result数据
        getResultData(text);
        searchTeamResults.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
        if (searchTeamResults.getAdapter() == null) {
            //获取搜索数据 设置适配器
            searchTeamResults.setAdapter(searchTeamAdapter);
        } else {
            //更新搜索数据
            searchTeamAdapter.notifyDataSetChanged();
        }
        T.testShowShort(this, "完成搜索");
    }


    /**
     * 更新UI和控制子线程设置图片
     *
     * @param handler
     * @param key
     * @param value
     */
    private void UpdateUi(Handler handler, String key, String value ) {
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
