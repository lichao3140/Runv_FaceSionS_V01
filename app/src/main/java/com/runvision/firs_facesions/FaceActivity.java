package com.runvision.firs_facesions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.utils.SharedPreferencesHelper;

public class FaceActivity extends BaseActivity {

    private SharedPreferencesHelper faceSP;
    private Context context;
    private FaceFrameView myFaceFrameView;
    private MyCameraSuf myCameraView;
    private ImageView ivFace;
    private RelativeLayout show_card;
    private TextView loadcardText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        context = this;
        faceSP = new SharedPreferencesHelper(context, "faceInfo");
        init();
    }

    private void init() {
        myFaceFrameView = findViewById(R.id.myFaceFrameView);
        myCameraView = findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();

        show_card	= findViewById(R.id.show_card);
        loadcardText = findViewById(R.id.loadcardText);
        ivFace = findViewById(R.id.iv_face);
        new Thread() { // 将服务器返回的Base64数据转换成图片
            @Override
            public void run() {
                runOnUiThread(() -> {
                    String faceInfo = faceSP.getSharedPreference("face", "").toString().trim();
                    byte[] decode = Base64.decode(faceInfo, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
                    ivFace.setImageBitmap(bitmap);
                });
            }
        }.start();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myCameraView.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCameraView.releaseCamera();
    }
}
