package com.lizikj.datasource.readwrite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration("DataSourceConfiguration")
@AutoConfigureOrder(1)
@DependsOn("springContextUtil")
public class DataSourceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfiguration.class);
    /**
     * 读数据源名称list
     */
    public static List<String> readDataSourceList = new ArrayList<>();
    /**
     * 读数据源map
     */
    Map<String, DataSource> readDataSourceMap = new HashMap<>();
    @Value("${datasource.type}")
    private Class<? extends DataSource> dataSourceType;
    private ConversionService conversionService = new DefaultConversionService();
    private PropertyValues dataSourcePropertyValues;

    @Bean(name = "writeDataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource.write")
    public DataSource writeDataSource(@Qualifier("wallFilter") WallFilter wfilter) {
        log.debug("-------------------- writeDataSource init ---------------------");
        //变更：加入wallFilter，保证批量update语句能通过  liaojw 2017/06/27
    	DruidDataSource dataSource = (DruidDataSource) DataSourceBuilder.create().type(dataSourceType).build();
    	List<Filter> filters = new ArrayList<>();
    	filters.add(wfilter);
    	dataSource.setProxyFilters(filters);
        log.debug("-------------------- writeDataSource init end ---------------------");
        return dataSource;
    }

    /**
     * 读库数据源初始化，有多少个从库就要配置多少个
     *
     * @return
     */
    @Bean(name = "readDataSources")
    public Map<String, DataSource> readDataSources(Environment env) {
        log.debug("-------------------- readDataSources init ---------------------");
        initCustomDataSources(env);
        log.debug("-------------------- readDataSources init end---------------------");
        return readDataSourceMap;
    }

    /**
     * 初始化更多数据源
     */
    private void initCustomDataSources(Environment env) {
        // 读取配置文件获取更多数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "datasource.reads.");
        String readNames = propertyResolver.getProperty("names");
        for (String readName : readNames.split(",")) {// 多个数据源
            Map<String, Object> dsMap = propertyResolver.getSubProperties(readName + ".");
            DataSource ds = buildDataSource(dsMap);
            if (ds != null) {
                dataBinder(ds, env);
                readDataSourceMap.put(readName, ds);
                readDataSourceList.add(readName);
            }

        }

    }

    /**
     * 初始化DataSource
     */
    public DataSource buildDataSource(Map<String, Object> dsMap) {
        try {
            String driverClassName = dsMap.get("driver-class-name").toString();
            String url = dsMap.get("url").toString();
            String username = dsMap.get("username").toString();
            String password = dsMap.get("password").toString();
            DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url).username(username).password(password).type(dataSourceType);
            return factory.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 为DataSource绑定更多数据
     *
     * @param dataSource
     * @param env
     */

    private void dataBinder(DataSource dataSource, Environment env) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);//false
        dataBinder.setIgnoreInvalidFields(false);//false
        dataBinder.setIgnoreUnknownFields(true);//true
        if (dataSourcePropertyValues == null) {
            Map<String, Object> rpr = new RelaxedPropertyResolver(env, "datasource.reads").getSubProperties(".");
            Map<String, Object> values = new HashMap<>(rpr);
            // 排除已经设置的属性
            values.remove("type");
            values.remove("driverClassName");
            values.remove("url");
            values.remove("username");
            values.remove("password");
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);

    }
    
    /**
     * druid wll设置
     * @author liaojw 2017/06/27
     * @return
     */
    @Primary
    @Bean(name = "wallConfig")
    public WallConfig  wallFilterConfig(){
	    WallConfig wc = new WallConfig ();
	    wc.setMultiStatementAllow(true);
    	return wc;
    }
    
    /**
     * druid wll设置
     * @author liaojw 2017/06/27
     * @return
     */
    @Primary
    @Bean(name = "wallFilter")
    @DependsOn("wallConfig")
    WallFilter wallFilter(@Qualifier("wallConfig")WallConfig wallConfig){
       WallFilter wfilter = new WallFilter ();
       wfilter.setConfig(wallConfig);
       return wfilter;
    }
}