package com.pakgopay.mapper.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Table: login_log
 * Login log DTO
 */
@Data
public class LoginLogDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Table id */
    private Long id;

    /** Login user id */
    private String userId;

    /** Login name */
    private String loginName;

    /** Login role */
    private String loginRole;

    /** Login ip */
    private String loginIp;

    /** Create time */
    private Long createTime;

    /** Update time */
    private Long updateTime;

    /** Event type: 1-login, 2-logout */
    private Integer eventType;

    /** Event time (epoch second) */
    private Long eventTime;

    /** Access token jti */
    private String tokenJti;

    /** Logout reason: manual/expired/kicked */
    private String logoutReason;
}

