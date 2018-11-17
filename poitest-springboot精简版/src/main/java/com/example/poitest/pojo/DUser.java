package com.example.poitest.pojo;

import com.example.poitest.annotation.ExcelVOAttribute;
/**
 * User:xuyalun
 * Date 2018/11/16
 * Time 15:25
 */
public class DUser {
    @ExcelVOAttribute(name = "序号", column = "A",isExport = true)
    private Integer id;
    @ExcelVOAttribute(name = "姓名", column = "B", isExport = true)
    private String name;
    @ExcelVOAttribute(name = "年龄", column = "C", isExport = true)
    private String age;
    @ExcelVOAttribute(name = "性别", column = "D", isExport = true)
    private String sex;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age == null ? null : age.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    @Override
    public String toString() {
        return "DUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}