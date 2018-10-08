package com.lizikj.mq.demo;

import com.lizikj.mq.Bootstrap;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Bootstrap.class)
/**
 * Created by Michael.Huang on 2017/4/1.
 */
public class BaseTest {

    protected void waitConsume(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }

    }
}
