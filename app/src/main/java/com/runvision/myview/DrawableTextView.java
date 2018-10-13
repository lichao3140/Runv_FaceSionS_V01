package com.runvision.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.runvision.firs_facesions.R;

/**
 * Created by ChaoLi on 2018/10/13 0013 - 12:28
 * Email: lichao3140@gmail.com
 * Version: v1.0
 */
public class DrawableTextView extends AppCompatTextView {

    private Context mContext;
    /**
     * resID
     */
    private int drawableLeft ;
    private int drawableRight ;
    private int drawableTop ;
    private int drawableBottom ;
    /**
     * 图标宽度
     */
    private float drawableWidth ;
    /**
     * 图标高度
     */
    private float drawableHeight ;

    public DrawableTextView(Context context) {
        this(context,null);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context ;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.drawableText);
            try {
                drawableLeft = a.getResourceId(R.styleable.drawableText_leftDrawable, 0);
                drawableRight = a.getResourceId(R.styleable.drawableText_rightDrawable, 0);
                drawableTop = a.getResourceId(R.styleable.drawableText_topDrawable, 0);
                drawableBottom = a.getResourceId(R.styleable.drawableText_bottomDrawable, 0);

                drawableWidth = a.getDimensionPixelSize(R.styleable.drawableText_drawableWidth, 30);
                drawableHeight = a.getDimensionPixelSize(R.styleable.drawableText_drawableHeight, 30);

            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
        init();
    }

    private void init() {
        Drawable leftDrawable = null ;
        if (drawableLeft!=0)
        {
            leftDrawable = ContextCompat.getDrawable(mContext,drawableLeft);
            leftDrawable.setBounds(0, 0, (int)drawableWidth, (int)drawableHeight);
        }
        Drawable rightDrawable = null;
        if(drawableRight!=0){
            rightDrawable = ContextCompat.getDrawable(mContext,drawableRight);
            rightDrawable.setBounds(0, 0, (int)drawableWidth, (int)drawableHeight);
        }
        Drawable topDrawable = null;
        if (drawableTop!=0){
            topDrawable = ContextCompat.getDrawable(mContext,drawableTop);
            topDrawable.setBounds(0, 0, (int)drawableWidth, (int)drawableHeight);
        }
        Drawable bottomDrawable = null ;
        if (drawableBottom!=0){
            bottomDrawable = ContextCompat.getDrawable(mContext,drawableBottom);
            bottomDrawable.setBounds(0, 0, (int)drawableWidth, (int)drawableHeight);
        }

        this.setCompoundDrawables(leftDrawable,topDrawable,rightDrawable,bottomDrawable);

    }
}
