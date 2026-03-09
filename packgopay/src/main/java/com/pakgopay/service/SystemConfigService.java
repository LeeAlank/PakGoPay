package com.pakgopay.service;

import com.pakgopay.data.reqeust.roleManagement.AddRoleRequest;
import com.pakgopay.data.reqeust.roleManagement.DeleteRoleRequest;
import com.pakgopay.data.reqeust.roleManagement.ModifyRoleRequest;
import com.pakgopay.data.reqeust.systemConfig.LoginLogQueryRequest;
import com.pakgopay.data.reqeust.systemConfig.OperateLogQueryRequest;
import com.pakgopay.data.reqeust.systemConfig.LoginUserRequest;
import com.pakgopay.data.response.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface SystemConfigService {

    public CommonResponse listRoles(String roleName);

    public CommonResponse listLoginUsers(LoginUserRequest loginUserRequest);

    public CommonResponse listLoginLogs(LoginLogQueryRequest loginLogQueryRequest);

    public CommonResponse listOperateLogs(OperateLogQueryRequest operateLogQueryRequest);

    public CommonResponse updateLoginUserStatus(String userId, Integer status, String operatorId);

    public CommonResponse deleteLoginUser(String userId, String operatorId);

    public CommonResponse fetchLoginUserByLoginName(String loginName);

    public CommonResponse createRole(AddRoleRequest addRoleRequest, HttpServletRequest request);

    public CommonResponse fetchRoleMenuByRoleId(Integer menuId);

    public CommonResponse updateRole(ModifyRoleRequest modifyRoleRequest, HttpServletRequest request);

    public CommonResponse deleteRole(DeleteRoleRequest deleteRoleRequest, HttpServletRequest request);

    public CommonResponse resetGoogleKey(String operator,String userId,String loginName);
}
