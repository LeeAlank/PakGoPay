package com.pakgopay.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public final class RateLimitConfigParserUtil {

    private RateLimitConfigParserUtil() {
    }

    public static boolean parseEnabled(Object value, boolean fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.intValue() != 0;
        }
        String text = String.valueOf(value).trim();
        if (!StringUtils.hasText(text)) {
            return fallback;
        }
        return "1".equals(text) || "true".equalsIgnoreCase(text);
    }

    public static long parseLong(Object value, long fallback) {
        if (value == null) {
            return fallback;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        String text = String.valueOf(value).trim();
        if (!StringUtils.hasText(text)) {
            return fallback;
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public static Map<String, Long> parseFixedIpQps(String raw) {
        if (!StringUtils.hasText(raw)) {
            return Map.of();
        }
        Map<String, Long> result = new HashMap<>();
        String normalized = raw.replace('，', ',');
        String[] entries = normalized.split("[,\\n;]");
        for (String entry : entries) {
            String item = entry.trim();
            if (item.isEmpty()) {
                continue;
            }
            String[] parts = item.split("/");
            if (parts.length != 2) {
                parts = item.split(":");
            }
            if (parts.length != 2) {
                continue;
            }
            String ip = parts[0].trim();
            String qps = parts[1].trim();
            if (!StringUtils.hasText(ip) || !StringUtils.hasText(qps)) {
                continue;
            }
            try {
                long limit = Long.parseLong(qps);
                if (limit > 0) {
                    result.put(ip, limit);
                }
            } catch (NumberFormatException ignored) {
                // skip invalid value
            }
        }
        return result;
    }
}
