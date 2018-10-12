package com.face.sv;

/**
 * 人脸识别算法库的人脸检测JNI接口。
 *
 * @author zoufeng
 * @datetime 2018-01-30
 */
public class FaceFeatureNative {

    static {
        System.loadLibrary("THFeature");
    }

    /**
     * 获取已知人脸坐标的人脸图片中人脸模板
     *
     * @param BGR24    人脸图片BGR24
     * @param faceInfo 图片人脸信息, 数组长度580(有多个人脸则 截取需要检测的人脸信息)
     * @param width    图片宽度
     * @param height   图片高度
     * @return 人脸模板数据，成功数组长度2008字节, 失败数组长度为1，byte[0]返回0或负数。
     * (-101表示传入数据为空;-102表示malloc模型失败;103表示传入的faceInfo信息错误;
     */
    public static native byte[] getFaceFeature(byte[] BGR24, byte[] faceInfo, int width, int height);

    /**
     * 比对两个人脸模板相似度
     *
     * @param feature1 人脸模板
     * @param feature2 人脸模板
     * @return 相识度（分值范围0 ~ 100之间）， -1 表示参数feature1或feature2为null.
     */
    public static native int compareFeature(byte[] feature1, byte[] feature2);

}
