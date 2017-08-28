package tech.xpercent.springrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import tech.xpercent.springrpc.common.RpcDecoder;
import tech.xpercent.springrpc.common.RpcEncoder;
import tech.xpercent.springrpc.common.RpcRequest;
import tech.xpercent.springrpc.common.RpcResponse;

/**
 * Created by macxie on 8/4/17.
 * 框架的RPC 客户端（用于发送 RPC 请求）
 */
@Slf4j
public class SpringRpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private RpcResponse response;
    private final Object obj = new Object();

    private String host;
    private int port;

    public SpringRpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    protected void messageReceived(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        this.response = response;
        synchronized (obj) {
            obj.notifyAll();
        }
    }

    /**
     * 链接服务端，发送消息
     *
     * @param request
     * @return
     * @throws Exception
     */
    public RpcResponse send(RpcRequest request) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(SpringRpcClient.this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            //链接服务器
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //将request对象写入outbundle处理后发出（即RpcEncoder编码器）
            future.channel().writeAndFlush(request).sync();
            // 用线程等待的方式决定是否关闭连接
            // 其意义是：先在此阻塞，等待获取到服务端的返回后，被唤醒，从而关闭网络连接
            synchronized (obj) {
                obj.wait();
            }

            if (response != null) {
                future.channel().closeFuture().sync();
            }

            return response;
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("client caught exception", cause);
        ctx.close();
    }
}
