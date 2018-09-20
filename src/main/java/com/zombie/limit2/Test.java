package com.zombie.limit2;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableAutoConfiguration
public class Test {

    @RequestMapping("/api/test")
    @LxRateLimit(perSecond = 100.0, timeOut = 500)
    public String test() {
        return "helloword";
    }


}
