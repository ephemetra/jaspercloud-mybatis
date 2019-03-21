package com.jaspercloud.mybatis.autoconfigure;

import com.jaspercloud.mybatis.properties.JasperCloudDaoProperties;
import com.jaspercloud.mybatis.support.ddl.DdlExecuter;
import com.jaspercloud.mybatis.support.ddl.DdlMigrateScanner;
import com.jaspercloud.mybatis.support.plus.JasperMybatisConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * Created by TimoRD on 2017/9/7.
 */
@EnableConfigurationProperties(JasperCloudDaoProperties.class)
@Import(JasperCloudMybatisBeanDefinitionRegistry.class)
@EnableTransactionManagement
@Configuration
public class JasperCloudMybatisAutoConfiguration {

    @Bean
    public DdlMigrateScanner ddlMigrateScanner() {
        return new DdlMigrateScanner();
    }

    @Bean
    public DdlExecuter ddlExecuter() {
        return new DdlExecuter();
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisConfigurationFactory mybatisConfigurationFactory() {
        return new MybatisConfigurationFactory() {
            @Override
            public org.apache.ibatis.session.Configuration create() {
                return new JasperMybatisConfiguration();
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public MybatisConfigurationCustomizer defaultMybatisConfigurationCustomizer() {
        return new MybatisConfigurationCustomizer() {
            @Override
            public void customize(String name, org.apache.ibatis.session.Configuration configuration) {
                configuration.setMapUnderscoreToCamelCase(true);
            }
        };
    }
}
