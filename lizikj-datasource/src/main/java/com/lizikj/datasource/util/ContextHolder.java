package com.lizikj.datasource.util;


import com.lizikj.datasource.enums.DataSourceTypeEnum;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class ContextHolder {
    /**
     * 读写类型
     */
    private static final ThreadLocal<String> dataSourceType = new ThreadLocal<>();
    /**
     * 日志请求流水
     */
    private static ThreadLocal<String> localRequestId = new ThreadLocal<>();

    public ContextHolder() {
    }

    public static String getRequestId() {
        return localRequestId.get();
    }

    public static void setRequestId(String requestId) {
        localRequestId.set(requestId);
    }

    private static void removeRequestId() {
        localRequestId.remove();
    }


    public static ThreadLocal<String> getLocalJdbcType() {
        return dataSourceType;
    }

    /**
     * 写只有一个库
     */
    public static void writeDataSourceType() {
        dataSourceType.set(DataSourceTypeEnum.write.getType());
    }

    /**
     * 读负载均衡从库
     */
    public static void readDataSourceType() {
        dataSourceType.set(DataSourceTypeEnum.read.getType());
    }

    /**
     * 耗时读操作从库
     */
    public static void readTimeConsumDataSourceType() {
        dataSourceType.set(DataSourceTypeEnum.time_consum_read.getType());
    }


    public static String geteDataSourceType() {
        return dataSourceType.get();
    }

    private static void removeDataSourceType() {
        dataSourceType.remove();
    }

    public static void removeAll() {
        removeRequestId();
        removeDataSourceType();
    }

}