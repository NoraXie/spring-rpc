package tech.xpercent.springrpc.common;

import lombok.Data;

/**
 * Created by macxie on 8/4/17.
 */
@Data
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
