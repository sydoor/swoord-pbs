package com.lizikj.version.annotation;

import com.lizikj.common.util.SpringContextUtil;
import com.lizikj.version.LZVersion;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Michael.Huang
 * @date 2017/5/18
 */
@Aspect
@Component
public class VersionAdapterAspect {

    @Pointcut("@within(com.lizikj.version.annotation.VAdapter)")
    public void versionAdapter() {

    }

    @Around("versionAdapter()")
    public Object aroundVersionAdapter(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method method = ms.getMethod();
        String version = null;
        Object proceed;
        do {
            Object[] args = joinPoint.getArgs();
            for (Object o : args) {
                if (o instanceof LZVersion) {
                    version = ((LZVersion) o).getVersion();
                    break;
                }
            }
            if (null == version)
                break;

            Class cls = ms.getDeclaringType();

            if (!cls.isInterface())
                break;
            if (null == SpringContextUtil.getContext()) {
                break;
            }

            Map<String, Object> map =
                    SpringContextUtil.getContext().getBeansWithAnnotation(VAdaptee.class);

            if (map != null && map.size() > 0) {
                Iterator it = map.values().iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    if (cls.isInstance(o)) {
                        if (o.getClass().isAnnotationPresent(VAdaptee.class)) {
                            VAdaptee vatt = o.getClass().getAnnotation(VAdaptee.class);
                            String v = vatt.value();
                            if (version.equals(v)) {
                                proceed = method.invoke(o, args);
                                return proceed;
                            }
                        }
                    }
                }
            }
        } while (false);
        proceed = joinPoint.proceed();
        if (proceed == null) {
            return null;
        }
        return proceed;
    }
}
