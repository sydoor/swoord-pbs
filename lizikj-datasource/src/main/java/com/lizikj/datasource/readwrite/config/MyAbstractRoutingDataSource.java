package com.lizikj.datasource.readwrite.config;

import com.lizikj.datasource.enums.DataSourceTypeEnum;
import com.lizikj.datasource.util.ContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class MyAbstractRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger logger = LoggerFactory.getLogger(MyAbstractRoutingDataSource.class);

    private AtomicInteger count = new AtomicInteger(0);

    public MyAbstractRoutingDataSource() {

    }

    @Override
    protected Object determineCurrentLookupKey() {
        String typeKey = ContextHolder.geteDataSourceType();
        if (typeKey == null) {
            //如果typeKey==null,说明当前调用的方法没有设置在apo拦截器中,默认使用写库
            logger.debug("typeKey is null, current Service can't set a point cut");
            typeKey = DataSourceTypeEnum.write.getType();
        }
        if (typeKey.equals(DataSourceTypeEnum.write.getType())) {
            String dsType = DataSourceTypeEnum.write.getType();
            logger.debug("db type is {}", dsType);
            return dsType;
        }
        int readSize = DataSourceConfiguration.readDataSourceList.size();
        if (readSize == 1) {
            logger.debug("db type is {}", DataSourceConfiguration.readDataSourceList.get(0));
            return DataSourceConfiguration.readDataSourceList.get(0);
        }
        //耗时读操作返回读数据源列表最后一个
        if (typeKey.equals(DataSourceTypeEnum.time_consum_read.getType())) {
            logger.debug("db type is {}", DataSourceConfiguration.readDataSourceList.get(readSize - 1));
            return DataSourceConfiguration.readDataSourceList.get(readSize - 1);
        }
        // 普通读 第1个到倒数第2个读数据源简单负载均衡
        int number = count.getAndAdd(1);
        int lookupKey = number % (readSize - 1);
        String dbKey = DataSourceConfiguration.readDataSourceList.get(lookupKey);
        logger.debug("db key is {}", dbKey);
        return dbKey;
    }
}