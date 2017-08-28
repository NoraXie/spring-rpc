package tech.xpercent.springrpcsample.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.xpercent.springrpc.server.RpcService;

/**
 * Created by macxie on 8/4/17.
 */
@Slf4j
@RpcService(BizService.class)
public class BizServiceImpl implements BizService {
    public String doStuff(String name) {
        log.debug("成功调用服务端接口实现, 业务处理结果为:{}",name);
        return name + "let's do it!";
    }
}
