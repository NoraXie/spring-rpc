package tech.xpercent.springrpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

/**
 * Created by macxie on 8/4/17.
 */
@Slf4j
public class RpcDecoder extends MessageToByteEncoder {
    private Class<?> rpcRequest;

    public RpcDecoder() {
    }

    public RpcDecoder(Class<?> rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    protected void encode(ChannelHandlerContext ctx, Object inObject, ByteBuf out) throws Exception {
        if(rpcRequest.isInstance(inObject)){
            byte[] data = SerializationUtils.serialize(inObject);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
