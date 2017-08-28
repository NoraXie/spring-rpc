package tech.xpercent.springrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by macxie on 8/4/17.
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder{
    private Class<?> rpcResponse;

    public RpcEncoder() {
    }

    public RpcEncoder(Class<?> rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    protected void encode(ChannelHandlerContext ctx, Object inObject, ByteBuf out) throws Exception {
        if(rpcResponse.isInstance(inObject)){
            byte[] data = SerializationUtil.serialize(inObject);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
