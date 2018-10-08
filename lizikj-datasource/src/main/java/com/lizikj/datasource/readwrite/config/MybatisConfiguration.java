package com.lizikj.datasource.readwrite.config;

import com.github.pagehelper.PageHelper;
import com.lizikj.common.util.SpringContextUtil;
import com.lizikj.datasource.enums.DataSourceTypeEnum;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Created by Michael.Huang on 2017/4/1.
 * 读写分离配置
 */
@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@AutoConfigureAfter({DataSourceConfiguration.class})
@DependsOn({"writeDataSource", "readDataSources"})
public class MybatisConfiguration {
    private static final Logger log = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Bean(name = "sqlSessionFactory")
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactorys() throws Exception {
        log.debug("-------------------- 重载父类 sqlSessionFactory init ---------------------");
//        SqlSessionFactory sessionFactory = super.sqlSessionFactory(roundRobinDataSouceProxy());
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(roundRobinDataSouceProxy());
        log.debug("-------------------- sqlSessionFactoryBean.setDataSource(roundRobinDataSouceProxy()); ---------------------");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:/mybatis/*.xml"));

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties props = new Properties();
        //禁用合理化时，如果pageNum<1或pageNum>pages会返回空数据
        props.setProperty("reasonable", "false");
        props.setProperty("supportMethodsArguments", "true");
        props.setProperty("returnPageInfo", "check");
        props.setProperty("params", "count=countSql");
        pageHelper.setProperties(props);
        //添加插件  
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageHelper});

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "transactionManager")
    @Order(2)
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager() {
        //boolean a =SpringContextUtil.getBean("roundRobinDataSouceProxy")==roundRobinDataSouceProxy();
        return new DataSourceTransactionManager(roundRobinDataSouceProxy());
    }

    /**
     * 有多少个数据源就要配置多少个bean
     *
     * @return
     */
    @Bean(name = "roundRobinDataSouceProxy")
    public AbstractRoutingDataSource roundRobinDataSouceProxy() {
        MyAbstractRoutingDataSource proxy = new MyAbstractRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource writeDataSource = SpringContextUtil.getBean("writeDataSource");
        // 写
        targetDataSources.put(DataSourceTypeEnum.write.getType(), writeDataSource);
        Map<String, DataSource> readsMap = SpringContextUtil.getBean("readDataSources");
        for (Map.Entry<String, DataSource> entry : readsMap.entrySet()) {
            targetDataSources.put(entry.getKey(), entry.getValue());
        }
        proxy.setDefaultTargetDataSource(writeDataSource);
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }

}