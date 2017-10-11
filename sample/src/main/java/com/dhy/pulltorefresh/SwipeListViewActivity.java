package com.dhy.pulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.jaynm.pulltorefresh.PullToRefreshBase;
import com.jaynm.pulltorefresh.PullToRefreshMenuView;
import com.jaynm.pulltorefresh.SwipeMenu;
import com.jaynm.pulltorefresh.SwipeMenuListView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by caobo on 2016/11/1 0001.
 * ListView上拉刷新、下拉加載更多+左滑刪除。
 * <p>
 * 这个在魅族手机侧滑删除菜单【能正常使用】
 */

public class SwipeListViewActivity extends Activity implements PullToRefreshBase.OnRefreshListener<SwipeMenuListView> {
    Activity activity;
    private PullToRefreshMenuView refreshlistview;

    private SwipeMenuListView swipeMenuListView;

    private LinkedList<String> pullData;

    private ListAdapter adapter;

    //标记下拉index
    private int pullDownIndex = 0;
    //标记上拉index
    private int pullUpIndex = 0;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipelistview);
        activity = this;
        pullData = new LinkedList<>();
        refreshlistview = findViewById(R.id.refreshlistview);
        refreshlistview.setPullLoadEnabled(false);
        refreshlistview.setScrollLoadEnabled(true);
        refreshlistview.setOnRefreshListener(this);
        swipeMenuListView = refreshlistview.getRefreshableView();
        adapter = new ListAdapter();
        swipeMenuListView.setAdapter(adapter);
        refreshlistview.onRefreshComplete();
        // 操作删除按钮的点击事件
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                Toast.makeText(SwipeListViewActivity.this, "删除" + pullData.get(position), Toast.LENGTH_LONG).show();
                return false;
            }
        });


        // 操作ListView左滑时的手势操作，这里用于处理上下左右滑动冲突：开始滑动时则禁止下拉刷新和上拉加载手势操作，结束滑动后恢复上下拉操作
        swipeMenuListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                refreshlistview.setPullRefreshEnabled(false);
            }

            @Override
            public void onSwipeEnd(int position) {
                refreshlistview.setPullRefreshEnabled(true);
            }
        });
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
        onPullDown();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
        onPullUp();
    }

    public List<String> getData() {
        for (int i = 1; i <= 20; i++) {
            pullData.add("默认ListView数据" + i);
        }
        return pullData;
    }

    /**
     * 下拉刷新添加数据到List集合
     */
    public void onPullDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    pullData.addFirst("下拉刷新数据" + pullDownIndex);
                    pullDownIndex++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshlistview.onRefreshComplete();
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 上拉加载添加数据到List集合
     */
    public void onPullUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    pullData.addLast("上拉加载数据" + pullUpIndex);
                    pullUpIndex++;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshlistview.onRefreshComplete();
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.listview_item, parent, false);
            }
            return convertView;
        }
    }

    public void onBackClick(View view) {
        finish();
    }
}
