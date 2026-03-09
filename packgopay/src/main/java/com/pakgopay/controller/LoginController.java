package com.pakgopay.controller;

import com.pakgopay.common.enums.OperateInterfaceEnum;
import com.pakgopay.data.reqeust.LoginRequest;
import com.pakgopay.data.response.CommonResponse;
import com.pakgopay.service.LoginService;
import com.pakgopay.service.common.OperateLogService;
import com.pakgopay.thirdUtil.GoogleUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pakGoPay/server/Login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private OperateLogService operateLogService;


    @PostMapping(value = "/login")
    public CommonResponse login(HttpServletRequest request, @RequestBody LoginRequest loginRequest){
        CommonResponse commonResponse = loginService.login(loginRequest, request);
        String operatorUserId = loginRequest.getUserId() == null ? null : String.valueOf(loginRequest.getUserId());
        operateLogService.write(OperateInterfaceEnum.LOGIN, operatorUserId, loginRequest);
        return commonResponse;
    }

    @GetMapping(value = "/logout")
    public CommonResponse logout(HttpServletRequest request){
        CommonResponse commonResponse = loginService.logout(request);
        String operatorUserId = resolveUserIdFromRequest(request);
        operateLogService.write(OperateInterfaceEnum.LOGOUT, operatorUserId, null);
        return commonResponse;
    }

    /**
     * 此接口不需要token校验
     * 用refreshToken刷新accessToken
     * @param freshToken
     * @return
     */
    @GetMapping("/refreshToken")
    public CommonResponse accessTokenRefresh(@RequestParam String freshToken, HttpServletRequest request){
        return loginService.refreshAuthToken(freshToken, request);
    }

    /**
     * 获取谷歌令牌绑定二维码
     * @param userName
     * @return
     */
    @RequestMapping(value = "/getCode")
    public CommonResponse verify(@RequestParam(value = "userName")String userName, @RequestParam(value = "password") String password){
        return loginService.generateLoginQrCode(userName, password);
    }

    private String resolveUserIdFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String userInfo = GoogleUtil.getUserInfoFromToken(request);
        if (userInfo == null || userInfo.isBlank() || !userInfo.contains("&")) {
            return null;
        }
        return userInfo.split("&")[0];
    }
}
