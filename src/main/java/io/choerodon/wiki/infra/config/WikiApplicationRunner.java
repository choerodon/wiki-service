package io.choerodon.wiki.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.choerodon.wiki.app.service.WikiScanningService;

/**
 * Created by Zenger on 2018/11/6.
 */
@Component
@Order(value = 1)
public class WikiApplicationRunner implements ApplicationRunner {

    @Autowired
    private WikiScanningService wikiScanningService;

    @Value("${wiki.sync}")
    private Boolean sync;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if (sync) {
            wikiScanningService.scanning();
        }
    }
}
