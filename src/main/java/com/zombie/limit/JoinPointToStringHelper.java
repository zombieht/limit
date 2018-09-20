package com.zombie.limit;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;

/**
 * @author alan.huang
 */
public class JoinPointToStringHelper {

    //返回   类名称.方法名称
    public static String toString(JoinPoint jp, RateLimit limit) {
        if (limit != null && StringUtils.isNotEmpty(limit.value())) {
            return limit.value();
        } else {
            String methodName = jp.getSignature().getName();
            String className = jp.getTarget().getClass().getName();
            return className.concat(".").concat(methodName);
        }
    }
}