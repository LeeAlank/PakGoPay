package com.pakgopay.mapper;

import com.pakgopay.data.reqeust.systemConfig.LoginLogQueryRequest;
import com.pakgopay.mapper.dto.LoginLogDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LoginLogMapper {

    /** Insert */
    int insert(LoginLogDto dto);

    /** Find by primary key */
    Optional<LoginLogDto> findById(@Param("id") Long id, @Param("userId") String userId);

    /** List by user id */
    List<LoginLogDto> listByUserId(@Param("userId") String userId);

    /** Count by token jti and event type */
    Integer countByTokenJtiAndEventType(@Param("tokenJti") String tokenJti,
                                        @Param("eventType") Integer eventType);

    /** Count by query condition */
    Integer countByQuery(LoginLogQueryRequest request);

    /** Page by query condition */
    List<LoginLogDto> pageByQuery(LoginLogQueryRequest request);
}
