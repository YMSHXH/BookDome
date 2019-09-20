package com.example.ymsreadbooker.weight;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.example.ymsreadbooker.bean.InfoVo;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileName: BookPageView
 * Author: xh`yang
 * Date: 2019/9/14 21:11
 * Description:
 */
@SuppressLint("AppCompatCustomView")
public class BookView extends TextView implements GestureDetector.OnGestureListener {

    private GestureDetector gestureDetector;
    private static final int FLING_MIN_DISTANCE = 50;
    private static final int FLING_MIN_VELOCITY = 0;
    private BookCallBack bookCallBack;
    private Context mContext;

    private String strTxt;
    private List<InfoVo> titleList;

    public List<InfoVo> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<InfoVo> titleList) {
        this.titleList = titleList;
    }

    public void setBookCallBack(BookCallBack bookCallBack) {
        this.bookCallBack = bookCallBack;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    setText(strTxt);
                    break;
                case 1:
                    Toast.makeText(mContext, "不支持此编码，请换成utf-8编码！", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(mContext, "未找到文件！", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    };
    private int SCREEN_WIDTH = ScreenUtils.getScreenWidth();
    private int SCREEN_HEIGHT = ScreenUtils.getScreenHeight();


    public BookView(Context context) {
        this(context,null);
    }

    public BookView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }



    public void setBook(final String pash){
        new Thread(){
            @Override
            public void run() {
                try {
                    //InputStream open = getResources().getAssets().open(pash);
                    InputStream inputStream = new FileInputStream(pash);
                    InputStreamReader read = new InputStreamReader(inputStream,"GBK");
                    BufferedReader bufferedReader = new BufferedReader(read);
                    StringBuffer stringBuffer = new StringBuffer();
                    String lineTxt = null;
                    int offset = 0; //章节所在行数
                    int count = 1; //章节数
                    titleList = new ArrayList<InfoVo>();
                    InfoVo infoVo;
                    while ((lineTxt = bufferedReader.readLine()) != null) {
                        stringBuffer.append("\n"+lineTxt);
                        infoVo = new InfoVo();
                        offset++;
                        if (lineTxt.contains("第") && lineTxt.contains("章")) {
                            infoVo.setCount(count);
                            infoVo.setOffset(offset);
                            infoVo.setTitle(lineTxt);
                            titleList.add(infoVo);
                            count++;
                        }
                    }
                    strTxt = stringBuffer.toString();
                    handler.sendEmptyMessage(0);
                    bufferedReader.close();
                    read.close();
                } catch (UnsupportedEncodingException e1) {
                    handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    handler.sendEmptyMessage(2);
                }
            }
        }.start();

    }


    private void initView() {
        gestureDetector = new GestureDetector(mContext,this);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });


    }

    /**
     * 手指轻碰屏幕的一瞬间 由1个ACTION_DOWN触发
     *
     * @param e
     * @return
     */
    @Override
    public boolean onDown(MotionEvent e) {
        if (e.getX() >= SCREEN_WIDTH / 3
                && e.getX() <= SCREEN_WIDTH * 2 / 3
                && e.getY() <= SCREEN_HEIGHT * 3 / 4){//召唤菜单
            //Toast.makeText(mContext, "召唤菜单", Toast.LENGTH_SHORT).show();
            //Log.e("bookView","宽"+ SCREEN_WIDTH+",高"+ SCREEN_HEIGHT);
            bookCallBack.callMenu();

            return true;
        } else if (e.getX() < SCREEN_WIDTH / 3
                && e.getY() < SCREEN_HEIGHT * 3 / 4 ){ //上一页
            //Log.e("bookView","上一页");
            if (getScrollY() <= getHeight())
                scrollTo(0, 0);
            else
                scrollTo(0, getScrollY() - getHeight());
            return false;
        } else { //下一页
            //Log.e("bookView","下一页");
            if (getScrollY() >= getLineCount()
                    * getLineHeight() - getHeight() * 2)
                scrollTo(
                        0,
                        getLineCount() * getLineHeight()
                                - getHeight());
            else
                scrollTo(0, getScrollY() + getHeight());
            return false;
        }
    }

    /**
     * 手指轻碰屏幕的一瞬间,尚未松开或拖动  由1个ACTION_DOWN触发
     * 与onDown区别：强调没有松开或者拖动的状态
     * @param e
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * 手指轻碰屏幕后松开，伴随ACTION_UP而触发
     * 单击行为
     * @param e
     * @return
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     *  手指按下屏幕并拖动，由1个ACTION_DOWN，多个ACTION_MOVE触发
     *  拖动行为
     * @param e1
     * @param e2
     * @param distanceX
     * @param distanceY
     * @return
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * 长按事件
     * @param e
     */
    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * 快速滑动
     * @param e1
     * @param e2
     * @param velocityX
     * @param velocityY
     * @return
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    /**
     * 阅读器相关回调
     */
    public interface BookCallBack{
        /**
         * 召唤菜单
         */
        void callMenu();

    }



//    /**
//     * 计算设置最大行数
//     */
//    public void setTextLine() {
//        if (getHeight() > 0) {
//            int lineSize = getHeight()  / getLineHeight();
//            setMaxLines(lineSize - 1);
//        }
//    }
}
