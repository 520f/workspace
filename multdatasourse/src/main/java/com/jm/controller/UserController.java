package com.jm.controller;


import com.jm.pojo.DUser;
import com.jm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * User:flowers
 * Date 2018/3/1
 * Time 21:25
 */
@RestController
public class UserController {

    @Autowired
    private UserService userServiceImpl;
    @RequestMapping("/list")
    public Object list() {
        List<DUser> list= userServiceImpl.getUserList();
        List<DUser> list2= userServiceImpl.getUserList2();
        List<DUser> objects = new ArrayList<>();
        objects.add(list.get(0));
        objects.add(list2.get(0));
        System.out.println("list:"+list);
        System.out.println("list2:"+list2);
        return objects;
    }
}
