package com.pakgopay.mapper.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;

@Data
public class OperateLogDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer operateType;
    private String operateName;
    private String operateParams;
    @JsonIgnore
    private String operateUserId;
    private String operateUserName;
    private Long createTime;
    private Long updateTime;
}
