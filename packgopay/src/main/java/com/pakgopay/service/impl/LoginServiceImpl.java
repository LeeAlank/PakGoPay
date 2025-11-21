package com.pakgopay.service.impl;

import com.pakgopay.common.errorCode.ResultCode;
import com.pakgopay.common.reqeust.LoginRequest;
import com.pakgopay.common.response.CommonResponse;
import com.pakgopay.entity.User;
import com.pakgopay.mapper.UserMapper;
import com.pakgopay.service.LoginService;
import com.pakgopay.util.TokenUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    UserMapper userMapper;

    @Override
    public CommonResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User oneUser = userMapper.getOneUser(username, password);
        if (!ObjectUtils.isEmpty(oneUser)) {
            return new CommonResponse(0,"login success",TokenUtils.getToken(username)+"&&"+oneUser.getUsername());
        }
        return new CommonResponse(ResultCode.USER_LOGIN_FAIL);
    }
}
