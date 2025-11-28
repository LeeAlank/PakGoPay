package com.pakgopay.common.response;

import lombok.Data;

@Data
public class BaseResponse {
    private Integer code;
    private String message;
}
