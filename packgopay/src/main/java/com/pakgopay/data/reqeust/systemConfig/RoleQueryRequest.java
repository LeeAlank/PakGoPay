package com.pakgopay.data.reqeust.systemConfig;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RoleQueryRequest {
    private String roleName;

    @Min(value = 1, message = "pageNo must be greater than 0")
    private Integer pageNo;

    @Min(value = 1, message = "pageSize must be greater than 0")
    @Max(value = 200, message = "pageSize cannot exceed 200")
    private Integer pageSize;

    public Integer getOffset() {
        return (pageNo - 1) * pageSize;
    }
}
