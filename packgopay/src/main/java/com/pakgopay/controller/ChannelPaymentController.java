package com.pakgopay.controller;

import com.pakgopay.common.enums.OperateInterfaceEnum;
import com.pakgopay.common.enums.ResultCode;
import com.pakgopay.common.exception.PakGoPayException;
import com.pakgopay.data.reqeust.channel.*;
import com.pakgopay.data.response.CommonResponse;
import com.pakgopay.service.ChannelPaymentService;
import com.pakgopay.service.common.OperateLogService;
import com.pakgopay.util.ExportFileUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/pakGoPay/server")
public class ChannelPaymentController {

    @Autowired
    private ChannelPaymentService channelPaymentService;

    @Autowired
    private OperateLogService operateLogService;

    @PostMapping("/queryChannel")
    public CommonResponse queryChannel(@RequestBody @Valid ChannelQueryRequest channelQueryRequest, HttpServletRequest request) {
        try {
            return channelPaymentService.queryChannels(channelQueryRequest);
        } catch (PakGoPayException e) {
            log.error("queryChannel failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "queryChannel failed: " + e.getMessage());
        }
    }

    @PostMapping("/queryPayment")
    public CommonResponse queryPayment(@RequestBody @Valid PaymentQueryRequest paymentQueryRequest, HttpServletRequest request) {
        try {
            return channelPaymentService.queryPayments(paymentQueryRequest);
        } catch (PakGoPayException e) {
            log.error("queryPayment failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "queryPayment failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "exportChannel")
    public void exportChannel(
            @RequestBody @Valid ChannelQueryRequest channelQueryRequest, HttpServletResponse response) {
        try {
            channelPaymentService.exportChannels(channelQueryRequest, response);
        } catch (PakGoPayException e) {
            log.error("exportChannel failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            ExportFileUtils.writeJsonError(response, e.getCode(), "exportChannel failed: " + e.getMessage());
        } catch (IOException e) {
            log.error("exportChannel failed, IOException message: {}", e.getMessage());
            ExportFileUtils.writeJsonError(response,
                    ResultCode.FAIL, "exportChannel failed, IOException message: " + e.getMessage());
        }
    }

    @PostMapping(value = "exportPayment")
    public void exportPayment(
            @RequestBody @Valid PaymentQueryRequest paymentQueryRequest, HttpServletResponse response) {
        try {
            channelPaymentService.exportPayments(paymentQueryRequest, response);
        } catch (PakGoPayException e) {
            log.error("exportPayment failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            ExportFileUtils.writeJsonError(response, e.getCode(), "exportPayment failed: " + e.getMessage());
        } catch (IOException e) {
            log.error("exportPayment failed, IOException message: {}", e.getMessage());
            ExportFileUtils.writeJsonError(response,
                    ResultCode.FAIL, "exportPayment failed, IOException message: " + e.getMessage());
        }
    }


    @PostMapping("/editChannel")
    public CommonResponse editChannel(@RequestBody @Valid ChannelEditRequest channelEditRequest, HttpServletRequest request) {
        try {
            CommonResponse response = channelPaymentService.updateChannel(channelEditRequest);
            operateLogService.write(OperateInterfaceEnum.EDIT_CHANNEL, channelEditRequest.getUserId(), channelEditRequest);
            return response;
        } catch (PakGoPayException e) {
            log.error("editChannel failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "editChannel failed: " + e.getMessage());
        }
    }

    @PostMapping("/editPayment")
    public CommonResponse editPayment(@RequestBody @Valid PaymentEditRequest paymentEditRequest, HttpServletRequest request) {
        try {
            CommonResponse response = channelPaymentService.updatePayment(paymentEditRequest);
            operateLogService.write(OperateInterfaceEnum.EDIT_PAYMENT, paymentEditRequest.getUserId(), paymentEditRequest);
            return response;
        } catch (PakGoPayException e) {
            log.error("editPayment failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "editPayment failed: " + e.getMessage());
        }
    }

    @PostMapping("/addChannel")
    public CommonResponse addChannel(@RequestBody @Valid ChannelAddRequest channelAddRequest, HttpServletRequest request) {
        try {
            CommonResponse response = channelPaymentService.createChannel(channelAddRequest);
            operateLogService.write(OperateInterfaceEnum.ADD_CHANNEL, channelAddRequest.getUserId(), channelAddRequest);
            return response;
        } catch (PakGoPayException e) {
            log.error("addChannel failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "addChannel failed: " + e.getMessage());
        }
    }

    @PostMapping("/addPayment")
    public CommonResponse addPayment(@RequestBody @Valid PaymentAddRequest paymentAddRequest, HttpServletRequest request) {
        try {
            CommonResponse response = channelPaymentService.createPayment(paymentAddRequest);
            operateLogService.write(OperateInterfaceEnum.ADD_PAYMENT, paymentAddRequest.getUserId(), paymentAddRequest);
            return response;
        } catch (PakGoPayException e) {
            log.error("addPayment failed, code: {} message: {}", e.getErrorCode(), e.getMessage());
            return CommonResponse.fail(e.getCode(), "addPayment failed: " + e.getMessage());
        }
    }

}
