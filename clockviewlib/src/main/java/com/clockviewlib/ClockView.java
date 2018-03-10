package com.clockviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.tentcoostudio.clockviewlib.R;

import java.util.ArrayList;

/**
 * @author: Frank
 * @time: 2018/3/7 21:53
 * 实现的主要功能:
 */

public class ClockView extends View {
    //外圆画笔
    private Paint paint;
    //文字画笔
    private Paint paintNum;
    //小圆画笔
    private Paint paintSmall;
    //线画笔
    private Paint paintLine;
    //中心坐标
    private float x,y;
    //触摸坐标
    private float X,Y;
    //字体与外圆的边缘的距离
    private float outDistance;
    //字体与内圆的边缘的距离
    private float inDistance;
    //字体位置
    private float textLocal;
    //大圆半径
    private int radius;
    //选中的位置
    private int selectedNum = 0;
    //设置时钟格式
    private int count;
    public static int CLOCK_HOUR = 24;
    public static int CLOCK_MINUTE = 60;
    //组件位置用到的颜色
    private int circleBackground;
    private int smallCircleColor;
    private int selectTextColor;
    private int unSelectTextColor;
    private int lineColor;
    private int circleTextColor;
    //保存初始化信息
    private ArrayList<Float> saveX;
    private ArrayList<Float> saveY;
    private ArrayList<Integer> saveText;

