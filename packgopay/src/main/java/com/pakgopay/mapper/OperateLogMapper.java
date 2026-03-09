package com.pakgopay.mapper;

import com.pakgopay.data.reqeust.systemConfig.OperateLogQueryRequest;
import com.pakgopay.mapper.dto.OperateLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OperateLogMapper {

    int insert(OperateLogDto dto);

    Integer countByQuery(OperateLogQueryRequest request);

    List<OperateLogDto> pageByQuery(OperateLogQueryRequest request);
}
