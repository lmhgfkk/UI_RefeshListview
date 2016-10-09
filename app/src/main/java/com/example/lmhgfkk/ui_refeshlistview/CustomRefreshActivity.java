package com.example.lmhgfkk.ui_refeshlistview;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lmhgfkk.ui_refeshlistview.view.RefreshListview;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.M)
public class CustomRefreshActivity extends AppCompatActivity {

    private RefreshListview listview;
    private ArrayList<String> lists=new ArrayList<String>();
    private MyAdapter adapter;

    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    adapter.notifyDataSetChanged();
                    listview.completeRefresh(false);
                    break;
                case 1:
                    adapter.notifyDataSetChanged();
                    listview.completeRefresh(true);
                    listview.setSelection(lists.size());
                    break;
            }


            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_customrefresh);

        listview = (RefreshListview) findViewById(R.id.refreshlistview);
        initData();
    }



    private void initData() {
        for(int i=0;i<15;i++){
            lists.add("原来的数据---"+i);
        }
        adapter=new MyAdapter();
        listview.setAdapter(adapter);

        listview.setOnRefreshListener(new RefreshListview.onRefreshListener() {
            @Override
            public void onPullRefresh() {

                getDataFromServer();
            }

            @Override
            public void onloadmore() {
                getMoreFromServer();
            }
        });
    }



    public void getDataFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                lists.add(0,"下拉刷新的数据");
                handler.sendEmptyMessage(0);
            }
        }).start();


    }

    private void getMoreFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                lists.add("加载更多的数据---1");
                lists.add("加载更多的数据---2");
                lists.add("加载更多的数据---3");
                handler.sendEmptyMessage(1);
            }
        }).start();

    }

    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return lists.size();
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
            TextView tv=new TextView(CustomRefreshActivity.this);
            tv.setTextSize(18);
            tv.setPadding(20,20,20,20);
            tv.setText(lists.get(position));
            return tv;
        }
    }



}
