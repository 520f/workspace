package com.jm.service.impl;

import com.jm.mapper.DUserMapper;
import com.jm.pojo.DUser;
import com.jm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User:flowers
 * Date 2018/3/1
 * Time 21:28
 */
@Service
@Component()
public class UserServiceImpl implements UserService {
    @Autowired
    private DUserMapper dUserMapper;
    @Override
    public List<DUser> getUserList() {
        List<DUser> list = dUserMapper.getList();
        return list;
    }

    @Override
    public List<DUser> getUserList2() {
        //DataSourceHolder.setDataSourceType("ds2");
        List<DUser> list = dUserMapper.getList();
        return list;
    }
}
