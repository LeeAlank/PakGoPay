package com.pakgopay.data.reqeust.agent;

import com.pakgopay.data.reqeust.ExportBaseRequest;
import lombok.Data;

@Data
public class AgentQueryRequest extends ExportBaseRequest {

    /**
     * Agent name
     */
    private String agentName;

    /**
     * account name
     */
    private String accountName;

    /**
     * level
     */
    private Boolean isSearchFirstLevel = false;

    /**
     * level
     */
    private Boolean isSearchNextLevel = false;

    /**
     * status
     */
    private Integer status;
}
