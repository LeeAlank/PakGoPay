package com.pakgopay.common.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ConditionalOnProperty(prefix = "pakgopay.datasource.secondary", name = "enabled", havingValue = "true")
public class SecondaryDataSourceConfig {

    @Bean
    @ConfigurationProperties("pakgopay.datasource.secondary")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties("pakgopay.datasource.secondary.hikari")
    public DataSource secondaryDataSource(
            @Qualifier("secondaryDataSourceProperties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(secondaryDataSource);
        factoryBean.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/secondary/**/*.xml"));
        return factoryBean.getObject();
    }

    @Bean(name = "secondarySqlSessionTemplate")
    public SqlSessionTemplate secondarySqlSessionTemplate(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        return new SqlSessionTemplate(secondarySqlSessionFactory);
    }

    @Bean(name = "secondaryTransactionManager")
    public PlatformTransactionManager secondaryTransactionManager(
            @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new DataSourceTransactionManager(secondaryDataSource);
    }

    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new JdbcTemplate(secondaryDataSource);
    }

    @Bean
    public MapperFactoryBean<com.pakgopay.mapper.secondary.LoginLogMapper> secondaryLoginLogMapper(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        MapperFactoryBean<com.pakgopay.mapper.secondary.LoginLogMapper> factoryBean =
                new MapperFactoryBean<>(com.pakgopay.mapper.secondary.LoginLogMapper.class);
        factoryBean.setSqlSessionFactory(secondarySqlSessionFactory);
        return factoryBean;
    }

    @Bean
    public MapperFactoryBean<com.pakgopay.mapper.secondary.OperateLogMapper> secondaryOperateLogMapper(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        MapperFactoryBean<com.pakgopay.mapper.secondary.OperateLogMapper> factoryBean =
                new MapperFactoryBean<>(com.pakgopay.mapper.secondary.OperateLogMapper.class);
        factoryBean.setSqlSessionFactory(secondarySqlSessionFactory);
        return factoryBean;
    }

    @Bean
    public MapperFactoryBean<com.pakgopay.mapper.secondary.CollectionOrderFlowLogMapper> secondaryCollectionOrderFlowLogMapper(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        MapperFactoryBean<com.pakgopay.mapper.secondary.CollectionOrderFlowLogMapper> factoryBean =
                new MapperFactoryBean<>(com.pakgopay.mapper.secondary.CollectionOrderFlowLogMapper.class);
        factoryBean.setSqlSessionFactory(secondarySqlSessionFactory);
        return factoryBean;
    }

    @Bean
    public MapperFactoryBean<com.pakgopay.mapper.secondary.PayOrderFlowLogMapper> secondaryPayOrderFlowLogMapper(
            @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        MapperFactoryBean<com.pakgopay.mapper.secondary.PayOrderFlowLogMapper> factoryBean =
                new MapperFactoryBean<>(com.pakgopay.mapper.secondary.PayOrderFlowLogMapper.class);
        factoryBean.setSqlSessionFactory(secondarySqlSessionFactory);
        return factoryBean;
    }
}
