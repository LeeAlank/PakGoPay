package com.pakgopay.mapper;

import com.pakgopay.data.reqeust.currencyTypeManagement.CurrencyTypeRequest;
import com.pakgopay.mapper.dto.CurrencyTypeDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CurrencyTypeMapper {
    List<CurrencyTypeDTO> getAllCurrencyType(CurrencyTypeRequest currencyTypeRequest);

    Integer getCount( CurrencyTypeRequest entity);

    Integer addNewCurrency(CurrencyTypeDTO currencyTypeDTO);

    Integer batchAddNewCurrency(List<CurrencyTypeDTO> list);

    Integer updateCurrencyType(CurrencyTypeDTO currencyTypeDTO);

    CurrencyTypeDTO getCurrencyById(Integer id);

    String getTimezoneByCurrencyType(String currencyType);

    CurrencyTypeDTO getCurrencyByCurrencyType(String currencyType);

    List<CurrencyTypeDTO> listByCurrencyTypes(List<String> currencyTypes);
}
