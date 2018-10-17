package com.runvision.bean;

public class Cours {

    /**
     * coursename : 法律、法规及道路交通信号|机动车基本知识
     * subject : 1
     * coursecode : 01,02
     * classcode : 91620866542145536
     * targetlen : 45
     */

    /**
     * 课程名称
     */
    private String coursename;

    /**
     * 科目
     */
    private String subject;

    /**
     * 课程编码
     */
    private String coursecode;

    /**
     * 课堂编码
     */
    private String classcode;

    /**
     * 目标学习时长
     */
    private String targetlen;

    public String getCoursename() {
        return coursename;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCoursecode() {
        return coursecode;
    }

    public void setCoursecode(String coursecode) {
        this.coursecode = coursecode;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getTargetlen() {
        return targetlen;
    }

    public void setTargetlen(String targetlen) {
        this.targetlen = targetlen;
    }

    @Override
    public String toString() {
        return "Cours{" +
                "coursename='" + coursename + '\'' +
                ", subject='" + subject + '\'' +
                ", coursecode='" + coursecode + '\'' +
                ", classcode='" + classcode + '\'' +
                ", targetlen='" + targetlen + '\'' +
                '}';
    }
}
