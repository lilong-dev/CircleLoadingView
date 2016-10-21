package com.ll.circleloadingview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

/**
 * Created by ll on 2016/10/20.
 */
public class CircleLoadingView extends View{
    private static final String TAG = "CircleLoadingView";

    private static final float MAX_PROGRESS = 100f;
    private PorterDuffXfermode mMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    private Paint circlePaint;//绘制圆画笔
    private Paint progressPaint;//绘制进度画笔
    private Paint borderPaint;//绘制边框画笔
    private Paint progressTextPaint;//绘制进度文本画笔
    private Canvas mCanvas;
    private Bitmap bitmap;

    private int circleViewBgColor;//view 的背景色
    private int progressBgColor;//进度背景色
    private int borderColor;//边框颜色
    private float borderWidth;//边框的宽度
    private int progressTextColor;//进度文本的颜色
    private int progressTextSize;//进度文本的大小

    private Path mPath;

    private int viewSize;
    private int progress = 50;
    public CircleLoadingView(Context context) {
        this(context,null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CircleLoadingView,defStyleAttr,0);
        circleViewBgColor = ta.getColor(R.styleable.CircleLoadingView_circleViewBgColor, Color.parseColor("#CCCCCC"));
        progressBgColor = ta.getColor(R.styleable.CircleLoadingView_progressBgColor,Color.parseColor("#4291F1"));
        borderColor = ta.getColor(R.styleable.CircleLoadingView_borderColor,Color.parseColor("#00FF00"));
        borderWidth = ta.getDimensionPixelSize(R.styleable.CircleLoadingView_borderWidth,40);
        progressTextColor = ta.getColor(R.styleable.CircleLoadingView_progressTextColor,Color.parseColor("#FFFFFF"));
        progressTextSize = ta.getDimensionPixelSize(R.styleable.CircleLoadingView_progressTextSize,sp2x(12));
        ta.recycle();
        this.init();
    }

    private void init() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleViewBgColor);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressBgColor);

        progressTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressTextPaint.setColor(progressTextColor);
        progressTextPaint.setTextSize(progressTextSize);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        viewSize = Math.min(widthSize,heightSize);
        setMeasuredDimension(viewSize,viewSize);

        bitmap = Bitmap.createBitmap(viewSize,viewSize, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        if(borderWidth > viewSize * 1/4){
            borderWidth = viewSize *1/4;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制border
        float halfViewSize = viewSize / 2f;
        if(borderWidth > 0 ){
            canvas.drawCircle(halfViewSize,halfViewSize,halfViewSize - borderWidth / 2f,borderPaint);//border
        }
        //清除掉图像 不然path会重叠,不加此行代码，导致修改borderWidth不起作用
        bitmap.eraseColor(Color.parseColor("#00000000"));
        //绘制圆
        mCanvas.drawCircle(halfViewSize,halfViewSize,halfViewSize - borderWidth,circlePaint);
        //绘制进度
        int y = (int)(viewSize -borderWidth) - (int)((progress / MAX_PROGRESS)*(viewSize -2 *borderWidth));
        mPath.reset();
        mPath.moveTo(0,y);
        mPath.lineTo(viewSize,y);
        mPath.lineTo(viewSize,viewSize);
        mPath.lineTo(0,viewSize);
        mPath.close();
        progressPaint.setXfermode(mMode);
        mCanvas.drawPath(mPath,progressPaint);
        progressPaint.setXfermode(null);
        canvas.drawBitmap(bitmap,0,0,null);
        //绘制进度文本
        drawProgressText(canvas);

    }

    /**
     * 绘制进度文本
     * @param canvas
     */
    private void drawProgressText(Canvas canvas) {
        String text = progress + "%";
        float textWidth = progressTextPaint.measureText(text);//测量文本的宽度
        Paint.FontMetrics fm = progressTextPaint.getFontMetrics();
        float baseLine = viewSize / 2 - fm.descent + (fm.bottom-fm.top)/2;
        canvas.drawText(text,viewSize / 2 - textWidth / 2,baseLine,progressTextPaint);
    }

    private int dp2px(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,getResources().getDisplayMetrics());
    }

    private int sp2x(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value,getResources().getDisplayMetrics());
    }

    public void setPercent(int percent){
        this.progress = percent;
        postInvalidate();
    }

    /**
     * 设置边框颜色
     * @param color
     */
    public void setBorderColor(int color){
        this.borderColor = color;
        borderPaint.setColor(color);
        postInvalidate();
    }

    /**
     * 设置边框宽度
     * @param width
     */
    public void setBorderWidth(float width){
       this.borderWidth = width;
        if(borderWidth > viewSize * 1/4){
            borderWidth = viewSize *1/4;
        }
        borderPaint.setStrokeWidth(borderWidth);
        postInvalidate();
    }

    public void  setProgressBgColor(int color){
        this.progressBgColor = color;
        progressPaint.setColor(color);
        postInvalidate();
    }

    public void setViewBgColor(int color){
        this.circleViewBgColor = color;
        circlePaint.setColor(color);
        postInvalidate();
    }

    public void setProgressTextColor(int color){
        this.progressTextColor = color;
        progressTextPaint.setColor(color);
        postInvalidate();
    }

    public void setProgressTextSize(int size){
        this.progressTextSize = size;
        progressTextPaint.setTextSize(size);
        postInvalidate();
    }
}
