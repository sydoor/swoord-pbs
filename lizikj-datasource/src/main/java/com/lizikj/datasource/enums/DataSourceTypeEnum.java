package com.lizikj.datasource.enums;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
public enum DataSourceTypeEnum {
    /**
     * 用于写和修改操作，默认库
     */
    write("write", "主库"),
    /**
     * 第1个读库到倒数第2个读库用于读数据时负载均衡轮询,使用：在service方法上标记@DataSourceType(type ="read")或者方法名符合 BusinessConstants.READ_METHOD 规则
     */
    read("read", "从库"),
    /**
     * 最后一个读库，不用于读数据负载均衡，用于执行耗时的读操作,使用：在service方法上标记@DataSourceType(type ="time_consum_read")
     */
    time_consum_read("time_consum_read", "耗时读操作从库");

    private String type;
    private String name;

    DataSourceTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}