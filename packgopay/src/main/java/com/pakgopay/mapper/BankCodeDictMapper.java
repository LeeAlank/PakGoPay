package com.pakgopay.mapper;

import com.pakgopay.data.reqeust.bankCode.BankCodeQueryRequest;
import com.pakgopay.data.reqeust.bankCode.PaymentBankCodeQueryRequest;
import com.pakgopay.mapper.dto.BankCodeDictDto;
import com.pakgopay.mapper.dto.PaymentBankCodeDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BankCodeDictMapper {

    int batchInsert(@Param("list") List<BankCodeDictDto> list);

    int batchUpdateById(@Param("list") List<BankCodeDictDto> list);

    int deleteById(@Param("id") Long id);

    List<BankCodeDictDto> listByIds(@Param("ids") List<Long> ids);

    List<BankCodeDictDto> listByCurrencyCode(@Param("currencyCode") String currencyCode);

    List<BankCodeDictDto> listByCurrencyCountry(@Param("currencyCode") String currencyCode,
                                                @Param("country") String country);

    List<BankCodeDictDto> listByQuery(BankCodeQueryRequest request);

    Integer countByQuery(BankCodeQueryRequest request);

    List<BankCodeDictDto> pageByQuery(BankCodeQueryRequest request);

    List<PaymentBankCodeDto> listByPaymentCurrency(PaymentBankCodeQueryRequest request);
}
