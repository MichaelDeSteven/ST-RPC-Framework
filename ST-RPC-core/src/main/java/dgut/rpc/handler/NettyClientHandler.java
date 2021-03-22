package dgut.rpc.handler;

import dgut.rpc.coder.socket.RpcEncoderImpl;
import dgut.rpc.protocol.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.protostuff.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: NettyClientHandler
 * @author: Steven
 * @time: 2021/3/12 14:59
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    private static UnprocessedRequestsHandler unprocessedRequestsHandler;

    public NettyClientHandler() {
        unprocessedRequestsHandler = UnprocessedRequestsHandler.getInstance();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                RpcResponse response) throws Exception {
        try {
            logger.info(String.format("客户端接收到消息: %s", response));
            unprocessedRequestsHandler.complete(response);
        } finally {
            ReferenceCountUtil.release(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("连接时发生异常");
    }
}
