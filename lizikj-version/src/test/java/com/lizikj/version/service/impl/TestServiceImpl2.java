package com.lizikj.version.service.impl;

import com.lizikj.version.annotation.VAdaptee;
import com.lizikj.version.dto.TestDTO;
import com.lizikj.version.service.TestService;
import org.springframework.stereotype.Service;

/**
 * @author Michael.Huang
 * @date 2017/6/16
 */
@VAdaptee("2.0")
@Service(value = "TestServiceImpl2")
public class TestServiceImpl2 implements TestService {
    @Override
    public void doSomething(TestDTO dto) {
        System.out.println("TestServiceImpl2");
    }
}
