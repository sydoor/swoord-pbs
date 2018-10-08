package com.lizikj.version.service.impl;

import com.lizikj.version.annotation.VAdapter;
import com.lizikj.version.dto.TestDTO;
import com.lizikj.version.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @author Michael.Huang
 * @date 2017/6/16
 */
@VAdapter
@Service(value = "TestServiceImpl")
public class TestServiceImpl implements TestService {
    @Override
    public void doSomething(TestDTO dto) {
        System.out.println("TestServiceImpl");
    }
}
