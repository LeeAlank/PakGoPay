package com.pakgopay.data.response.bankCode;

import com.pakgopay.mapper.dto.PaymentBankCodeDto;
import lombok.Data;

import java.util.List;

@Data
public class PaymentBankCodeQueryResponse {
    private Integer totalNumber;
    private List<PaymentBankCodeDto> paymentBankCodeDtoList;
}
