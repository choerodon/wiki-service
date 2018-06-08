package com.test.devops.service.impl;

import com.test.devops.entity.Demo;
import com.test.devops.service.DemoService;
import com.test.devops.mapper.DemoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author crcokitwood
 */
@Service
public class DemoServiceImpl implements DemoService {
    @Autowired
    private DemoMapper mapper;
    @Override
    public List<Demo> queryAll() {
        return mapper.selectAll();
    }
}
