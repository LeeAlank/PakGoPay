package com.pakgopay.service.common;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakgopay.common.constant.CommonConstant;
import com.pakgopay.common.enums.OperateInterfaceEnum;
import com.pakgopay.mapper.secondary.OperateLogMapper;
import com.pakgopay.mapper.dto.OperateLogDto;
import com.pakgopay.util.SensitiveDataMaskUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
public class OperateLogService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Set<String> SENSITIVE_KEYWORDS = Set.of(
            "password", "pwd", "secret", "key", "token", "sign",
            "googlecode", "code", "apikey", "signkey", "loginsecret", "apisecret"
    );

    @Autowired
    private OperateLogMapper operateLogMapper;

    public void write(OperateInterfaceEnum operateInterface, String operatorIdentity, Object requestBody) {
        if (operateInterface == null) {
            log.warn("operate log skip, operateInterface is null");
            return;
        }
        if (!StringUtils.hasText(operatorIdentity)) {
            log.warn("operate log skip, operatorIdentity is empty, interface={}", operateInterface.getMessage());
            return;
        }
        long now = Instant.now().getEpochSecond();
        OperateLogDto dto = new OperateLogDto();
        dto.setOperateType(operateInterface.getOperateType());
        dto.setOperateName(operateInterface.getMessage());
        dto.setOperateParams(buildSafeOperateParams(requestBody));
        String operatorUserName = resolveOperatorUserName();
        dto.setOperateUserName(StringUtils.hasText(operatorUserName) ? operatorUserName : operatorIdentity);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        try {
            int affected = operateLogMapper.insert(dto);
            log.info("operate log inserted, affected={}, dto={}", affected, dto);
        } catch (Exception e) {
            log.error("operate log insert failed, interface={}, operatorIdentity={}, message={}",
                    operateInterface.getMessage(), operatorIdentity, e.getMessage());
        }
    }

    private String buildSafeOperateParams(Object requestBody) {
        if (requestBody == null) {
            return null;
        }
        try {
            // Use Jackson first so Java records are serialized as normal JSON objects.
            String requestJson = OBJECT_MAPPER.writeValueAsString(requestBody);
            Object jsonObject = JSON.parse(requestJson);
            Object sanitized = SensitiveDataMaskUtil.sanitizePayload(jsonObject, SENSITIVE_KEYWORDS);
            return JSON.toJSONString(sanitized);
        } catch (Exception e) {
            log.warn("operate log params sanitize failed, message={}", e.getMessage());
            return "{\"_masked\":\"sanitize_failed\"}";
        }
    }

    private String resolveOperatorUserName() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Object userName = request.getAttribute(CommonConstant.ATTR_USER_NAME);
        if (userName == null) {
            return null;
        }
        String value = String.valueOf(userName).trim();
        return StringUtils.hasText(value) ? value : null;
    }
}
