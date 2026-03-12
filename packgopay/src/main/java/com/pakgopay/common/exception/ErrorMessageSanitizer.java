package com.pakgopay.common.exception;

import com.pakgopay.common.enums.ResultCode;

import java.util.Locale;
import java.util.regex.Pattern;

public final class ErrorMessageSanitizer {

    private static final Pattern SENSITIVE_PATTERN = Pattern.compile(
            "(sql|select\\s+|insert\\s+|update\\s+|delete\\s+|drop\\s+|truncate\\s+|" +
                    "syntax error|bad sql grammar|jdbc|mybatis|mapper|constraint|duplicate key|" +
                    "org\\.|java\\.|exception|stack trace|nested exception|cause:)",
            Pattern.CASE_INSENSITIVE
    );

    private ErrorMessageSanitizer() {
    }

    public static String sanitize(ResultCode resultCode, String message) {
        String msg = message == null ? "" : message.trim();
        if (msg.isEmpty()) {
            return resultCode.getMessage();
        }
        String lower = msg.toLowerCase(Locale.ROOT);
        if (SENSITIVE_PATTERN.matcher(lower).find()) {
            if (resultCode == ResultCode.INVALID_PARAMS) {
                return ResultCode.INVALID_PARAMS.getMessage();
            }
            return ResultCode.FAIL.getMessage();
        }
        return msg;
    }
}
