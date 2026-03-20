package com.pakgopay.mapper.dto;

import lombok.Data;

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
    private String operateUserName;
    private Long createTime;
    private Long updateTime;
}
