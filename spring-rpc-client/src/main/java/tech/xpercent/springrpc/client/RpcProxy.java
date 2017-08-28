package tech.xpercent.springrpc.client;

import lombok.extern.slf4j.Slf4j;
import tech.xpercent.springrpc.common.RpcRequest;
import tech.xpercent.springrpc.common.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by macxie on 8/4/17.
 * RPC 代理（用于创建 RPC 服务代理）
 */
@Slf4j
public class RpcProxy {
    private String serverIP;
    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serverIP) {
        this.serverIP = serverIP;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        if(serviceDiscovery != null){
                            serverIP = serviceDiscovery.discover();
                        }

                        String[] array = serverIP.split(":");
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);

                        SpringRpcClient client = new SpringRpcClient(host,port);
                        RpcResponse response = client.send(request);

                        if(response.getError() == null){
                            throw response.getError();
                        }else {
                            return response.getResult();
                        }

                    }
                });
    }
}
