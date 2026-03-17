package com.pakgopay.common.enums;

import java.util.Locale;

public enum SystemConfigGroupEnum {
    TELEGRAM("telegram", OperateInterfaceEnum.UPDATE_TELEGRAM_CONFIG),
    RATELIMIT("ratelimit", OperateInterfaceEnum.UPDATE_RATE_LIMIT_CONFIG),
    COLLECTION("collection", OperateInterfaceEnum.UPDATE_COLLECTION_CONFIG),
    PAYOUT("payout", OperateInterfaceEnum.UPDATE_PAYOUT_CONFIG);

    private final String group;
    private final OperateInterfaceEnum updateOperate;

    SystemConfigGroupEnum(String group, OperateInterfaceEnum updateOperate) {
        this.group = group;
        this.updateOperate = updateOperate;
    }

    public String getGroup() {
        return group;
    }

    public OperateInterfaceEnum getUpdateOperate() {
        return updateOperate;
    }

    public static SystemConfigGroupEnum fromGroup(String group) {
        if (group == null || group.isBlank()) {
            throw new IllegalArgumentException("group is empty");
        }
        String normalized = group.trim().toLowerCase(Locale.ROOT);
        for (SystemConfigGroupEnum item : values()) {
            if (item.group.equals(normalized)) {
                return item;
            }
        }
        throw new IllegalArgumentException("unsupported group: " + group);
    }
}
