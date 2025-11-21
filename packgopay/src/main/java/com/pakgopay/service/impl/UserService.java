package com.pakgopay.service.impl;

import com.pakgopay.entity.User;
import com.pakgopay.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User selectAllUser(){
        return userMapper.selectAllUser();
    }
}
