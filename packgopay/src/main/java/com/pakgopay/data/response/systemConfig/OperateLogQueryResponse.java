package com.pakgopay.data.response.systemConfig;

import com.pakgopay.mapper.dto.OperateLogDto;
import lombok.Data;

import java.util.List;

@Data
public class OperateLogQueryResponse {
    private List<OperateLogDto> operateLogs;
    private Integer totalNumber;
    private Integer pageNo;
    private Integer pageSize;
}
