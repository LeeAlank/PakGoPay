package com.pakgopay.mapper.secondary;

import com.pakgopay.data.reqeust.systemConfig.LoginLogQueryRequest;
import com.pakgopay.mapper.dto.LoginLogDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface LoginLogMapper {

    int insert(LoginLogDto dto);

    Optional<LoginLogDto> findById(@Param("id") Long id, @Param("userId") String userId);

    List<LoginLogDto> listByUserId(@Param("userId") String userId);

    Integer countByTokenJtiAndEventType(@Param("tokenJti") String tokenJti,
                                        @Param("eventType") Integer eventType);

    Integer countByQuery(LoginLogQueryRequest request);

    List<LoginLogDto> pageByQuery(LoginLogQueryRequest request);
}
