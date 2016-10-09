package com.example.lmhgfkk.ui_refeshlistview.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lmhgfkk.ui_refeshlistview.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lmhgfkk on 16/10/8.
 */
public class RefreshListview extends ListView implements AbsListView.OnScrollListener {

    private View headerview;
    private View footerview;
    private int headerviewheight;
    private int footerviewheight;
    private int currentpadding;
    private float startY = -1;
    private float endY;
    private int spaceY;
    private final int PULL = 0;
    private final int RELEASE = 1;
    private final int LOAD = 2;
    private int currentstate = PULL;

    private ImageView arrow;
    private ProgressBar progressbar;
    private TextView isloading;
    private TextView lastloadtime;

    private RotateAnimation rotateup;
    private RotateAnimation rotatedown;


    public RefreshListview(Context context) {
        super(context);

        initView(context);
    }


    public RefreshListview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setOnScrollListener(this);
        initAnimation();
        LayoutInflater layoutinflater = LayoutInflater.from(context);
        headerview = layoutinflater.inflate(R.layout.headerview, null);
        footerview = layoutinflater.inflate(R.layout.footerview, null);

        arrow = (ImageView) headerview.findViewById(R.id.arrow);
        progressbar = (ProgressBar) headerview.findViewById(R.id.progressbar);
        isloading = (TextView) headerview.findViewById(R.id.isloading);
        lastloadtime = (TextView) headerview.findViewById(R.id.lastloadtime);


        headerview.measure(0, 0);//主动通知系统去测量该view
        footerview.measure(0, 0);

//        headerviewheight = headerview.getMeasuredHeight();
//        footerviewheight = footerview.getMeasuredHeight();
//        headerview.setPadding(0, -headerviewheight, 0, 0);
//        footerview.setPadding(0, 0, 0, 0);

        headerview.post(new Runnable() {
            @Override
            public void run() {
                headerviewheight = headerview.getMeasuredHeight();
                headerview.setPadding(0, -headerviewheight, 0, 0);

            }
        });
        footerview.post(new Runnable() {
            @Override
            public void run() {
                footerviewheight = footerview.getMeasuredHeight();
                footerview.setPadding(0, -footerviewheight, 0, 0);
            }
        });
// ---  setHeaderviewPadding(headerview.getMeasuredHeight());
        RefreshListview.this.addHeaderView(headerview);
        RefreshListview.this.addFooterView(footerview);


    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:
                if (startY == -1) {// 确保startY的值是有效的
                    startY = ev.getRawY();
                }
                if (currentstate == LOAD) {
                    break;
                }
                endY = ev.getRawY();
                spaceY = (int) (endY - startY);
                currentpadding = -headerviewheight + spaceY;
                if (currentpadding > -headerviewheight && getFirstVisiblePosition() == 0) {

                    headerview.setPadding(0, currentpadding, 0, 0);

                    if (currentpadding > 0 && currentstate == PULL) {
                        currentstate = RELEASE;
                        setCurentState();

                    }
                    if (currentpadding < 0 && currentstate == RELEASE) {
                        currentstate = PULL;
                        setCurentState();
                    }
//
                    return true;
                }

                break;
            case MotionEvent.ACTION_UP:
                startY = -1;// 重置
                if (currentstate == RELEASE) {
                    currentstate = LOAD;
                    setCurentState();
                } else if (currentstate == PULL) {
                    currentstate = PULL;
                    setCurentState();
                    headerview.setPadding(0, -headerviewheight, 0, 0);
                }


                break;
        }


        return super.onTouchEvent(ev);
    }

    private void setCurentState() {
        switch (currentstate) {
            case PULL:

                isloading.setText("下拉刷新");
                arrow.startAnimation(rotatedown);


                break;
            case RELEASE:
                arrow.startAnimation(rotateup);
                isloading.setText("松开刷新");

                break;
            case LOAD:
                arrow.clearAnimation();
                isloading.setText("正在刷新");
                arrow.setVisibility(View.INVISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                headerview.setPadding(0, 0, 0, 0);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        completeRefresh();
//                    }
//                },3000);
                if (listener != null) {
                    listener.onPullRefresh();
                }
                break;
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        return time;
    }

    private void initAnimation() {
        rotateup = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateup.setDuration(300);
        rotateup.setFillAfter(true);
        rotatedown = new RotateAnimation(-180, -360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotatedown.setDuration(300);
        rotatedown.setFillAfter(true);

    }

    public void completeRefresh(boolean flag) {
        if(flag){
            //重置footerView状态
            footerview.setPadding(0,-footerviewheight,0,0);
        }else {
            //重置headerView状态
            currentstate = PULL;
            progressbar.setVisibility(View.INVISIBLE);
            arrow.setVisibility(View.VISIBLE);
            isloading.setText("下拉刷新");
            lastloadtime.setText("最后刷新时间" + getCurrentTime());
            headerview.setPadding(0, -headerviewheight, 0, 0);
        }

    }

    private onRefreshListener listener;
    public interface onRefreshListener {
        void onPullRefresh();
        void onloadmore();
    }
    public void setOnRefreshListener(onRefreshListener l) {
        listener = l;
    }


    /**
     * SCROLL_STATE_IDLE:闲置状态，就是手指松开
     * SCROLL_STATE_TOUCH_SCROLL：手指触摸滑动，就是按着来滑动
     * SCROLL_STATE_FLING：快速滑动后松开
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==OnScrollListener.SCROLL_STATE_IDLE && getLastVisiblePosition()==getCount()-1){
            footerview.setPadding(0,0,0,0);
            setSelection(getCount());//让listview最后一条显示出来
            if(listener!=null){
                listener.onloadmore();
            }


        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

}
