package io.choerodon.wiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;


@SpringBootApplication
@EnableEurekaClient
@EnableChoerodonResourceServer
@EnableFeignClients("io.choerodon")
public class WikiServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(WikiServiceApplication.class, args);
    }

}

