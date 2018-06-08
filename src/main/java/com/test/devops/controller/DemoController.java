package com.test.devops.controller;

import com.test.devops.entity.Demo;
import com.test.devops.service.DemoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class DemoController {

    @Autowired
    DemoService demoService;

    @Permission(permissionPublic = true,level = ResourceLevel.USER)
    @ApiOperation(value = "运行的Demo")
    @RequestMapping(value = "/demos", method = RequestMethod.GET)
    public List<Demo> getVersion() {
        return demoService.queryAll();
    }
}