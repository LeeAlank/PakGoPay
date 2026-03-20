package com.pakgopay.mapper.secondary;

import com.pakgopay.data.reqeust.systemConfig.OperateLogQueryRequest;
import com.pakgopay.mapper.dto.OperateLogDto;

import java.util.List;

public interface OperateLogMapper {

    int insert(OperateLogDto dto);

    Integer countByQuery(OperateLogQueryRequest request);

    List<OperateLogDto> pageByQuery(OperateLogQueryRequest request);
}
