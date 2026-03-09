package com.pakgopay.service.common;

import com.pakgopay.common.constant.CommonConstant;
import com.pakgopay.mapper.UserMapper;
import com.pakgopay.mapper.dto.UserDTO;
import com.pakgopay.thirdUtil.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * User status resolver with Redis-first cache strategy.
 * Used by auth/filter flows to block disabled users quickly.
 */
@Slf4j
@Service
public class UserStatusService {

    private static final int USER_STATUS_CACHE_SECONDS = 7200;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserMapper userMapper;

    /**
     * Resolve whether user is enabled.
     * Priority: Redis cache -> DB fallback -> cache refresh.
     *
     * @param userId user id
     * @return true if enabled, false otherwise
     */
    public boolean isUserEnabled(String userId) {
        if (!StringUtils.hasText(userId)) {
            log.warn("isUserEnabled skip, userId is blank");
            return false;
        }
        String key = buildUserStatusKey(userId);
        String cached = redisUtil.getValue(key);
        if (StringUtils.hasText(cached)) {
            boolean enabled = String.valueOf(CommonConstant.ENABLE_STATUS_ENABLE).equals(cached);
            log.info("user status cache hit, userId={}, status={}, enabled={}", userId, cached, enabled);
            return enabled;
        }
        log.info("user status cache miss, fallback db, userId={}", userId);
        UserDTO user = userMapper.getOneUserByUserId(userId);
        if (user == null || user.getStatus() == null) {
            log.warn("user status not found, userId={}", userId);
            return false;
        }
        cacheStatus(userId, user.getStatus());
        log.info("user status loaded from db, userId={}, status={}", userId, user.getStatus());
        return CommonConstant.ENABLE_STATUS_ENABLE.equals(user.getStatus());
    }

    /**
     * Cache user status in Redis.
     *
     * @param userId user id
     * @param status status value
     */
    public void cacheStatus(String userId, Integer status) {
        if (!StringUtils.hasText(userId) || status == null) {
            log.warn("cacheStatus skip, invalid param, userId={}, status={}", userId, status);
            return;
        }
        redisUtil.setWithSecondExpire(buildUserStatusKey(userId), String.valueOf(status), USER_STATUS_CACHE_SECONDS);
        log.info("user status cached, userId={}, status={}, ttlSeconds={}", userId, status, USER_STATUS_CACHE_SECONDS);
    }

    /**
     * Apply status update and handle session cleanup when user is disabled.
     *
     * @param userId user id
     * @param status status value
     */
    public void applyStatusUpdate(String userId, Integer status) {
        if (!StringUtils.hasText(userId) || status == null) {
            log.warn("applyStatusUpdate skip, invalid param, userId={}, status={}", userId, status);
            return;
        }
        cacheStatus(userId, status);
        if (CommonConstant.ENABLE_STATUS_DISABLE.equals(status)) {
            redisUtil.remove(CommonConstant.USER_INFO_KEY_PREFIX + userId);
            redisUtil.remove(CommonConstant.REFRESH_TOKEN_START_TIME_PREFIX + userId);
            log.info("user disabled, login session cache cleared, userId={}", userId);
        } else {
            log.info("user enabled status applied, userId={}, status={}", userId, status);
        }
    }

    private String buildUserStatusKey(String userId) {
        return CommonConstant.USER_STATUS_KEY_PREFIX + ":" + userId;
    }
}
