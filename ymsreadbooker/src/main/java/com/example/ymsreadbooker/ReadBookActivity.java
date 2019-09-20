package com.example.ymsreadbooker;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.ymsreadbooker.adapter.CatalogAdapter;
import com.example.ymsreadbooker.adapter.ReadBackAdater;
import com.example.ymsreadbooker.weight.BookView;

import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ReadBookActivity extends AppCompatActivity implements BookView.BookCallBack {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     * *系统用户界面是否在
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     * *如果设置了，则之后等待的毫秒数
     * *隐藏系统用户界面之前的用户交互。
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     * 一些较旧的设备需要在ui小部件更新之间有一个很小的延迟以及状态和导航栏的更改。
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private DrawerLayout drawerLayout;
    private BookView mContentView;
    private LinearLayout mControlsView;
    private ImageView menuCatalog;
    private ImageView menuSet;
    private ImageView menuMore;
    private RecyclerView catalogRecycler;


    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar 延迟删除状态和导航栏

            // Note that some of these constants are new as of API 16 (Jelly Bean) 注意，这些常量中的一些是API 16（果冻豆）的新常量
            // and API 19 (KitKat). It is safe to use them, as they are inlined 和API 19（Kitkat）。使用它们是安全的，因为它们是内联的
            // at compile-time and do nothing on earlier devices.在编译时，在早期设备上不执行任何操作。
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements ui元素的延迟显示
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };


    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private List<Integer> listColor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        mVisible = true;

        init();
    }


    private void init() {
        initView();
        initData();
        //关闭手势滑动
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        /**
         * 设置字体大小
         */
        mContentView.setTextSize(SPUtils.getInstance("YmsReadBook").
                getInt("fontSize", ConvertUtils.px2dp(mContentView.getTextSize())));

        /**
         * 设置行距
         */
        mContentView.setLineSpacing(0, SPUtils.getInstance("YmsReadBook").getFloat("LineSpacing", 1.5f));

        /**
         * 设置背景颜色
         */
        mContentView.setBackgroundColor(listColor.get(SPUtils.getInstance("YmsReadBook").getInt("BGcolor", 0)));
        mContentView.setCursorVisible(false);
        mContentView.setBook("悲剧的诞生.txt");
        mContentView.setBookCallBack(this);


        /**
         * 设置最大行数
         */
        int textMaxLine = SPUtils.getInstance("YmsReadBook").getInt("TextMaxLine", 0);
        if (textMaxLine >0) {
            mContentView.setMaxLines(textMaxLine);
        }
    }


    /**
     * 触摸用于布局内用户界面控件的侦听器可延迟隐藏系统用户界面。这是为了防止控件在与活动ui交互时发出刺耳的行为。
     */
