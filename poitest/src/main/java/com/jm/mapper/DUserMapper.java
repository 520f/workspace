package com.jm.mapper;

import com.jm.pojo.DUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DUserMapper {
    List<DUser> getUserList();
}