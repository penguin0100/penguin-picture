package org.example.picture.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
@MapperScan("org.example.picture.mapper")
public class MyBatisPlusConfig {

    /**
     * 拦截器配置
     *
     * @return {@link MybatisPlusInterceptor}
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 手动创建 SqlSessionFactory。
     * <p>
     * Spring Boot 3.5.x 中 MybatisPlusAutoConfiguration 的
     * {@code @ConditionalOnSingleCandidate(DataSource.class)} 条件有时无法
     * 匹配到已注册的 DataSource bean，导致 SqlSessionFactory 不会被自动创建。
     * 此处显式手动创建以解决兼容性问题。
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(
            DataSource dataSource,
            MybatisPlusProperties properties,
            ObjectProvider<Interceptor[]> interceptorsProvider,
            ObjectProvider<TypeHandler[]> typeHandlersProvider,
            ObjectProvider<LanguageDriver[]> languageDriversProvider,
            ObjectProvider<DatabaseIdProvider> databaseIdProvider,
            ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) throws Exception {

        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        // 全局配置（逻辑删除等）
        GlobalConfig globalConfig = properties.getGlobalConfig();
        factory.setGlobalConfig(globalConfig);

        // MyBatis 核心配置
        MybatisConfiguration configuration = new MybatisConfiguration();
        MybatisPlusProperties.CoreConfiguration coreConfiguration = properties.getConfiguration();
        if (coreConfiguration != null) {
            coreConfiguration.applyTo(configuration);
        }

        // 应用 ConfigurationCustomizer 扩展
        List<ConfigurationCustomizer> configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        if (!CollectionUtils.isEmpty(configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : configurationCustomizers) {
                customizer.customize(configuration);
            }
        }

        factory.setConfiguration(configuration);

        // 拦截器
        Interceptor[] interceptors = interceptorsProvider.getIfAvailable();
        if (interceptors != null && interceptors.length > 0) {
            factory.setPlugins(interceptors);
        }

        // TypeHandler
        TypeHandler[] typeHandlers = typeHandlersProvider.getIfAvailable();
        if (typeHandlers != null && typeHandlers.length > 0) {
            factory.setTypeHandlers(typeHandlers);
        }

        // LanguageDriver
        LanguageDriver[] languageDrivers = languageDriversProvider.getIfAvailable();
        if (languageDrivers != null && languageDrivers.length > 0) {
            factory.setScriptingLanguageDrivers(languageDrivers);
        }

        // DatabaseIdProvider
        DatabaseIdProvider dbIdProvider = databaseIdProvider.getIfAvailable();
        if (dbIdProvider != null) {
            factory.setDatabaseIdProvider(dbIdProvider);
        }

        factory.setConfigurationProperties(properties.getConfigurationProperties());

        return factory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