//    @OnTouch({ R.id.dummy_button})
//    public boolean onDelayHideTouchListener(View v, MotionEvent event) {
//        if (AUTO_HIDE) {
//            delayedHide(AUTO_HIDE_DELAY_MILLIS);
//        }
//        return false;
//    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been 在活动完成后不久触发初始hide（）
        // created, to briefly hint to the user that UI controls 创建向用户简要提示ui控件
        // are available.可用。
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first 先隐藏用户界面
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        // schedule a runnable删除延迟后的状态和导航栏
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar 显示系统栏
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        //schedule a runnable以在延迟后显示ui元素
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     * 以延迟毫秒为单位将调用计划为hide（），取消以前计划的任何调用。
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    /**
     * 召唤菜单
     */
    @Override
    public void callMenu() {
        toggle();
    }



    /**
     * 设置页面
     */
    private int fontSize;
    private ImageView fontSubtract;
    private TextView fontSizeText;
    private ImageView fontAdd;
    private RecyclerView readBackground;
    private BottomNavigationView navigation;

    @SuppressLint("SetTextI18n")
    private void openSet() {
        View menuSet = LayoutInflater.from(this).inflate(R.layout.menu_set, null, false);

        fontSubtract = (ImageView) menuSet.findViewById(R.id.font_subtract);
        fontSizeText = (TextView) menuSet.findViewById(R.id.font_size_text);
        fontAdd = (ImageView) menuSet.findViewById(R.id.font_add);
        readBackground = (RecyclerView) menuSet.findViewById(R.id.read_background);
        navigation = (BottomNavigationView) menuSet.findViewById(R.id.font_spacing);

        PopupWindow popupWindow = new PopupWindow(menuSet,
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT, true);

        // 设置PopupWindow的背景
        popupWindow.showAtLocation(mContentView,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);


        /**
         * 当前字体的大小
         */
        fontSize = SPUtils.getInstance("YmsReadBook").getInt("fontSize", ConvertUtils.px2dp(mContentView.getTextSize()));
        fontSizeText.setText(fontSize + "");


        /**
         * 字体减小
         */
        fontSubtract.setOnClickListener(view -> {
            if (fontSize > 12) {
                fontSize--;
                fontSizeText.setText(fontSize + "");
                mContentView.setTextSize(fontSize);
                setTextLine();
                SPUtils.getInstance("YmsReadBook").put("fontSize", fontSize);
            } else {
                ToastUtils.showLong("字体已为最小");
            }
        });

        /**
         * 字体减小
         */
        fontAdd.setOnClickListener(view -> {
            if (fontSize < 60) {
                fontSize++;
                fontSizeText.setText(fontSize + "");
                mContentView.setTextSize(fontSize);
                setTextLine();
                SPUtils.getInstance("YmsReadBook").put("fontSize", fontSize);
            } else {
                ToastUtils.showLong("字体已为最大");
            }
        });

        /**
         * 设置选中的行距
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            float lineSpacingMultiplier = mContentView.getLineSpacingMultiplier();
            if (lineSpacingMultiplier == 1f) {
                navigation.setSelectedItemId(R.id.font_spacing1);
            } else if (lineSpacingMultiplier == 1.5f) {
                navigation.setSelectedItemId(R.id.font_spacing2);
            } else {
                navigation.setSelectedItemId(R.id.font_spacing3);
            }
        }

        /**
         * 设置行距
         */
        navigation.setOnNavigationItemSelectedListener(menuItem -> {
            int i = menuItem.getItemId();
            if (i == R.id.font_spacing1) {
                mContentView.setLineSpacing(0, 1f);
                setTextLine();
                SPUtils.getInstance("YmsReadBook").put("LineSpacing", 1f);
                return true;
            } else if (i == R.id.font_spacing2) {
                mContentView.setLineSpacing(0, 1.5f);
                setTextLine();
                SPUtils.getInstance("YmsReadBook").put("LineSpacing", 1.5f);
                return true;
            } else if (i == R.id.font_spacing3) {
                mContentView.setLineSpacing(0, 2f);
                setTextLine();
                SPUtils.getInstance("YmsReadBook").put("LineSpacing", 2f);
                return true;
            }
            return false;
        });

        /**
         * 设置背景颜色
         */
        readBackground.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ReadBackAdater readBackAdater = new ReadBackAdater(listColor);
        readBackground.setAdapter(readBackAdater);
        readBackAdater.setOnItemClickListener((adapter, view, position) -> {
            SPUtils.getInstance("YmsReadBook").put("BGcolor", position);
            mContentView.setBackgroundColor(listColor.get(position));
        });
    }




    private void initData() {
        listColor = new ArrayList<>();
        listColor.add(Color.parseColor("#FFFFFF"));
        listColor.add(Color.parseColor("#E6DBBF"));
        listColor.add(Color.parseColor("#BED0BF"));
        listColor.add(Color.parseColor("#F0ECE1"));
        listColor.add(Color.parseColor("#008577"));
        listColor.add(Color.parseColor("#D81B60"));
        listColor.add(Color.parseColor("#00574B"));
    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mContentView = (BookView) findViewById(R.id.fullscreen_content);
        mControlsView = (LinearLayout) findViewById(R.id.fullscreen_content_controls);
        menuCatalog = (ImageView) findViewById(R.id.menu_catalog);
        menuSet = (ImageView) findViewById(R.id.menu_set);
        menuMore = (ImageView) findViewById(R.id.menu_more);
        catalogRecycler = (RecyclerView) findViewById(R.id.catalog_recycler);

        /**
         * 打开  关闭 目录
         */
        menuCatalog.setOnClickListener(v -> {
            toggle();
            if (drawerLayout.isDrawerOpen(catalogRecycler)) {
                drawerLayout.closeDrawer(catalogRecycler);
            } else {
                drawerLayout.openDrawer(catalogRecycler);
            }
            catalogRecycler.setBackgroundColor(listColor.get(SPUtils.getInstance("YmsReadBook").getInt("BGcolor", 0)));
            catalogRecycler.setLayoutManager(new LinearLayoutManager(this));
            CatalogAdapter catalogAdapter = new CatalogAdapter(mContentView.getTitleList());
            catalogRecycler.setAdapter(catalogAdapter);
        });

        /**
         * 打开设置
         */
        menuSet.setOnClickListener(v -> {
            toggle();
            openSet();
        });

        /**
         * 进入更多
         */
        menuMore.setOnClickListener(v -> {

        });
    }


    /**
     * 计算设置最大行数
     */
    public void setTextLine() {
        if (mContentView.getHeight() > 0) {
            int lineSize = mContentView.getHeight()  / mContentView.getLineHeight();

            SPUtils.getInstance("YmsReadBook").put("TextMaxLine", lineSize - 2);

            mContentView.setMaxLines(lineSize - 2);
        }
    }


}
