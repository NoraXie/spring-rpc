package tech.xpercent.springrpcsample.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tech.xpercent.springrpc.client.RpcProxy;
import tech.xpercent.springrpcsample.server.BizService;

/**
 * Created by macxie on 8/4/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
@Slf4j
public class BizServiceImpl {
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void doStuff(){
        BizService bizService = rpcProxy.create(BizService.class);
        String result = bizService.doStuff("judy");
        log.info("服务器端返回结果: {}", result);
    }
}
