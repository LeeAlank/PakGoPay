package com.pakgopay.data.reqeust.systemConfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class TelegramBroadcastRequest implements Serializable {

    @NotEmpty(message = "merchantAccounts is empty")
    private List<String> merchantAccounts;

    private String title;

    @NotBlank(message = "content is empty")
    private String content;

    private Boolean pinMessage;

    @NotNull(message = "googleCode is empty")
    private Long googleCode;
}
