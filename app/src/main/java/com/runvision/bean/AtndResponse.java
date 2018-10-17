package com.runvision.bean;

public class AtndResponse {

    /**
     * data : [{"coursename":"法律、法规及道路交通信号|机动车基本知识","subject":"1","coursecode":"01,02","classcode":"91620866542145536","targetlen":"45"},{"coursename":"机动车基本知识|第一部分综合复习及考核","subject":"1","coursecode":"02,03","classcode":"100735421960556544","targetlen":"45"},{"coursename":"安全、文明驾驶知识|危险源辨识知识|夜间和高速公路安全驾驶知识|恶劣气象和复杂道路条件下的安全驾驶知识","subject":"4","coursecode":"41,42,43,44","classcode":"100735518626680832","targetlen":"45"},{"coursename":"紧急情况应急处置知识|危险化学品知识|典型事故案例分析|第四部分综合复习及考核","subject":"4","coursecode":"45,46,47,48","classcode":"100735591808897024","targetlen":"45"}]
     * message : 操作成功
     * errorcode : 0
     */

    private String data;
    private String message;
    private String errorcode;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(String errorcode) {
        this.errorcode = errorcode;
    }

    @Override
    public String toString() {
        return "AtndResponse{" +
                "data='" + data + '\'' +
                ", message='" + message + '\'' +
                ", errorcode='" + errorcode + '\'' +
                '}';
    }
}
