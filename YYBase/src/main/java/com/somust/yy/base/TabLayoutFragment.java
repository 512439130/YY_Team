package com.somust.yy.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.somust.yy.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by tangyangkai on 16/5/12.
 */
public class TabLayoutFragment extends Fragment implements TabsAdapter.onStartDragListener {
    public static final String TABLAYOUT_FRAGMENT = "tab_fragment";
    public static final int TABS = 100;
    private TextView txt, sortTxt;
    private View sortView;
    private RecyclerView recyclerview;
    private int type;
    private TabsAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;


    public static TabLayoutFragment newInstance(int type) {
        TabLayoutFragment fragment = new TabLayoutFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(TABLAYOUT_FRAGMENT, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = (int) getArguments().getSerializable(TABLAYOUT_FRAGMENT);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tablayout, container, false);
        initView(view);
        return view;
    }


    protected void initView(View view) {
        txt = (TextView) view.findViewById(R.id.tab_txt);
        txt.setText("这里是第" + type + "个fragment");
        sortTxt = (TextView) view.findViewById(R.id.sort_txt);
        sortView = view.findViewById(R.id.sort_view);
        recyclerview = (RecyclerView) view.findViewById(R.id.sorts_recyclerview);
        if (type == TABS) {
            sortTxt.setVisibility(View.VISIBLE);
            sortView.setVisibility(View.VISIBLE);
            txt.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
            initSortViews();
        } else {
            sortTxt.setVisibility(View.GONE);
            sortView.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        }

    }

    //加载排序布局
    private void initSortViews() {
        adapter = new TabsAdapter(getActivity(), this);
        adapter.setListener(new TabsAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Event item = new Event(position);
                EventBus.getDefault().post(item);
                Log.e("tag", position + "");

            }


        });
        LinearLayoutManager manger = new LinearLayoutManager(getActivity());
        recyclerview.setLayoutManager(manger);
        recyclerview.setAdapter(adapter);
        //关联ItemTouchHelper和RecyclerView
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerview);


    }


    @Override
    public void startDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
