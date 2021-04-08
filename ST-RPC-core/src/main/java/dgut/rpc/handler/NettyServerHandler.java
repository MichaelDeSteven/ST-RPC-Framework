package dgut.rpc.handler;

import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.transport.netty.server.NettyRpcServerImpl;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
        if (rpcRequest.isHeartBeat()) {
            logger.info("收到来自客户端的心跳包");
            return;
        } else {
            Object response = handler.handle(rpcRequest);
            channelHandlerContext.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("服务端发生了异常...");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                logger.info("长时间未响应，中断客户端" + ctx.channel().remoteAddress() + "连接");
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
