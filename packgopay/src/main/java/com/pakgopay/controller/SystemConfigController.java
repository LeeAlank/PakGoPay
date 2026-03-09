package com.pakgopay.controller;

import com.pakgopay.common.constant.CommonConstant;
import com.pakgopay.common.enums.OperateInterfaceEnum;
import com.pakgopay.common.enums.ResultCode;
import com.pakgopay.data.reqeust.CreateUserRequest;
import com.pakgopay.data.reqeust.roleManagement.AddRoleRequest;
import com.pakgopay.data.reqeust.roleManagement.DeleteRoleRequest;
import com.pakgopay.data.reqeust.roleManagement.ModifyRoleRequest;
import com.pakgopay.data.reqeust.systemConfig.LoginLogQueryRequest;
import com.pakgopay.data.reqeust.systemConfig.LoginUserRequest;
import com.pakgopay.data.reqeust.systemConfig.OperateLogQueryRequest;
import com.pakgopay.data.reqeust.systemConfig.RateLimitConfigRequest;
import com.pakgopay.data.reqeust.systemConfig.TelegramConfigRequest;
import com.pakgopay.data.response.CommonResponse;
import com.pakgopay.service.common.TelegramService;
import com.pakgopay.service.common.RateLimitConfigService;
import com.pakgopay.service.common.OperateLogService;
import com.pakgopay.service.SystemConfigService;
import com.pakgopay.service.impl.UserService;
import com.pakgopay.thirdUtil.GoogleUtil;
import com.pakgopay.thirdUtil.RedisUtil;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pakGoPay/server/SystemConfig")
public class SystemConfigController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private RateLimitConfigService rateLimitConfigService;

    @Autowired
    private OperateLogService operateLogService;

    @PostMapping("/createUser")
    public CommonResponse createLoginUser(@RequestBody CreateUserRequest createUserRequest){
        CommonResponse response = userService.createLoginUser(createUserRequest);
        operateLogService.write(OperateInterfaceEnum.CREATE_USER, createUserRequest.getOperatorId(), createUserRequest);
        return response;
    }


    @GetMapping("/roleList")
    public CommonResponse roleList(@RequestParam(required = false) String roleName){
        return systemConfigService.listRoles(roleName);
    }

    @PostMapping("/loginUserList")
    public CommonResponse loginUserList(@RequestBody(required = false) LoginUserRequest loginUserRequest) {
        return systemConfigService.listLoginUsers(loginUserRequest);
    }

    @PostMapping("/loginLogList")
    public CommonResponse loginLogList(@RequestBody(required = false) @Valid LoginLogQueryRequest loginLogQueryRequest) {
        return systemConfigService.listLoginLogs(loginLogQueryRequest);
    }

    @PostMapping("/operateLogList")
    public CommonResponse operateLogList(@RequestBody(required = false) @Valid OperateLogQueryRequest operateLogQueryRequest) {
        return systemConfigService.listOperateLogs(operateLogQueryRequest);
    }

    @GetMapping("/manageLoginUserStatus")
    public CommonResponse manageLoginUserStatus(
            String userId,
            Integer status,
            @RequestAttribute(CommonConstant.ATTR_USER_ID) String operatorId){
        CommonResponse response = systemConfigService.updateLoginUserStatus(userId, status, operatorId);
        operateLogService.write(OperateInterfaceEnum.MANAGE_LOGIN_USER_STATUS, operatorId,
                new ManageLoginUserStatusPayload(userId, status));
        return response;
    }

    @GetMapping("/deleteLoginUser")
    public CommonResponse deleteLoginUser(
            String userId,
            @RequestAttribute(CommonConstant.ATTR_USER_ID) String operatorId){
        CommonResponse response = systemConfigService.deleteLoginUser(userId, operatorId);
        operateLogService.write(OperateInterfaceEnum.DELETE_LOGIN_USER, operatorId,
                new DeleteLoginUserPayload(userId));
        return response;
    }

    @GetMapping("/loginUserByLoginName")
    public CommonResponse loginUserByLoginName(String loginName){
        return systemConfigService.fetchLoginUserByLoginName(loginName);
    }

    @PostMapping("/addRole")
    public CommonResponse addRoleInfo(@RequestBody AddRoleRequest addRoleRequest, HttpServletRequest request){
        CommonResponse response = systemConfigService.createRole(addRoleRequest, request);
        operateLogService.write(OperateInterfaceEnum.ADD_ROLE, resolveOperatorUserIdFromRequest(request), addRoleRequest);
        return response;
    }

    @PostMapping("/modifyRoleInfo")
    public CommonResponse modifyRoleInfo(@RequestBody ModifyRoleRequest modifyRoleRequest, HttpServletRequest request){
        CommonResponse response = systemConfigService.updateRole(modifyRoleRequest, request);
        operateLogService.write(OperateInterfaceEnum.MODIFY_ROLE, resolveOperatorUserIdFromRequest(request), modifyRoleRequest);
        return response;
    }

    @PostMapping("/deleteRole")
    public CommonResponse deleteRole(@RequestBody DeleteRoleRequest deleteRoleRequest, HttpServletRequest request){
        CommonResponse response = systemConfigService.deleteRole(deleteRoleRequest, request);
        String operatorUserId = deleteRoleRequest.getUserId() == null
                ? resolveOperatorUserIdFromRequest(request)
                : deleteRoleRequest.getUserId();
        operateLogService.write(OperateInterfaceEnum.DELETE_ROLE, operatorUserId, deleteRoleRequest);
        return response;
    }

    @GetMapping("/getRoleInfoByRoleId")
    public CommonResponse getRoleInfoOfMenu(Integer roleId) {
        return systemConfigService.fetchRoleMenuByRoleId(roleId);
    }

    @GetMapping("/resetGoogleKey")
    public CommonResponse resetGoogleKey(HttpServletRequest request, @Param("userId") String userId, @Param("googleCode") Integer googleCode, @Param("loginName") String loginName){
        // 管理员重置令牌，校验谷歌验证码
        String userInfo = GoogleUtil.getUserInfoFromToken(request);
        String operator = userInfo.split("&")[0];
        CommonResponse response = systemConfigService.resetGoogleKey(operator, userId, loginName);
        operateLogService.write(OperateInterfaceEnum.RESET_GOOGLE_KEY, operator,
                new ResetGoogleKeyPayload(userId, loginName));
        return response;
    }

    @GetMapping("/bindGoogleKey")
    public CommonResponse bindGoogleKey(HttpServletRequest request, @Param("userId") String userId, @Param("loginName") String loginName){
        String userInfo = GoogleUtil.getUserInfoFromToken(request);
        String operator = userInfo.split("&")[0];
        CommonResponse response = systemConfigService.resetGoogleKey(operator, userId, loginName);
        operateLogService.write(OperateInterfaceEnum.BIND_GOOGLE_KEY, operator,
                new BindGoogleKeyPayload(userId, loginName));
        return response;
    }

    @GetMapping("/unCommonMessage")
    public CommonResponse getUncommonMessage(HttpServletRequest request, @Param("userName") String userName){
        String noKeyTimeInfo = redisUtil.getValue(CommonConstant.USER_NO_KEY_LOGIN_TIMES+userName);
        if (noKeyTimeInfo != null) {
            return CommonResponse.success(Integer.parseInt(noKeyTimeInfo));
        } else {
            return CommonResponse.success("success");
        }
    }

    @GetMapping("/telegramConfig")
    public CommonResponse getTelegramConfig() {
        return CommonResponse.success(telegramService.getTelegramConfig());
    }

    @PostMapping("/telegramConfig")
    public CommonResponse updateTelegramConfig(@RequestBody TelegramConfigRequest request, HttpServletRequest httpServletRequest) {
        telegramService.updateTelegramConfig(
            request.getToken(),
            request.getChatId(),
            request.getWebhookSecret(),
            request.getAllowedUserIds(),
            request.getEnabled()
        );
        operateLogService.write(OperateInterfaceEnum.UPDATE_TELEGRAM_CONFIG,
                resolveOperatorUserIdFromRequest(httpServletRequest), request);
        return CommonResponse.success("ok");
    }

    @GetMapping("/rateLimitConfig")
    public CommonResponse getRateLimitConfig() {
        return CommonResponse.success(rateLimitConfigService.getConfigAsMap());
    }

    @PostMapping("/rateLimitConfig")
    public CommonResponse updateRateLimitConfig(@RequestBody RateLimitConfigRequest request, HttpServletRequest httpServletRequest) {
        if (Boolean.TRUE.equals(request.getEnabled())) {
            Long windowSeconds = request.getWindowSeconds();
            Long maxRequests = request.getMaxRequests();
            if (windowSeconds == null || windowSeconds <= 0 || maxRequests == null || maxRequests <= 0) {
                return CommonResponse.fail(ResultCode.INVALID_PARAMS, "windowSeconds/maxRequests must be greater than 0");
            }
        }
        rateLimitConfigService.updateConfig(
            request.getEnabled(),
            request.getWindowSeconds(),
            request.getMaxRequests(),
            request.getFixedIpQps()
        );
        operateLogService.write(OperateInterfaceEnum.UPDATE_RATE_LIMIT_CONFIG,
                resolveOperatorUserIdFromRequest(httpServletRequest), request);
        return CommonResponse.success("ok");
    }

    private String resolveOperatorUserIdFromRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userId = request.getAttribute(CommonConstant.ATTR_USER_ID);
        return userId == null ? null : String.valueOf(userId);
    }

    private record ManageLoginUserStatusPayload(String userId, Integer status) {}
    private record DeleteLoginUserPayload(String userId) {}
    private record ResetGoogleKeyPayload(String userId, String loginName) {}
    private record BindGoogleKeyPayload(String userId, String loginName) {}
}
