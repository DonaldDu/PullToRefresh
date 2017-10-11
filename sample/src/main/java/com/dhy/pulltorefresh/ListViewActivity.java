package com.dhy.pulltorefresh;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.jaynm.pulltorefresh.PullToRefreshBase;
import com.jaynm.pulltorefresh.PullToRefreshListView;

/**
 * Created by caobo on 2016/11/1 0001.
 * ListView下拉刷新、上拉加载更多
 * <p>
 * 这个在魅族手机侧滑删除菜单【不能正常使用】
 */

public class ListViewActivity extends Activity implements PullToRefreshBase.OnRefreshListener<ListView> {

    private PullToRefreshListView refreshlistview;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        handler = new Handler();
        refreshlistview = findViewById(R.id.refreshlistview);
        refreshlistview.setPullRefreshEnabled(true);
        refreshlistview.setPullLoadEnabled(false);
        refreshlistview.setScrollLoadEnabled(true);
        refreshlistview.setOnRefreshListener(this);
        ListView mListView = refreshlistview.getRefreshableView();
        mListView.setAdapter(new MyAdapter());
        refreshlistview.onRefreshComplete();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshlistview.onRefreshComplete();
            }
        }, 2000);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshlistview.onPullUpRefreshComplete();
            }
        }, 2000);
    }

    SwipeLayout.SwipeListener swipeListener = new SwipeLayout.SwipeListener() {
        @Override
        public void onStartOpen(SwipeLayout layout) {
            refreshlistview.setPullRefreshEnabled(false);
            Log.i("TAG", "onStartOpen: ");
        }

        @Override
        public void onOpen(SwipeLayout layout) {

        }

        @Override
        public void onStartClose(SwipeLayout layout) {

        }

        @Override
        public void onClose(SwipeLayout layout) {
            refreshlistview.setPullRefreshEnabled(true);
        }

        @Override
        public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

        }

        @Override
        public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

        }
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 10;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_swipe_item, parent, false);
                SwipeLayout swipeLayout = (SwipeLayout) convertView;
                swipeLayout.addSwipeListener(swipeListener);
            }
            return convertView;
        }
    }

    public void onBackClick(View view) {
        finish();
    }
}
