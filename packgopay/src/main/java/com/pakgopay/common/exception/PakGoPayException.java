package com.pakgopay.common.exception;

import com.pakgopay.common.enums.ResultCode;

public class PakGoPayException extends Exception{

    private Integer code;
    private String message;

    public PakGoPayException(Integer code,String message) {
        super(message);
        this.code = code;
    }

    public PakGoPayException(ResultCode resultCode){
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }
}
