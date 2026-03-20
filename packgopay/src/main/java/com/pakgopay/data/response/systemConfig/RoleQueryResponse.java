package com.pakgopay.data.response.systemConfig;

import com.pakgopay.data.response.RoleInfoResponse;
import lombok.Data;

import java.util.List;

@Data
public class RoleQueryResponse {
    private List<RoleInfoResponse> roles;
    private Integer totalNumber;
    private Integer pageNo;
    private Integer pageSize;
}
