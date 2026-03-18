package com.pakgopay.data.reqeust.systemConfig;

import com.pakgopay.data.reqeust.BaseRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SystemSyncRequest extends BaseRequest {

    @NotNull(message = "syncType is null")
    @Min(value = 1, message = "syncType must be 1 or 2")
    @Max(value = 2, message = "syncType must be 1 or 2")
    private Integer syncType;
}
