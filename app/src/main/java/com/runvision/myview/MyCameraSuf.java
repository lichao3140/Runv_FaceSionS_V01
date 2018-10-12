package com.runvision.myview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.runvision.core.Const;
import com.runvision.core.MyApplication;
import com.runvision.thread.OneVSMoreTask;
import com.runvision.thread.PreviewTask;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/1.
 */

@SuppressLint("NewApi")
public class MyCameraSuf extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "MyCameraSuf";
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Camera mRedCamera;
    private Context mContext;
    private Camera.Parameters parameters;
    private Camera.Parameters parametersRed;
    private PreviewTask task = null;
    public static ExecutorService exec = Executors.newFixedThreadPool(5);
    private static byte[] cameraData = null;
    private static byte[] mRedCameraData = null;


    private boolean flag = false;

    public static byte[] getCameraData() {
        return cameraData;
    }

    public static byte[] getmRedCameraData() {
        return mRedCameraData;
    }

    /**
     * 设置相机用于  0代表抓拍人脸用，1代表注册人脸模版用
     */
    private int camerType = 0;

    public void setCameraType(int type) {
        this.camerType = type;
    }

    public int getCamerType() {
        return camerType;
    }

    public MyCameraSuf(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurfaceHolder = getHolder();
        mContext = context;
        // translucent半透明
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        // transparent透明
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        init();
        getScreenMetrix(context);
        setWillNotDraw(false);
    }

    public MyCameraSuf(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @SuppressWarnings("deprecation")
    public void openCamera() {
        if (flag != false) {
            return;
        }
        try {
            if (Camera.getNumberOfCameras() == 2) {
                mCamera = Camera.open(0);
                mRedCamera = Camera.open(1);
                flag = true;
                System.out.println("open camera");
            } else {
                Toast.makeText(mContext, "相机不是双目的", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            mCamera = null;
            mRedCamera = null;
            Log.d("sulin", "openCamera: open相机失败");
        }
        initCamera();

    }

    private void initCamera() {
        if (mCamera != null && mRedCamera != null) {
            try {
                mCamera.setPreviewCallback(this);
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.addCallbackBuffer(new byte[((Const.PRE_WIDTH * Const.PRE_HEIGTH) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8]);
                if (camerType == 0) {
                    mRedCamera.setPreviewCallbackWithBuffer(this);
                    mRedCamera.addCallbackBuffer(new byte[((Const.PRE_WIDTH * Const.PRE_HEIGTH) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8]);

                    mRedCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            mRedCamera.addCallbackBuffer(data);
                            mRedCameraData = data;
                            if (!MyApplication.mInitStatus) {
                                return;
                            }
                            if (cameraData == null) {
                                return;
                            }

                            if (camerType == 0 && Const.openOneVsMore) {
                                if (oneVSMoreTask != null) {
                                    switch (oneVSMoreTask.getStatus()) {
                                        case RUNNING:
                                            return;
                                        case PENDING:
                                            oneVSMoreTask.cancel(false);
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                oneVSMoreTask = new OneVSMoreTask();
                                oneVSMoreTask.executeOnExecutor(exec);
                            }


                        }
                    });
                }
                mCamera.setDisplayOrientation(0);
                if (parameters == null) {
                    parameters = mCamera.getParameters();
                }
                parameters.setPreviewSize(640, 480);
                parameters.setPreviewFormat(ImageFormat.NV21);
                if (camerType == 0) {
                    //设置显示容器的高宽
                    double pre_width = ((mScreenHeight) / (double) Const.PRE_HEIGTH) * Const.PRE_WIDTH;
                    Const.Panel_width = (int) pre_width;
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) pre_width, mScreenHeight);
                    lp.setMargins(-(Const.Panel_width - mScreenWidth) / 2, 0, 0, 0);
                    this.setLayoutParams(lp);
                } else {
                    Const.Panel_width = 640;
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(640, 480);
                    this.setLayoutParams(lp);
                }


                if (camerType == 0) {
                    mProportionH = (float) mScreenHeight / (float) Const.PRE_HEIGTH;
                    mProportionW = (float) Const.Panel_width / (float) Const.PRE_WIDTH;
                } else {
                    mProportionH = 1;
                    mProportionW = 1;
                }

                mCamera.setParameters(parameters);
                if (camerType == 0) {
                    if (parametersRed == null) {
                        parametersRed = mRedCamera.getParameters();
                    }
                    parametersRed.setPreviewFormat(ImageFormat.NV21);
                    parametersRed.setPreviewSize(640, 480);
                    mRedCamera.setParameters(parametersRed);
                    mRedCamera.startPreview();
                }

                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                System.out.println("开始浏览");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated...");
        // openCamera();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged...");
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed...");
        releaseCamera();
    }

    public synchronized void releaseCamera() {
        if (flag != true) {
            return;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("close 1");
            mCamera = null;
        }

        if (mRedCamera != null) {
            mRedCamera.setPreviewCallbackWithBuffer(null);
            try {
                mRedCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRedCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mRedCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("close 2");
            mRedCamera = null;
        }

        flag = false;
    }

    private OneVSMoreTask oneVSMoreTask;

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCamera.addCallbackBuffer(data);
        cameraData = data;
        if (!MyApplication.mInitStatus) {
            return;
        }
        if (cameraData == null) {
            return;
        }
        if (task != null) {
            switch (task.getStatus()) {
                case RUNNING:
                    return;
                case PENDING:
                    task.cancel(false);
                    break;
                default:
                    break;
            }
        }

        task = new PreviewTask(this);
        task.executeOnExecutor(exec);



    }

    private Paint mPaint;
    private float mProportionH = 0;
    private float mProportionW = 0;
    private int width_offset = 0;
    private int mWeith = 40;
    // 人脸坐标
    private float nFaceLeft, nFaceTop, nFaceRight, nFaceBottom;

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub

        // Log.i(TAG, "onDraw");

        super.onDraw(canvas);
        mPaint.setColor(Color.GREEN);
        // canvas.drawRect(nFaceLeft, nFaceTop, nFaceRight, nFaceBottom,
        // mPaint);
        float startX = Const.Panel_width - nFaceRight;

        float startY = nFaceTop;
        float endX = Const.Panel_width - nFaceLeft;
        float endY = nFaceBottom;

        if (endX - startX < 160) {
            mWeith = 20;
        } else {
            mWeith = 30;
        }
        canvas.drawLine(startX, startY, startX, startY + mWeith, mPaint);
        canvas.drawLine(startX, startY, startX + mWeith, startY, mPaint);
        // 左下
        canvas.drawLine(startX, endY, startX, endY - mWeith, mPaint);
        canvas.drawLine(startX, endY, startX + mWeith, endY, mPaint);
        // 右下
        canvas.drawLine(endX, endY, endX, endY - mWeith, mPaint);
        canvas.drawLine(endX, endY, endX - mWeith, endY, mPaint);
        // 右上
        canvas.drawLine(endX, startY, endX, startY + mWeith, mPaint);
        canvas.drawLine(endX, startY, endX - mWeith, startY, mPaint);
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
        //
        // mProportionH = (float) mScreenHeight / (float) Const.PRE_HEIGTH;
        // mProportionW = (float) Const.Panel_width / (float) Const.PRE_WIDTH;
        //
        // Log.i("test", mProportionW + "*11**" + mProportionH);

    }

    private int mScreenWidth;
    private int mScreenHeight;

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels + 48;
        Log.i("run", "屏幕分辨率：" + mScreenWidth + "*" + mScreenHeight);
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
