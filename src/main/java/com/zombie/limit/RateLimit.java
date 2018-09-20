package com.zombie.limit;

import java.lang.annotation.*;

/**
 * @author alan.huang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    /**
     * @return rate limit in queries per second
     */
    String value() default "";

}