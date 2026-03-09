package com.pakgopay.data.response.systemConfig;

import com.pakgopay.mapper.dto.LoginLogDto;
import lombok.Data;

import java.util.List;

@Data
public class LoginLogQueryResponse {
    private List<LoginLogDto> loginLogs;
    private Integer totalNumber;
    private Integer pageNo;
    private Integer pageSize;
}
