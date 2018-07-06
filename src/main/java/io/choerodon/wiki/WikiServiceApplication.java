package io.choerodon.wiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;


@SpringBootApplication
@EnableEurekaClient
@EnableChoerodonResourceServer
public class WikiServiceApplication {

    public static void main(String[] args){
        SpringApplication.run(WikiServiceApplication.class, args);
    }

}