    private ClockTouchListener clockTouchListener;

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initParams(context,attrs);
    }


    /**
     * 初始化画笔
     * */
    private void init(){
        //大圆画笔
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(circleBackground);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);

        //字体画笔
        paintNum = new Paint();
        paintNum.setAntiAlias(true);
        paintNum.setStyle(Paint.Style.FILL);
        paintNum.setTextAlign(Paint.Align.CENTER);
        //设置字体大小
        paintNum.setTextSize(radius/10);
        //设置文字垂直居中
        textLocal = (paintNum.getFontMetrics().bottom - paintNum.getFontMetrics().top);

        //小圆画笔
        paintSmall = new Paint();
        paintSmall.setStrokeWidth(2);
        paintSmall.setColor(smallCircleColor);
        paintSmall.setAntiAlias(true);
        paintSmall.setStyle(Paint.Style.FILL);

        //线条画笔
        paintLine = new Paint();
        paintLine.setStrokeWidth(3);
        paintLine.setColor(lineColor);
        paintLine.setAntiAlias(true);
        paintLine.setStyle(Paint.Style.FILL_AND_STROKE);
    }
    /**
     * 属性
     * */
    private void initParams(Context context, AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.ClockView);
        if (typedArray != null){
            circleBackground = typedArray.getColor(R.styleable.ClockView_bigCircleColor,
                    Color.parseColor("#d2d2d2"));
            smallCircleColor = typedArray.getColor(R.styleable.ClockView_smallCircleColor,
                    Color.parseColor("#ff0000"));
            selectTextColor = typedArray.getColor(R.styleable.ClockView_selectTextColor,
                    Color.parseColor("#222222"));
            unSelectTextColor = typedArray.getColor(R.styleable.ClockView_unSelectTextColor,
                    Color.parseColor("#b7b7b7"));
            lineColor = typedArray.getColor(R.styleable.ClockView_lineColor,
                    Color.parseColor("#ff0000"));
            circleTextColor = typedArray.getColor(R.styleable.ClockView_circleTextColor,
                    Color.parseColor("#ffffff"));
            count = typedArray.getInteger(R.styleable.ClockView_timeFormat,CLOCK_HOUR);
            typedArray.recycle();
        }
    }
    /**
     * 设置时间格式
     * */
    public void setCount(int count){
        this.count = count;
    }
    /**
     * 设置选中圈的字体颜色
     * */
    public void setSelectTextColor(int selectTextColor){
        this.selectTextColor = selectTextColor;
    }
    /**
     * 设置未选中圈的字体颜色
     * */
    public void setUnSelectTextColor(int unSelectTextColor){
        this.unSelectTextColor = unSelectTextColor;
    }
    /**
     * 设置中心线的颜色
     *
     * @param lineColor*/
    public void setLineColor(int lineColor){
        this.lineColor = lineColor;
        postInvalidate();
    }
    /**
     * 设置大圆背景颜色
     * */
    public void setCircleBackground(int circleBackground){
        this.circleBackground = circleBackground;
    }
    /**
     * 设置小圆颜色
     * */
    public void setSmallCircleColor(int smallCircleColor){
        this.smallCircleColor = smallCircleColor;
    }
    /**
     * 设置选中字体颜色
     * */
    public void setCircleTextColor(int circleTextColor){
        this.circleTextColor = circleTextColor;
    }

    public interface ClockTouchListener{
        void getClockText(String num);
    }

    public void setClockTouchListener(ClockTouchListener clockTouchListener){
        this.clockTouchListener = clockTouchListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        setMeasuredDimension(width,width);
        y = width / 2;
        x = width / 2;
        radius = (int) (x - 5);
        //外圆数字离圆心的距离
        outDistance = radius - radius/5;
        //内圆数字离圆心的距离
        inDistance = (float) (radius - radius/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        canvas.drawCircle(x,y,radius,paint);
        drawText(canvas);
        if (saveText.get(selectedNum) <= 12){
            //若选中值小于等于12，则识别位于外圈值，设置外圈色深，内圈色浅
            drawLineAndCircle(canvas,selectTextColor,unSelectTextColor);
        }else if (saveText.get(selectedNum) <= 24 && saveText.get(selectedNum) > 12){
            //若选中值大于12，则识别位于内圈值，设置外圈色浅，内圈色深
            drawLineAndCircle(canvas,unSelectTextColor,selectTextColor);
        }
    }

    /**
     * 绘制文本
     * */
    private void drawText(Canvas canvas){
        saveX = new ArrayList<>();
        saveY = new ArrayList<>();
        saveText = new ArrayList<>();
        //数字坐标a,b
        float a,b;
        for (int i = 0; i < 24; i++){
            if (i < 12) {
                paintNum.setColor(selectTextColor);
                a = (float) (outDistance * Math.sin(i * 30 * Math.PI / 180) + x);
                b = (float) (y - outDistance * Math.cos(i * 30 * Math.PI / 180));
                if (count == CLOCK_HOUR) {
                    //格式为小时，直接设置数值
                    if (i == 0) {
                        saveText.add(12);
                        canvas.drawText("12", a, b + textLocal / 3, paintNum);
                    } else {
                        saveText.add(i);
                        canvas.drawText(i + "", a, b + textLocal / 3, paintNum);
                    }
                }else if (count == CLOCK_MINUTE){
                    //格式为分钟，转换数值格式
                    saveText.add(i);
                    canvas.drawText(int2string(i), a, b + textLocal / 3, paintNum);
                }
                saveX.add(a);
                saveY.add(b);
            }else if (i >= 12 && i < 24 && count == CLOCK_HOUR) {
                //格式为小时，绘制内圈，否则当格式为分钟时，不绘制
                paintNum.setColor(unSelectTextColor);
                a = (float) (inDistance * Math.sin(i * 30 * Math.PI / 180) + x);
                b = (float) (y - inDistance * Math.cos(i * 30 * Math.PI / 180));
                if (i == 12) {
                    saveText.add(24);
                    canvas.drawText("00", a, b + textLocal / 3, paintNum);
                } else {
                    saveText.add(i);
                    canvas.drawText(i + "", a, b + textLocal / 3, paintNum);
                }
                saveX.add(a);
                saveY.add(b);
            }
        }
    }

    /**
     * 绘制中心线与小圆
     * */
    private void drawLineAndCircle(Canvas canvas,int selectTextColor,int unSelectTextColor){
        for (int i = 0; i < saveText.size(); i++) {
            float a = saveX.get(i);
            float b = saveY.get(i);
            //手指触摸坐标达到范围值，将会自动选中最近的值
            if (Math.abs(X - a) < radius/6 && Math.abs(Y - b) < radius/6 && saveText.get(selectedNum) <= 12) {
                paintNum.setColor(circleTextColor);
                canvas.drawLine(x, y, a, b, paintLine);
                canvas.drawCircle(a, b, radius / 7, paintSmall);
                if (count == CLOCK_HOUR) {
                    //格式为小时，直接获取值
                    canvas.drawText(""+(saveText.get(i) == 24 ? "00" : saveText.get(i)), a, b + textLocal / 3, paintNum);
                }else if (count == CLOCK_MINUTE){
                    //格式为分钟，转为双位数的值
                    canvas.drawText(int2string(saveText.get(i)), a, b + textLocal / 3, paintNum);
                }
                //保存选中值的数组位置
                selectedNum = i;
            } else if (Math.abs(X - a) < radius/11 && Math.abs(Y - b) < radius/11 && saveText.get(selectedNum) > 12) {
                paintNum.setColor(circleTextColor);
                canvas.drawLine(x, y, a, b, paintLine);
                canvas.drawCircle(a, b, radius / 7, paintSmall);
                if (count == CLOCK_HOUR) {
                    //格式为小时，直接获取值
                    canvas.drawText(""+(saveText.get(i) == 24 ? "00" : saveText.get(i)), a, b + textLocal / 3, paintNum);
                }else if (count == CLOCK_MINUTE){
                    //格式为分钟，转为双位数的值
                    canvas.drawText(int2string(saveText.get(i)), a, b + textLocal / 3, paintNum);
                }
                //保存选中值的数组位置
                selectedNum = i;
            } else {
                //格式为小时，显示两圈数字，直接获取值
                if (saveText.get(i) <= 12 && count == CLOCK_HOUR) {
                    paintNum.setColor(selectTextColor);
                    canvas.drawText(""+(saveText.get(i) == 24 ? "00" : saveText.get(i)), a, b + textLocal / 3, paintNum);
                } else if (saveText.get(i) <= 24 && saveText.get(i) > 12 && count == CLOCK_HOUR) {
                    paintNum.setColor(unSelectTextColor);
                    canvas.drawText(""+(saveText.get(i) == 24 ? "00" : saveText.get(i)), a, b + textLocal / 3, paintNum);
                } else if (count == CLOCK_MINUTE){
                    //格式为分钟，只显示一圈数字，转为双位数的值
                    paintNum.setColor(selectTextColor);
                    canvas.drawText(int2string(saveText.get(i)), a, b + textLocal / 3, paintNum);
                }
            }
        }
        //绘制选中位置的文本与小圆
        paintNum.setColor(circleTextColor);
        canvas.drawLine(x, y, saveX.get(selectedNum), saveY.get(selectedNum), paintLine);
        canvas.drawCircle(saveX.get(selectedNum), saveY.get(selectedNum), radius / 7, paintSmall);
        if (count == CLOCK_HOUR) {
            //格式为小时，直接获取值
            canvas.drawText(""+(saveText.get(selectedNum) == 24 ? "00" : saveText.get(selectedNum)), saveX.get(selectedNum), saveY.get(selectedNum) + textLocal / 3, paintNum);
            if (saveText.get(selectedNum) < 10) {
                clockTouchListener.getClockText("0"+saveText.get(selectedNum));
            }else clockTouchListener.getClockText(""+(saveText.get(selectedNum) == 24 ? "00" : saveText.get(selectedNum)));
            invalidate();
        }else if (count == CLOCK_MINUTE){
            //格式为分钟，转为双位数的值
            canvas.drawText(int2string(saveText.get(selectedNum)), saveX.get(selectedNum), saveY.get(selectedNum) + textLocal / 3, paintNum);
            clockTouchListener.getClockText(int2string(saveText.get(selectedNum)));
            invalidate();
        }
    }
    /**
     * 触摸反馈
     * */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                X = event.getX();
                Y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                X = event.getX();
                Y = event.getY();
                break;
        }
        return true;
    }

    /**
     * 将数值格式转换为两位数
     * */
    private String int2string(int num){
        switch (num){
            case 0:
                return "00";
            case 1:
                return "05";
            case 2:
                return "10";
            case 3:
                return "15";
            case 4:
                return "20";
            case 5:
                return "25";
            case 6:
                return "30";
            case 7:
                return "35";
            case 8:
                return "40";
            case 9:
                return "45";
            case 10:
                return "50";
            case 11:
                return "55";
            default:
                return "";
        }
    }
}
