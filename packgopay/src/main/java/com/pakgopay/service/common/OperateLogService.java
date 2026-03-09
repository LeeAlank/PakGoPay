package com.pakgopay.service.common;

import com.alibaba.fastjson.JSON;
import com.pakgopay.common.enums.OperateInterfaceEnum;
import com.pakgopay.mapper.OperateLogMapper;
import com.pakgopay.mapper.dto.OperateLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Slf4j
@Service
public class OperateLogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    public void write(OperateInterfaceEnum operateInterface, String operatorUserId, Object requestBody) {
        if (operateInterface == null) {
            log.warn("operate log skip, operateInterface is null");
            return;
        }
        if (!StringUtils.hasText(operatorUserId)) {
            log.warn("operate log skip, operatorUserId is empty, interface={}", operateInterface.getMessage());
            return;
        }
        long now = Instant.now().getEpochSecond();
        OperateLogDto dto = new OperateLogDto();
        dto.setOperateType(operateInterface.getOperateType());
        dto.setOperateName(operateInterface.getMessage());
        dto.setOperateParams(requestBody == null ? null : JSON.toJSONString(requestBody));
        dto.setOperateUserId(operatorUserId);
        dto.setCreateTime(now);
        dto.setUpdateTime(now);
        try {
            int affected = operateLogMapper.insert(dto);
            log.info("operate log inserted, interface={}, operatorUserId={}, affected={}",
                    operateInterface.getMessage(), operatorUserId, affected);
        } catch (Exception e) {
            log.error("operate log insert failed, interface={}, operatorUserId={}, message={}",
                    operateInterface.getMessage(), operatorUserId, e.getMessage());
        }
    }
}
