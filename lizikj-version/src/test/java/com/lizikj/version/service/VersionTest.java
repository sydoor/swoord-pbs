package com.lizikj.version.service;

import com.lizikj.version.Bootstrap;
import com.lizikj.version.dto.TestDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Michael.Huang
 * @date 2017/6/16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
public class VersionTest {

    @Autowired
    @Qualifier(value = "TestServiceImpl")
    TestService testService;

    @Test
    public void testVersion() {
        TestDTO dto = new TestDTO();
        dto.setVersion("2.0");
        testService.doSomething(dto);

        dto.setVersion("3.0");
        testService.doSomething(dto);


        dto.setVersion("4.0");
        testService.doSomething(dto);
    }
}
