package com.runvision.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/7/21.
 */

public class FileUtils {

    /**
     * 删除模板图片
     * @param imageID
     * @param path
     */
    public static void deleteTempter(String imageID, String path) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/" + path + "/" + imageID + ".jpg";
        File file = new File(sdCardDir);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void deleteTempter(String imageID) {
        String sdCardDir = imageID + ".jpg";
        File file=new File(sdCardDir);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 保存模板图片
     * @param btImage
     * @param fileName
     * @param DirName
     * @return
     */
    public static File saveFile(Bitmap btImage, String fileName, String DirName) {
        if(btImage==null){
            return null;
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/";
            if (!DirName.equals("")) {
                sdCardDir = sdCardDir + DirName + "/";
            }
            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(sdCardDir, fileName + ".jpg");
            if(file.exists()){
                return file;
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                btImage.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        }
        return null;
    }

    public static boolean socketSaveImage(byte[] data, String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/SocketImage/";

            File dirFile = new File(sdCardDir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File file = new File(sdCardDir, fileName + ".jpeg");
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
                out.write(data, 0, data.length);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return false;
        }
    }

    public static void cleanDir(String dirName) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/" + dirName;
        File file = new File(sdCardDir);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Bitmap loadTempBitmap(String fileName, String DirName) {
        String sdCardDir = Environment.getExternalStorageDirectory() + "/FaceAndroid/" + DirName + "/" + fileName + ".jpg";
        File file = new File(sdCardDir);
        if (!file.exists()) {
            return null;
        }
        return getSmallBitmap(sdCardDir);
    }

    private static Bitmap getSmallBitmap(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);

        int tempWidth = options.outWidth;
        int tempHeight = options.outHeight;

        while (tempWidth > 1024 || tempHeight > 1024) {
            tempWidth /= 2;
            tempHeight /= 2;
        }
        options.inSampleSize = calculateInSampleSize(options, tempWidth, tempHeight);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return options;
    }

    /**
     * 把身份证的图片变成算法需要的BGR24
     *
     * @param bmp
     * @return
     */
    public static byte[] bitmapToBGR24(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        byte[] value = null;

        int[] pixels = new int[width * height];
        // 获取RGB32数据
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        byte[] BGR24 = new byte[width * height * 3];
        //byte[] gray = new byte[width * height];
        // 获取图片的RGB24数据和灰度图数据
        for (int i = 0; i < width * height; i++) {
            int r = (pixels[i] >> 16) & 0x000000FF;
            int g = (pixels[i] >> 8) & 0x000000FF;
            int b = pixels[i] & 0x000000FF;
            BGR24[i * 3] = (byte) (b & 0xFF);
            BGR24[i * 3 + 1] = (byte) (g & 0xFF);
            BGR24[i * 3 + 2] = (byte) (r & 0xFF);
        }

        return BGR24;

    }


    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static String bitmaptoString(Bitmap bitmap) {
        //将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }


}
