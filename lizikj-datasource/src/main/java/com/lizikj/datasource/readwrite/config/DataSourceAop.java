package com.lizikj.datasource.readwrite.config;

import com.lizikj.datasource.annotation.DataSourceType;
import com.lizikj.datasource.constants.BusinessConstants;
import com.lizikj.datasource.enums.DataSourceTypeEnum;
import com.lizikj.datasource.util.ContextHolder;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Created by Michael.Huang on 2017/4/1.
 */
@Aspect
@Component
@Order(1)//order要比PlatformTransactionManager小，保证先执行切点，再执行determineCurrentLookupKey方法
public class DataSourceAop {

    private static final Logger log = LoggerFactory.getLogger(DataSourceAop.class);

    @Around("execution(* com.lizikj..*.service..*.*(..)))")
    public Object setDataSourceType(ProceedingJoinPoint joinPoint) throws Throwable {
        String type = ContextHolder.geteDataSourceType();//获取进入方法时，父层方法的读写标记
        try {
            setType(joinPoint);//设置方法里边的数据操作的读写标记
        } catch (Throwable t) {
        }
        Object result = joinPoint.proceed();
        setDataSourceType(type);//执行结束，设置回原父层的读写标记
        return result;
    }

    private void setDataSourceType(String type) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        if (DataSourceTypeEnum.read.getType().equals(type)) {
            read();
        } else if (DataSourceTypeEnum.time_consum_read.getType().equals(type)) {
            readTimeConsum();
        } else {
            write();
        }

    }

    private void setType(ProceedingJoinPoint joinPoint) {
        //切换读写库，如果接口的方法上有注解，以注解为准
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        DataSourceType datasource = method.getAnnotation(DataSourceType.class);
        if (datasource != null) {
            String type = datasource.type();
            setDataSourceType(type);
            return;
        }
        //若没注解，切换读写库以方法名开头为准
        String methodName = method.getName();
        if (methodName.matches(BusinessConstants.READ_METHOD)) {
            read();
        } else {
            write();
        }
    }

    public void write() {
        ContextHolder.writeDataSourceType();
        log.debug("DataSourceAop：dataSource切换到主库");
    }

    public void read() {
        ContextHolder.readDataSourceType();
        log.debug("DataSourceAop：dataSource切换到普通从库");
    }

    public void readTimeConsum() {
        ContextHolder.readTimeConsumDataSourceType();
        log.debug("DataSourceAop：dataSource切换到耗时从库");
    }


}