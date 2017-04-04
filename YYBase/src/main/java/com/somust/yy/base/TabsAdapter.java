package com.somust.yy.base;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.somust.yy.R;

import java.util.Collections;

/**
 * Created by tangyangkai on 16/5/6.
 */
public class TabsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements onMoveAndSwipedListener {


    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public interface onStartDragListener {
        void startDrag(RecyclerView.ViewHolder viewHolder);
    }


    public interface onStateChangedListener {
        void onItemSelected();
        void onItemClear();
    }


    private Context context;
    private LayoutInflater layoutInflater;
    private onItemClickListener listener;
    private onStartDragListener dragListener;


    public void setListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public onItemClickListener getListener() {
        return listener;
    }

    public TabsAdapter(Context context, onStartDragListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.dragListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TabsViewHolder(layoutInflater.inflate(R.layout.item_recyclerview, null, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        TabsViewHolder tabViewHolder = (TabsViewHolder) holder;
        tabViewHolder.tabs.setText(TabLayoutActivity.tabTitle.get(position));
        tabViewHolder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getListener().onItemClick(position);
            }
        });

        tabViewHolder.imgs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //如果按下
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    //回调RecyclerListFragment中的startDrag方法
                    //让mItemTouchHelper执行拖拽操作
                    dragListener.startDrag(holder);
                }
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return TabLayoutActivity.tabTitle.size();
    }


    public class TabsViewHolder extends RecyclerView.ViewHolder implements onStateChangedListener {

        private TextView tabs;
        private ImageView imgs;
        private RelativeLayout rl;

        public TabsViewHolder(View itemView) {
            super(itemView);
            tabs = (TextView) itemView.findViewById(R.id.recycle_txt);
            imgs = (ImageView) itemView.findViewById(R.id.recycle_img);
            rl = (RelativeLayout) itemView.findViewById(R.id.recycle_rl);
        }

        @Override
        public void onItemSelected() {
            //设置item的背景颜色为浅灰色
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            //恢复item的背景颜色
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //交换mItems数据的位置
        Collections.swap(TabLayoutActivity.tabTitle, fromPosition, toPosition);
        //交换RecyclerView列表中item的位置
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


}
