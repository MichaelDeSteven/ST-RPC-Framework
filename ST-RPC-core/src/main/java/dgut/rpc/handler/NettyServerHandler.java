package dgut.rpc.handler;

import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.transport.netty.server.NettyRpcServerImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @description: NettyServerHandler
 * @author: Steven
 * @time: 2021/3/12 14:35
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerImpl.class);

    private RpcRequestHandler handler;

    public NettyServerHandler(RpcRequestHandler requestHandler) {
        this.handler = requestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcRequest rpcRequest) throws Exception {
        // 接收并处理消息
        Object response = handler.handle(rpcRequest);

        // 发送信息给客户端
        channelHandlerContext.writeAndFlush(response);
        channelHandlerContext.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("服务端发生了异常...");
        cause.printStackTrace();
        ctx.close();
    }


}
