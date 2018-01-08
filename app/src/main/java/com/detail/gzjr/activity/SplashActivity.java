package com.detail.gzjr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.detail.gzjr.R;
import com.detail.gzjr.activity.adapter.ViewPagerAdapter;
import com.detail.gzjr.activity.utils.HttpRequest;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jr on 2017/10/9.
 */

public class SplashActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener {

    //定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    //定义一个ArrayList来存放View
    private ArrayList<View> views;
    private boolean flag = false;
    private Runnable runnable;

    //引导图片资源
    private static final int[] pics = {R.mipmap.splash,R.mipmap.splashthree,R.mipmap.splashtwo};

    //获取版本信息数据URL;
    private String urlpath = "http://gdjy.fuziche.com/jpce/app/upgrade/appVersion/edu.html";

    //底部小点的图片
    private ImageView[] points;

    //记录当前选中位置
    private int currentIndex;

    @Bind(R.id.SkipImage)
    ImageView skipImage;

    @Bind(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        initView();
        try {
            HttpRequest.RequestData(urlpath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
    }

    private synchronized void goHome() {

        if (!flag) {
            flag = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initView(){
        //实例化ArrayList对象
        views = new ArrayList<View>();
        //实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //初始化引导图片列表
        for(int i=0; i<pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }
        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(SplashActivity.this);
        runnable = new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        };
        skipImage.postDelayed(runnable,4000);
        skipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        //初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
        points = new ImageView[pics.length];
        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }
        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
    }

    /**
     * 通过点击事件来切换当前的页面
     */
    @Override
    public void onClick(View v) {
        int position = (Integer)v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position){
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon){
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        skipImage.removeCallbacks(runnable);
        ButterKnife.unbind(this);
    }
}
