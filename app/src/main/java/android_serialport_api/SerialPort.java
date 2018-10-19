/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    private static final String TAG = "SerialPort";

    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public static boolean Fill_in_light=false;//add by 20180806 Gavin

    public SerialPort(File device, int baudrate, int flags) throws SecurityException, IOException {
        /* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());

                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }

        mFd = open(device.getAbsolutePath(), baudrate, flags);

        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }

        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // JNI
    private native static FileDescriptor open(String path, int baudrate, int flags);

    public native void close();

    static {
        System.loadLibrary("serial_port");
    }


    private static SerialPort mSerialPort;
    protected static InputStream mInputStream;
    protected static OutputStream mOutputStream;
    private static String prot = "ttyS1";// 串口号（具体的根据自己的串口号来配置）
    private static int baudrate = 9600;// 波特率（可自行设定）
    private static boolean status = false;

    public synchronized static void openLED() {
        if (status) return;

        // 配置并打开串口
        try {
            if (mSerialPort == null)
                mSerialPort = new SerialPort(new File("/dev/" + prot), baudrate, 0);
            mInputStream = mSerialPort.getInputStream();
            mOutputStream = mSerialPort.getOutputStream();
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        byte[] b = {(byte) 0xFD, (byte) 0x09, (byte) 0x13};
                        mOutputStream.write(b);
                        status = true;
                        Fill_in_light=true;
                        Log.i("test", "发送成功");
                        Thread.sleep(39 * 1000);
                        status = false;
                    } catch (Exception e) {
                        status = false;
                        Log.i("test", "发送失败");
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (SecurityException e) {
            status = false;
            e.printStackTrace();
        } catch (IOException e) {
            status = false;
            Log.i("test", "打开失败");
            e.printStackTrace();
        }
    }
}
