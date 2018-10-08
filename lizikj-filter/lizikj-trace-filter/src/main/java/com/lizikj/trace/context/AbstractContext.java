package com.lizikj.trace.context;

import lombok.Getter;
import lombok.Setter;

/**
 * 上下文基类
 * @auth zone
 * @date 2017-10-14
 */
public abstract class AbstractContext {

    @Getter
    @Setter
    private String applicationName;


}
