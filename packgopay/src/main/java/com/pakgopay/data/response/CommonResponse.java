package com.pakgopay.data.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.pakgopay.common.enums.ResultCode;
import com.pakgopay.common.exception.ErrorMessageSanitizer;
import com.pakgopay.common.exception.PakGoPayException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class CommonResponse<T> implements Serializable {
    private Integer code;
    private String message;
    private String data;

    public CommonResponse() {
    }

    public CommonResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(Integer code, String message, String data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public CommonResponse(String message, String data) {

        this.message = message;
        this.data = data;
        this.code = 0;
    }

    public CommonResponse(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public static <T> CommonResponse<T> success(String data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResponse<T> success(Object data) {
        return new CommonResponse<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage()
                , JSON.toJSONString(data, SerializerFeature.DisableCircularReferenceDetect));
    }

    public static CommonResponse<Void> fail(ResultCode resultCode) {
        String rawMessage = resultCode.getMessage();
        log.error("[ERR_SANITIZE_BEFORE] code={}, rawMessage={}", resultCode.getCode(), rawMessage);
        String sanitizedMessage = ErrorMessageSanitizer.sanitize(resultCode, rawMessage);
        log.error("[ERR_SANITIZE_AFTER] code={}, sanitizedMessage={}, replaced={}",
                resultCode.getCode(), sanitizedMessage, !sanitizedMessage.equals(rawMessage));
        return new CommonResponse<>(resultCode.getCode(), sanitizedMessage);
    }

    public static CommonResponse<Void> fail(ResultCode resultCode, String message) {
        String rawMessage = message;
        log.error("[ERR_SANITIZE_BEFORE] code={}, rawMessage={}", resultCode.getCode(), rawMessage);
        String sanitizedMessage = ErrorMessageSanitizer.sanitize(resultCode, rawMessage);
        log.error("[ERR_SANITIZE_AFTER] code={}, sanitizedMessage={}, replaced={}",
                resultCode.getCode(), sanitizedMessage, !sanitizedMessage.equals(rawMessage));
        return new CommonResponse<>(resultCode.getCode(), sanitizedMessage);
    }

    public static CommonResponse<Void> fail(PakGoPayException pe) {
        ResultCode rc = pe.getCode() == null ? ResultCode.FAIL : pe.getCode();
        String rawMessage = pe.getMessage();
        log.error("[ERR_SANITIZE_BEFORE] code={}, rawMessage={}", pe.getErrorCode(), rawMessage);
        String sanitizedMessage = ErrorMessageSanitizer.sanitize(rc, rawMessage);
        log.error("[ERR_SANITIZE_AFTER] code={}, sanitizedMessage={}, replaced={}",
                pe.getErrorCode(), sanitizedMessage, !sanitizedMessage.equals(rawMessage));
        return new CommonResponse<>(pe.getErrorCode(), sanitizedMessage);
    }
}
