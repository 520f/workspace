package com.jm.service.impl;

import com.jm.mapper.DUserMapper;
import com.jm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User:flowers
 * Date 2018/3/1
 * Time 21:28
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private DUserMapper dUserMapper;
    @Override
    public List getUserList() {
        return dUserMapper.getUserList();
    }
}
