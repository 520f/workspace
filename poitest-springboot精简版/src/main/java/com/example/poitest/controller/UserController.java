package com.example.poitest.controller;


import com.example.poitest.pojo.DUser;
import com.example.poitest.util.ExcelUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * User:xuyalun
 * Date 2018/11/16
 * Time 15:25
 */
@Controller
public class UserController {

    /**
     * 测试url：http://localhost:8080/testImportExcel
     *
     * 测试导入
     * @return
     */
    @RequestMapping("/testImportExcel")
    @ResponseBody
    public Object testExceptExcel(){
        //绝对路径（将项目下的文件复制到任意目录）
        String filePath="D:\\学生信息.xlsx";
        // 创建excel工具类
        ExcelUtil<DUser> util = new ExcelUtil<>(DUser.class);
        List<DUser> userList = null;
        try {
            //传入参数是一个FileInputStream对象，FileInputStream可以直接加载文件获取，
            // 也可以通过web表单获取（本次测试是加载的项目下的文件）
            userList = util.importExcel(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //输出表格中数据
        System.out.println("userList:"+userList);
        return userList;
    }
    /**
     *  测试url：http://localhost:8080/testExceptExcel
     *     导出测试
     * @param response
     */
    @RequestMapping("/testExceptExcel")
    private void testExceptExcel(HttpServletResponse response){
        // 创建excel工具类
        ExcelUtil<DUser> util = new ExcelUtil<>(DUser.class);
        //制造数据
        List list = new ArrayList();
        DUser user01 = new DUser();
        user01.setId(1);
        user01.setName("漳卅1");
        user01.setAge("18");
        user01.setSex("男");
        DUser user02 = new DUser();
        user02.setId(2);
        user02.setName("漳卅2");
        user02.setAge("18");
        user02.setSex("女");
        DUser user03 = new DUser();
        user03.setId(3);
        user03.setName("漳卅3");
        user03.setAge("15");
        user03.setSex("女");
        DUser user04 = new DUser();
        user04.setId(4);
        user04.setName("漳卅4");
        user04.setAge("18");
        user04.setSex("男");
        list.add(user01);
        list.add(user02);
        list.add(user03);
        list.add(user04);
        //写入到web的响应流中
        util.webOutputExcel(list,response,Math.random()+".xlsx");
        try {
            //写入到本地硬盘(内部自动关流)
            util.exportExcel(list,"学生信息sheet",new FileOutputStream("D:\\1.xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
