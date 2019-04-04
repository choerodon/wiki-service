package io.choerodon.wiki;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;


@SpringBootApplication
@EnableEurekaClient
@EnableChoerodonResourceServer
@EnableFeignClients("io.choerodon")
@EnableAsync
public class WikiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikiServiceApplication.class, args);
    }

    /**
     * 自定义异步线程池
     *
     * @return
     */
    @Bean
    @Qualifier("org-pro-sync")
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("org-pro-sync");
        executor.setMaxPoolSize(3);
        executor.setCorePoolSize(2);
        return executor;
    }
}

