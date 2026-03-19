package com.pakgopay.service.impl;

import com.pakgopay.common.enums.ResultCode;
import com.pakgopay.data.reqeust.currencyTypeManagement.CurrencyTypeRequest;
import com.pakgopay.data.response.CommonResponse;
import com.pakgopay.data.response.currencyManagement.CurrencyReponse;
import com.pakgopay.data.response.currencyManagement.CurrencySyncResponse;
import com.pakgopay.mapper.CurrencyTypeMapper;
import com.pakgopay.mapper.dto.CurrencyTypeDTO;
import com.pakgopay.mapper.dto.CurrencyTypeSyncExcelRow;
import com.pakgopay.service.CurrencyTypeManagementService;
import com.pakgopay.service.common.CurrencyTimezoneService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class CurrencyTypeManagementServiceImpl implements CurrencyTypeManagementService {
    private static final String INVALID_SYNC_FILE_MESSAGE = "sync file is invalid";

    @Autowired
    private CurrencyTypeMapper currencyTypeMapper;
    @Autowired
    private CurrencyTimezoneService currencyTimezoneService;

    @Override
    public CommonResponse listCurrencyTypes(CurrencyTypeRequest currencyTypeRequest, HttpServletRequest request) {
       try {
           log.info("start getAllCurrencyType");
           Integer totalNumber = currencyTypeMapper.getCount(currencyTypeRequest);
           if (totalNumber == null) {
               totalNumber = 0;
           }
           List<CurrencyTypeDTO> allCurrencyType = currencyTypeMapper.getAllCurrencyType(currencyTypeRequest);
           CurrencyReponse currencyReponse = new CurrencyReponse();
           currencyReponse.setTotalNumber(totalNumber);
           currencyReponse.setCurrencyTypeDTOList(allCurrencyType);
           currencyReponse.setPageNo(currencyTypeRequest.getPageNo());
           currencyReponse.setPageSize(currencyTypeRequest.getPageSize());
           log.info("end getAllCurrencyType");
           return CommonResponse.success(currencyReponse);
       } catch (Exception e) {
           log.error(e.toString());
           return CommonResponse.fail(ResultCode.FAIL,"get currency type failed");
       }
    }

    @Override
    public CommonResponse createCurrencyType(CurrencyTypeRequest currencyTypeRequest, HttpServletRequest request) {
        try {
            String timezone = normalizeTimezone(currencyTypeRequest.getTimezone());
            currencyTypeRequest.setTimezone(timezone);
            CurrencyTypeDTO currencyTypeDTO = new CurrencyTypeDTO();
            BeanUtils.copyProperties(currencyTypeRequest, currencyTypeDTO);
            insertCurrencyType(currencyTypeDTO);
            return CommonResponse.success(ResultCode.SUCCESS);
        } catch (DuplicateKeyException e) {
            return CommonResponse.fail(ResultCode.FAIL, "currency already exists");
        } catch (IllegalArgumentException e) {
            return CommonResponse.fail(ResultCode.ORDER_PARAM_VALID, e.getMessage());
        } catch (Exception e) {
            return CommonResponse.fail(ResultCode.FAIL,"add currency type failed "+ e.getMessage());
        }
    }

    @Override
    public CommonResponse updateCurrencyType(CurrencyTypeRequest currencyTypeRequest, HttpServletRequest request) {
        try {
            if (currencyTypeRequest.getId() == null) {
                return CommonResponse.fail(ResultCode.FAIL, "currency id is required");
            }
            CurrencyTypeDTO before = currencyTypeMapper.getCurrencyById(currencyTypeRequest.getId());
            String timezone = normalizeTimezone(currencyTypeRequest.getTimezone());
            String operatorName = currencyTypeRequest.getUserName();
            CurrencyTypeDTO currencyTypeDTO = new CurrencyTypeDTO();
            currencyTypeDTO.setId(currencyTypeRequest.getId());
            currencyTypeDTO.setCurrencyAccuracy(currencyTypeRequest.getCurrencyAccuracy());
            currencyTypeDTO.setTimezone(timezone);
            currencyTypeDTO.setUpdateTime(System.currentTimeMillis() / 1000);
            currencyTypeDTO.setUpdateBy(operatorName);
            Integer updateResult = currencyTypeMapper.updateCurrencyType(currencyTypeDTO);
            if (updateResult == 1) {
                if (before != null && before.getCurrencyType() != null) {
                    currencyTimezoneService.refreshCurrencyTimezoneCache(before.getCurrencyType());
                }
                if (currencyTypeDTO.getCurrencyType() != null) {
                    currencyTimezoneService.refreshCurrencyTimezoneCache(currencyTypeDTO.getCurrencyType());
                }
                return CommonResponse.success(ResultCode.SUCCESS);
            }
            return CommonResponse.fail(ResultCode.FAIL, "update currency type failed");
        } catch (DuplicateKeyException e) {
            return CommonResponse.fail(ResultCode.FAIL, "currency already exists");
        } catch (IllegalArgumentException e) {
            return CommonResponse.fail(ResultCode.ORDER_PARAM_VALID, e.getMessage());
        } catch (Exception e) {
            return CommonResponse.fail(ResultCode.FAIL, "update currency type failed " + e.getMessage());
        }
    }

    @Override
    public CommonResponse syncCurrencyTypesFromRows(
            List<CurrencyTypeSyncExcelRow> rows,
            String source,
            String userId,
            String userName) {
        try {
            List<String> invalidRows = new ArrayList<>();
            List<String> skippedCurrencies = new ArrayList<>();
            int totalRows = rows == null ? 0 : rows.size();
            long now = System.currentTimeMillis() / 1000;
            String operatorName = resolveOperatorName(userId, userName);
            // 1) Validate + normalize + deduplicate rows from source file.
            LinkedHashMap<String, CurrencyTypeDTO> dedupMap =
                    collectValidCurrencies(rows, operatorName, now, invalidRows);
            // 2) Compare with DB and split into insert list + skipped list.
            List<CurrencyTypeDTO> toInsert = resolveToInsertCurrencies(dedupMap, skippedCurrencies);
            // 3) Batch insert new currencies and refresh timezone cache.
            int insertedCount = batchInsertCurrencies(toInsert);

            CurrencySyncResponse response = new CurrencySyncResponse(
                    source,
                    totalRows,
                    insertedCount,
                    skippedCurrencies.size(),
                    invalidRows.size(),
                    skippedCurrencies,
                    invalidRows
            );
            return CommonResponse.success(response);
        } catch (IllegalArgumentException e) {
            log.error("sync currency types invalid file content", e);
            return CommonResponse.fail(ResultCode.SYNC_FILE_INVALID, INVALID_SYNC_FILE_MESSAGE);
        } catch (Exception e) {
            log.error("sync currency types failed", e);
            if (isCurrencySyncContentError(e)) {
                return CommonResponse.fail(ResultCode.SYNC_FILE_INVALID, INVALID_SYNC_FILE_MESSAGE);
            }
            return CommonResponse.fail(ResultCode.FAIL, "sync currency type failed " + e.getMessage());
        }
    }

    /**
     * Validate and normalize rows, then deduplicate by currencyType.
     */
    private LinkedHashMap<String, CurrencyTypeDTO> collectValidCurrencies(
            List<CurrencyTypeSyncExcelRow> rows,
            String operatorName,
            long now,
            List<String> invalidRows) {
        LinkedHashMap<String, CurrencyTypeDTO> dedupMap = new LinkedHashMap<>();
        if (rows == null || rows.isEmpty()) {
            return dedupMap;
        }
        for (int i = 0; i < rows.size(); i++) {
            CurrencyTypeSyncExcelRow row = rows.get(i);
            String invalidReason = validateSyncRow(row);
            if (invalidReason != null) {
                invalidRows.add("row " + (i + 2) + ": " + invalidReason);
                continue;
            }
            CurrencyTypeDTO dto = buildSyncCurrencyDto(row, operatorName, now);
            // Keep last config for duplicated currency in same file.
            dedupMap.put(dto.getCurrencyType(), dto);
        }
        return dedupMap;
    }

    /**
     * Split deduplicated rows into toInsert and skipped-existing currencies.
     */
    private List<CurrencyTypeDTO> resolveToInsertCurrencies(
            LinkedHashMap<String, CurrencyTypeDTO> dedupMap,
            List<String> skippedCurrencies) {
        List<CurrencyTypeDTO> toInsert = new ArrayList<>();
        if (dedupMap.isEmpty()) {
            return toInsert;
        }
        List<String> currencyTypes = new ArrayList<>(dedupMap.keySet());
        List<CurrencyTypeDTO> existing = currencyTypeMapper.listByCurrencyTypes(currencyTypes);
        HashSet<String> existingSet = new HashSet<>();
        for (CurrencyTypeDTO dto : existing) {
            if (dto != null && dto.getCurrencyType() != null) {
                existingSet.add(dto.getCurrencyType());
            }
        }
        for (String currencyType : currencyTypes) {
            if (existingSet.contains(currencyType)) {
                skippedCurrencies.add(currencyType);
                continue;
            }
            toInsert.add(dedupMap.get(currencyType));
        }
        return toInsert;
    }

    /**
     * Batch insert and refresh timezone cache for inserted currencies.
     */
    private int batchInsertCurrencies(List<CurrencyTypeDTO> toInsert) {
        if (toInsert == null || toInsert.isEmpty()) {
            return 0;
        }
        // Insert all new currencies in one SQL to reduce DB round-trips.
        int insertedCount = currencyTypeMapper.batchAddNewCurrency(toInsert);
        // Proactively warm timezone cache for newly inserted currencies.
        for (CurrencyTypeDTO dto : toInsert) {
            currencyTimezoneService.refreshCurrencyTimezoneCache(dto.getCurrencyType());
        }
        return insertedCount;
    }

    private CurrencyTypeDTO buildSyncCurrencyDto(CurrencyTypeSyncExcelRow row, String operatorName, long now) {
        CurrencyTypeDTO currencyTypeDTO = new CurrencyTypeDTO();
        currencyTypeDTO.setCurrencyType(normalizeCurrencyType(row.getCurrencyType()));
        currencyTypeDTO.setName(row.getName().trim());
        currencyTypeDTO.setIcon(row.getIcon().trim());
        currencyTypeDTO.setCurrencyAccuracy(parseCurrencyAccuracy(row.getCurrencyAccuracy()));
        currencyTypeDTO.setTimezone(normalizeTimezone(row.getTimezone()));
        currencyTypeDTO.setCreateBy(operatorName);
        currencyTypeDTO.setUpdateBy(operatorName);
        currencyTypeDTO.setCreateTime(now);
        currencyTypeDTO.setUpdateTime(now);
        return currencyTypeDTO;
    }

    private String normalizeTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return null;
        }
        try {
            return ZoneId.of(timezone.trim()).getId();
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid timezone: " + timezone);
        }
    }

    private void insertCurrencyType(CurrencyTypeDTO currencyTypeDTO) {
        long now = System.currentTimeMillis() / 1000;
        if (currencyTypeDTO.getCreateTime() == null) {
            currencyTypeDTO.setCreateTime(now);
        }
        if (currencyTypeDTO.getUpdateTime() == null) {
            currencyTypeDTO.setUpdateTime(now);
        }
        Integer addResult = currencyTypeMapper.addNewCurrency(currencyTypeDTO);
        if (addResult == null || addResult != 1) {
            throw new IllegalStateException("insert currency type failed");
        }
        currencyTimezoneService.refreshCurrencyTimezoneCache(currencyTypeDTO.getCurrencyType());
    }

    private String validateSyncRow(CurrencyTypeSyncExcelRow row) {
        if (row == null) {
            return "empty row";
        }
        if (row.getCurrencyType() == null || row.getCurrencyType().trim().isEmpty()) {
            return "currencyType is empty";
        }
        if (row.getName() == null || row.getName().trim().isEmpty()) {
            return "name is empty";
        }
        if (row.getIcon() == null || row.getIcon().trim().isEmpty()) {
            return "icon is empty";
        }
        if (row.getCurrencyAccuracy() == null || row.getCurrencyAccuracy().trim().isEmpty()) {
            return "currencyAccuracy is empty";
        }
        parseCurrencyAccuracy(row.getCurrencyAccuracy());
        if (row.getTimezone() == null || row.getTimezone().trim().isEmpty()) {
            return "timezone is empty";
        }
        normalizeTimezone(row.getTimezone());
        return null;
    }

    private String normalizeCurrencyType(String currencyType) {
        return currencyType == null ? null : currencyType.trim().toUpperCase(Locale.ROOT);
    }

    private BigDecimal parseCurrencyAccuracy(String currencyAccuracy) {
        try {
            return new BigDecimal(currencyAccuracy.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid currencyAccuracy: " + currencyAccuracy);
        }
    }

    private String resolveOperatorName(String userId, String userName) {
        if (userName != null && !userName.trim().isEmpty()) {
            return userName.trim();
        }
        if (userId != null && !userId.trim().isEmpty()) {
            return userId.trim();
        }
        return "system";
    }

    private boolean isCurrencySyncContentError(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String className = current.getClass().getName();
            if (current instanceof IllegalArgumentException
                    || current instanceof NumberFormatException
                    || "com.alibaba.excel.exception.ExcelDataConvertException".equals(className)
                    || "com.alibaba.excel.exception.ExcelAnalysisException".equals(className)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
