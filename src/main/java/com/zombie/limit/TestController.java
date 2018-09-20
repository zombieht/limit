package com.zombie.limit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableAutoConfiguration
public class TestController {

    @RequestMapping("/test")
    @RateLimit
    public String test() {
        return "helloword";
    }


}
