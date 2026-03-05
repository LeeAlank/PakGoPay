package com.pakgopay.data.entity.agent;

import lombok.Data;

import java.util.List;

@Data
public class AgentInfoEntity {

    /**
     * Agent name
     */
    private String agentName;

    /**
     * Account name
     */
    private String accountName;

    /**
     * status
     */
    private Integer status;

    /**
     * user id
     */
    private String userId;

    /**
     * top agent id
     */
    private String topAgentId;

    /**
     * max level
     */
    private Integer maxLevel;

    /**
     * exact level
     */
    private Integer level;

    /**
     * direct parent id
     */
    private String parentId;

    /**
     * visible user ids for permission scope
     */
    private List<String> allowedUserIds;

    /** Page number (start from 1) */
    private Integer pageNo;

    /** Page size */
    private Integer pageSize;

    public Integer getOffset() {
        return (pageNo - 1) * pageSize;
    }
}
