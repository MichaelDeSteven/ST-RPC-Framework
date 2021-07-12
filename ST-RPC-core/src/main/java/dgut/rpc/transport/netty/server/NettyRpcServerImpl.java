package dgut.rpc.transport.netty.server;

import dgut.rpc.coder.impl.RpcDecoderImpl;
import dgut.rpc.coder.impl.RpcEncoderImpl;
import dgut.rpc.handler.NettyServerHandler;
import dgut.rpc.handler.RpcRequestHandler;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.transport.AbstractRpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @description: NettyRpcServerImpl
 * @author: Steven
 * @time: 2021/3/12 14:01
 */
public class NettyRpcServerImpl extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcServerImpl.class);

    private RpcRequestHandler handler;

    public NettyRpcServerImpl(int port) {
        super(port);
        handler = new RpcRequestHandler(serviceProvider);
    }

    @Override
    public void start() {
        logger.info("服务器启动中...");
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new RpcDecoderImpl(RpcRequest.class))
                                    .addLast(new IdleStateHandler(5L, 0, 0, TimeUnit.SECONDS))
                                    .addLast(new RpcEncoderImpl())
                                    .addLast(new NettyServerHandler(handler));

                        }
                    });
            ChannelFuture channelFuture = b.bind(port).sync();
            logger.info("服务器启动成功");
            scanServices();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("服务器启动失败");
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
