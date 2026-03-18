package com.pakgopay.data.reqeust.bankCode;

import com.pakgopay.data.reqeust.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentBankCodeQueryRequest extends BaseRequest {

    @NotNull(message = "paymentId is null")
    private Long paymentId;

    @NotBlank(message = "currencyCode is blank")
    private String currencyCode;
}
