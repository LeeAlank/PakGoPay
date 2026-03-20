package com.pakgopay.common.config;

import com.pakgopay.data.reqeust.systemConfig.LoginLogQueryRequest;
import com.pakgopay.data.reqeust.systemConfig.OperateLogQueryRequest;
import com.pakgopay.mapper.dto.LoginLogDto;
import com.pakgopay.mapper.dto.OperateLogDto;
import com.pakgopay.mapper.dto.OrderFlowLogDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@ConditionalOnProperty(prefix = "pakgopay.datasource.secondary", name = "enabled", havingValue = "false", matchIfMissing = true)
public class SecondaryLogMapperFallbackConfig {

    @Bean
    public com.pakgopay.mapper.secondary.LoginLogMapper secondaryLoginLogMapperFallback(
            com.pakgopay.mapper.LoginLogMapper delegate) {
        return new com.pakgopay.mapper.secondary.LoginLogMapper() {
            @Override
            public int insert(LoginLogDto dto) {
                return delegate.insert(dto);
            }

            @Override
            public Optional<LoginLogDto> findById(Long id, String userId) {
                return delegate.findById(id, userId);
            }

            @Override
            public List<LoginLogDto> listByUserId(String userId) {
                return delegate.listByUserId(userId);
            }

            @Override
            public Integer countByTokenJtiAndEventType(String tokenJti, Integer eventType) {
                return delegate.countByTokenJtiAndEventType(tokenJti, eventType);
            }

            @Override
            public Integer countByQuery(LoginLogQueryRequest request) {
                return delegate.countByQuery(request);
            }

            @Override
            public List<LoginLogDto> pageByQuery(LoginLogQueryRequest request) {
                return delegate.pageByQuery(request);
            }
        };
    }

    @Bean
    public com.pakgopay.mapper.secondary.OperateLogMapper secondaryOperateLogMapperFallback(
            com.pakgopay.mapper.OperateLogMapper delegate) {
        return new com.pakgopay.mapper.secondary.OperateLogMapper() {
            @Override
            public int insert(OperateLogDto dto) {
                return delegate.insert(dto);
            }

            @Override
            public Integer countByQuery(OperateLogQueryRequest request) {
                return delegate.countByQuery(request);
            }

            @Override
            public List<OperateLogDto> pageByQuery(OperateLogQueryRequest request) {
                return delegate.pageByQuery(request);
            }
        };
    }

    @Bean
    public com.pakgopay.mapper.secondary.CollectionOrderFlowLogMapper secondaryCollectionOrderFlowLogMapperFallback(
            com.pakgopay.mapper.CollectionOrderFlowLogMapper delegate) {
        return new com.pakgopay.mapper.secondary.CollectionOrderFlowLogMapper() {
            @Override
            public int insert(OrderFlowLogDto dto) {
                return delegate.insert(dto);
            }

            @Override
            public int insertBatch(List<OrderFlowLogDto> list) {
                return delegate.insertBatch(list);
            }

            @Override
            public List<OrderFlowLogDto> listByTransactionNo(String transactionNo, Long startTime, Long endTime) {
                return delegate.listByTransactionNo(transactionNo, startTime, endTime);
            }
        };
    }

    @Bean
    public com.pakgopay.mapper.secondary.PayOrderFlowLogMapper secondaryPayOrderFlowLogMapperFallback(
            com.pakgopay.mapper.PayOrderFlowLogMapper delegate) {
        return new com.pakgopay.mapper.secondary.PayOrderFlowLogMapper() {
            @Override
            public int insert(OrderFlowLogDto dto) {
                return delegate.insert(dto);
            }

            @Override
            public int insertBatch(List<OrderFlowLogDto> list) {
                return delegate.insertBatch(list);
            }

            @Override
            public List<OrderFlowLogDto> listByTransactionNo(String transactionNo, Long startTime, Long endTime) {
                return delegate.listByTransactionNo(transactionNo, startTime, endTime);
            }
        };
    }
}
