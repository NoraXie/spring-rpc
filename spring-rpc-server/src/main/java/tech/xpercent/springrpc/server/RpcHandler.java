package tech.xpercent.springrpc.server;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xpercent.springrpc.common.RpcRequest;
import tech.xpercent.springrpc.common.RpcResponse;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by macxie on 8/4/17.
 */
@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private Map handlerMap;

    public RpcHandler() {
    }

    public RpcHandler(Map handlerMap) {
        this.handlerMap = handlerMap;
    }


    protected void messageReceived(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        try {
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (Throwable throwable) {
            response.setError(throwable);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 根据request来处理具体的业务调用
     * @param request
     * @return
     * @throws Throwable
     */
    public Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Class<?> forName = Class.forName(className);
        Method method = forName.getMethod(methodName, parameterTypes);
        return method.invoke(method, parameters);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception, ", cause);
    }
}
