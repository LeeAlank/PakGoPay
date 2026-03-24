package com.pakgopay.mapper.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PaymentAmountAggDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Payment ID */
    private Long paymentId;

    /** Current day amount sum */
    private BigDecimal dayAmount;

    /** Current month amount sum */
    private BigDecimal monthAmount;
}

