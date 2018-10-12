package com.runvision.bean;

public class DeviceResponse {

    private Data data; //附加信息
    private String message; //提示信息
    private String errorcode; //返回码

    public void setData(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    public String getErrorcode() {
        return errorcode;
    }

    @Override
    public String toString() {
        return "Device{" +
                "data=" + data +
                ", message='" + message + '\'' +
                ", errorcode='" + errorcode + '\'' +
                '}';
    }


    public class Data {
        private String privateKey; //RSA私钥
        private long devnum; //终端编号

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setDevnum(long devnum) {
            this.devnum = devnum;
        }

        public long getDevnum() {
            return devnum;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "privateKey='" + privateKey + '\'' +
                    ", devnum=" + devnum +
                    '}';
        }
    }

}
