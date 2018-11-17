package com.jm.controller;


import com.jm.pojo.DUser;
import com.jm.service.UserService;
import com.jm.utils.ExcelUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * User:flowers
 * Date 2018/3/1
 * Time 21:25
 */
@Controller
public class UserController {

    private Logger logger = Logger.getLogger(UserController.class);
    @Autowired
    private UserService userServiceImpl;

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
        String filePath="C:\\学生信息.xlsx";
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
     *  测试url：http://localhost:8080/testImportExcel
     *     导出测试
     * @param response
     */
    @RequestMapping("/testExceptExcel")
    private void testExceptExcel(HttpServletResponse response){
        // 创建excel工具类
        ExcelUtil<DUser> util = new ExcelUtil<>(DUser.class);
        //从数据库获取数据
        List list = userServiceImpl.getUserList();
        //写入到web的响应流中
        util.webOutputExcel(list,response,Math.random()+".xlsx");
        try {
            //写入到本地硬盘(内部自动关流)
            util.exportExcel(list,"学生信息sheet",new FileOutputStream("C:\\1.xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
