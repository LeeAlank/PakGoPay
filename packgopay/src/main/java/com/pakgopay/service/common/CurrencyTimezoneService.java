package com.pakgopay.service.common;

import com.pakgopay.mapper.CurrencyTypeMapper;
import com.pakgopay.thirdUtil.RedisUtil;
import com.pakgopay.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Slf4j
@Service
public class CurrencyTimezoneService {
    private static final String CURRENCY_TIMEZONE_CACHE_PREFIX = "currency:timezone:";
    private static final int CURRENCY_TIMEZONE_CACHE_SECONDS = 86400;

    @Autowired
    private CurrencyTypeMapper currencyTypeMapper;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * Resolve zone by currency code:
     * 1) Redis cache
     * 2) existing fallback mapping in CommonUtil
     */
    public ZoneId resolveZoneIdByCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return ZoneId.systemDefault();
        }
        String currencyKey = currency.trim().toUpperCase();
        String cacheKey = buildCacheKey(currencyKey);
        String cachedTimezone = redisUtil.getValue(cacheKey);
        if (cachedTimezone != null && !cachedTimezone.isBlank()) {
            try {
                return ZoneId.of(cachedTimezone.trim());
            } catch (Exception e) {
                log.warn("invalid timezone in redis cache, currency={}, timezone={}, message={}",
                        currencyKey, cachedTimezone, e.getMessage());
                redisUtil.remove(cacheKey);
            }
        }
        ZoneId zoneId = loadZoneIdByCurrency(currencyKey);
        redisUtil.setWithSecondExpire(cacheKey, zoneId.getId(), CURRENCY_TIMEZONE_CACHE_SECONDS);
        return zoneId;
    }

    public void refreshCurrencyTimezoneCache(String currency) {
        if (currency == null || currency.isBlank()) {
            return;
        }
        String currencyKey = currency.trim().toUpperCase();
        String cacheKey = buildCacheKey(currencyKey);
        redisUtil.remove(cacheKey);
        ZoneId zoneId = loadZoneIdByCurrency(currencyKey);
        redisUtil.setWithSecondExpire(cacheKey, zoneId.getId(), CURRENCY_TIMEZONE_CACHE_SECONDS);
        log.info("currency timezone cache refreshed, currency={}, zoneId={}", currencyKey, zoneId.getId());
    }

    private ZoneId loadZoneIdByCurrency(String currency) {
        try {
            String timezone = currencyTypeMapper.getTimezoneByCurrencyType(currency);
            if (timezone != null && !timezone.isBlank()) {
                try {
                    return ZoneId.of(timezone.trim());
                } catch (Exception e) {
                    log.warn("invalid timezone configured, currency={}, timezone={}, message={}",
                            currency, timezone, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("load timezone by currency failed, currency={}, message={}", currency, e.getMessage());
        }
        return CommonUtil.resolveZoneIdByCurrency(currency);
    }

    private String buildCacheKey(String currency) {
        return CURRENCY_TIMEZONE_CACHE_PREFIX + currency;
    }
}
