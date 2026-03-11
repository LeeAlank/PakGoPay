package com.pakgopay.common.enums;

import lombok.Getter;

@Getter
public enum OrderFlowStepEnum {
    MERCHANT_CREATE_REQUEST(1, "MERCHANT_CREATE_REQUEST", "Merchant Create Request"),
    CHANNEL_SELECTED(2, "CHANNEL_SELECTED", "Channel Selected"),
    AGENT_CHAIN_RESOLVED(3, "AGENT_CHAIN_RESOLVED", "Agent Chain Resolved"),
    THIRD_CREATE_REQUEST(4, "THIRD_CREATE_REQUEST", "Third Create Request"),
    THIRD_CREATE_RESPONSE(5, "THIRD_CREATE_RESPONSE", "Third Create Response"),
    MERCHANT_CREATE_RESPONSE(6, "MERCHANT_CREATE_RESPONSE", "Merchant Create Response"),
    THIRD_NOTIFY_REQUEST(7, "THIRD_NOTIFY_REQUEST", "Third Notify Request"),
    THIRD_QUERY_REQUEST(8, "THIRD_QUERY_REQUEST", "Third Query Request"),
    THIRD_QUERY_RESPONSE(9, "THIRD_QUERY_RESPONSE", "Third Query Response"),
    MERCHANT_NOTIFY_REQUEST(10, "MERCHANT_NOTIFY_REQUEST", "Merchant Notify Request"),
    MERCHANT_NOTIFY_RESPONSE(11, "MERCHANT_NOTIFY_RESPONSE", "Merchant Notify Response"),
    NOTIFY_API_RESPONSE(12, "NOTIFY_API_RESPONSE", "Notify API Response");

    private final Integer seq;
    private final String code;
    private final String name;

    OrderFlowStepEnum(Integer seq, String code, String name) {
        this.seq = seq;
        this.code = code;
        this.name = name;
    }
}
