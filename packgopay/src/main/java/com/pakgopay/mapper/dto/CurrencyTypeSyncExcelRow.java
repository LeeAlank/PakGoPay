package com.pakgopay.mapper.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CurrencyTypeSyncExcelRow {
    @ExcelProperty(index = 0)
    private String currencyType;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private String icon;

    @ExcelProperty(index = 3)
    private String currencyAccuracy;

    @ExcelProperty(index = 4)
    private String timezone;
}
