package com.somust.yy.base;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by tangyangkai on 16/5/17.
 */

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private onMoveAndSwipedListener mAdapter;

    public SimpleItemTouchHelperCallback(onMoveAndSwipedListener listener) {
        mAdapter = listener;
    }


    /**
     * 这个方法是用来设置我们拖动的方向以及侧滑的方向的
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //如果是ListView样式的RecyclerView
        //设置拖拽方向为上下
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        //将方向参数设置进去
        return makeMovementFlags(dragFlags, 0);
    }

    /**
     * 当我们拖动item时会回调此方法
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        //如果两个item不是一个类型的，我们让他不可以拖拽
        if (viewHolder.getItemViewType() != target.getItemViewType()) {
            return false;
        }
        //回调adapter中的onItemMove方法
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     * 当我们侧滑item时会回调此方法
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    /**
     * 当状态改变时回调此方法
     */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //当前状态不是idel（空闲）状态时，说明当前正在拖拽或者侧滑
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //看看这个viewHolder是否实现了onStateChangedListener接口
            if (viewHolder instanceof TabsAdapter.onStateChangedListener) {
                TabsAdapter.onStateChangedListener listener = (TabsAdapter.onStateChangedListener) viewHolder;
                //回调ItemViewHolder中的onItemSelected方法来改变item的背景颜色
                listener.onItemSelected();
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    /**
     * 当用户拖拽完或者侧滑完一个item时回调此方法，用来清除施加在item上的一些状态
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof TabsAdapter.onStateChangedListener) {
            TabsAdapter.onStateChangedListener listener = (TabsAdapter.onStateChangedListener) viewHolder;
            listener.onItemClear();
        }
    }

}