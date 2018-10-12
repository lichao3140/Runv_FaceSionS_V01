package com.runvision.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.runvision.core.Const;

public class FaceFrameView extends View {

    private Context mContext;
    private Paint mPaint;
    private int screenWidth, screenHeight;
    private float mProportionH = 0;
    private float mProportionW = 0;

    private float nFaceLeft, nFaceTop, nFaceRight, nFaceBottom; // 人脸坐标

    public FaceFrameView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public FaceFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        // 设置画笔为抗锯齿
        mPaint.setAntiAlias(true);
        // 设置颜色为
        mPaint.setColor(Color.GREEN);
        /**
         * 画笔样式分三种： 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充
         * 3.Paint.Style.FILL：填充
         */
        mPaint.setStyle(Paint.Style.STROKE);
        /**
         * 设置描边的粗细，单位：像素px 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
         */
        mPaint.setStrokeWidth(3);

        /**
         * 获取屏幕的宽高
         */
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();
        Log.i("test", screenWidth + "*" + screenHeight);
        mProportionH = (float) screenHeight / (float) Const.PRE_HEIGTH;
        mProportionW = (float) screenWidth / (float) Const.PRE_WIDTH;
    }

    /**
     * 由于onDraw()方法会不停的绘制 所以需要定义一个初始化画笔方法 避免导致不停创建造成内存消耗过大
     */
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        mPaint.setColor(Color.GREEN);

        canvas.drawRect(screenWidth - nFaceRight, nFaceTop, screenWidth - nFaceLeft, nFaceBottom, mPaint);

    }

    // 可能需要修改的
    public void setFacePamaer(Rect rect) {


        if (rect.top == 0 && rect.left == 0 && rect.right == 0 && rect.bottom == 0) {
            this.nFaceLeft = 0;
            this.nFaceTop = 0;
            this.nFaceRight = 0;
            this.nFaceBottom = 0;
        } else {
            this.nFaceLeft = rect.left * mProportionW;
            this.nFaceTop = rect.top * mProportionH;
            this.nFaceRight = rect.right * mProportionW;
            this.nFaceBottom = rect.bottom * mProportionH;
        }
        postInvalidate();

    }

}
