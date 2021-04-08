package dgut.rpc.transport.netty.client;

import dgut.rpc.coder.impl.RpcDecoderImpl;
import dgut.rpc.coder.impl.RpcEncoderImpl;
import dgut.rpc.handler.NettyClientHandler;
import dgut.rpc.protocol.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @description: ChannelProvider
 * @author: Steven
 * @time: 2021/3/20 14:32
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static Bootstrap bootstrap;

    private static EventLoopGroup eventLoopGroup;

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    static {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class);
    }

    public static Channel get(InetSocketAddress inetSocketAddress,
                               int serializerCode) throws InterruptedException {
        String key = null;
        if (channels.containsKey(key = inetSocketAddress.toString())) {
            Channel channel = channels.get(inetSocketAddress.toString());
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(inetSocketAddress.toString());
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new IdleStateHandler(0L, 5L, 0, TimeUnit.SECONDS))
                        .addLast(new RpcDecoderImpl(RpcResponse.class))
                        .addLast(new RpcEncoderImpl(serializerCode))
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(inetSocketAddress);
        } catch (ExecutionException e) {
            logger.info("客户端连接失败");
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture();

        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()) {
                completableFuture.complete(future.channel());
                logger.info("客户端连接成功");
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

}
