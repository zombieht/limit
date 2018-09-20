package com.zombie.limit;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author alan.huang
 */
@Aspect
@Component
public class RateLimiterAspect {

    @Autowired
    private Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterAspect.class);
    private final ConcurrentHashMap<String, RateLimiter> limiters;
    private final KeyFactory keyFactory;

    @Autowired
    public RateLimiterAspect(Optional<KeyFactory> keyFactory) {
        this.limiters = new ConcurrentHashMap<>();
        this.keyFactory = (jp, limit) -> JoinPointToStringHelper.toString(jp, limit);
    }

    @Before("@annotation(limit)")
    public void rateLimit(JoinPoint jp, RateLimit limit) {
        String key = createKey(jp, limit);
        RateLimiter limiter = limiters.computeIfAbsent(key, createLimiter(jp, limit));
        if (limiter != null) {
            if (limiter.tryAcquire()) {
                throw new RuntimeException("234");
            }
        }
    }

    private Function<String, RateLimiter> createLimiter(JoinPoint jp, RateLimit limit) {
        String keyProps = limit.value();
        if (StringUtils.isEmpty(keyProps)) {
            keyProps = getKeyProps(jp);
        }
        //如果没有配置这个值 就设置为无限大
        String limitStrVal = env.getProperty(keyProps);
        if (StringUtils.isNotEmpty(limitStrVal)) {
            try {
                double limitDoubleVal = Double.valueOf(limitStrVal);
                return name -> RateLimiter.create(limitDoubleVal);
            } catch (NumberFormatException e) {
                LOGGER.error("限流属性转换异常.... key : " + keyProps);
                return name -> RateLimiter.create(Double.MAX_VALUE);
            }
        } else {
            return name -> RateLimiter.create(Double.MAX_VALUE);
        }

    }

    private String createKey(JoinPoint jp, RateLimit limit) {
        return keyFactory.createKey(jp, limit);
    }

    @FunctionalInterface
    public interface KeyFactory {
        String createKey(JoinPoint jp, RateLimit limit);
    }

    public String getKeyProps(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        String className = jp.getTarget().getClass().getName();
        return className.concat(".").concat(methodName).concat("limit");
    }
}