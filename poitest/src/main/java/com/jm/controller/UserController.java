package com.jm.controller;


import com.jm.pojo.DUser;
import com.jm.service.UserService;
import com.jm.utils.ExcelUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
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
    @RequestMapping("/list")
    public Object list(HttpServletResponse  response){
        List list= userServiceImpl.getUserList();
        System.out.println(list);
        testExceptExcel(list,response);
        //相对路径
        String filePath="学生信息.xls";
        testInputExcel(filePath);
        return list;
    }

    /**
     *         导入测试
     * @param filePath  文件路径（相对路径，绝对路径都可以）
     */
    private void testInputExcel(String filePath) {
        // 创建excel工具类
        ExcelUtil<DUser> util = new ExcelUtil<>(DUser.class);
        List<DUser> userList = util.importExcel(filePath);
        //输出表格中数据
        System.out.println("userList:"+userList);
    }

    /**
     *     导出测试
     * @param list
     * @param response
     */
    private void testExceptExcel(List list, HttpServletResponse response){
        // 创建excel工具类
        ExcelUtil<DUser> util = new ExcelUtil<>(DUser.class);
        util.webOutputExcel(list,"学生信息",response,Math.random()+".xlsx");
    }
}
