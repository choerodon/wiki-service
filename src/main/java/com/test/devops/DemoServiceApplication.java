package com.test.devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
/*@EnableFeignClients
@EnableEurekaClient
@EnableChoerodonResourceServer*/
public class DemoServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(DemoServiceApplication.class, args);
    }

}

