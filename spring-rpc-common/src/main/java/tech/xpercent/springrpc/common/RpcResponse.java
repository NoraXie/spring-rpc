package tech.xpercent.springrpc.common;

import lombok.Data;

/**
 * Created by macxie on 8/4/17.
 */
@Data
public class RpcResponse {
    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError(){
        return error != null;
    }
}
