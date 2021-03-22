package dgut.rpc.proxy;

import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.coder.ICommonEncoder;
import dgut.rpc.coder.netty.NettyCommonDecoder;
import dgut.rpc.coder.socket.RpcDecoderImpl;
import dgut.rpc.coder.socket.RpcEncoderImpl;
import dgut.rpc.handler.NettyClientHandler;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.transport.AbstractRpcClient;
import dgut.rpc.transport.netty.client.NettyRpcClientImpl;
import dgut.rpc.transport.socket.client.SocketRpcClientImpl;
import dgut.rpc.util.RpcServicePropertyUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @description: RpcRequestProxy
 * @author: Steven
 * @time: 2021/3/4 23:23
 */
public class RpcRequestProxy implements InvocationHandler {

    private AbstractRpcClient client;

    private RpcServicePropertyUtil rpcServicePropertyUtil;

    public RpcRequestProxy(AbstractRpcClient client) {
        this(client, RpcServicePropertyUtil.builder().build());
    }

    public RpcRequestProxy(AbstractRpcClient client, RpcServicePropertyUtil rpcServicePropertyUtil) {
        this.client = client;
        this.rpcServicePropertyUtil = rpcServicePropertyUtil;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .parameters(args)
                .group(rpcServicePropertyUtil.getGroup())
                .version(rpcServicePropertyUtil.getVersion())
                .build();
        RpcResponse response = null;
        if (client instanceof SocketRpcClientImpl) {
            response = (RpcResponse) client.sendRequest(request);
        } else if (client instanceof NettyRpcClientImpl) {
            CompletableFuture<RpcResponse> completableFuture =
                    (CompletableFuture<RpcResponse>) client.sendRequest(request);
            response = completableFuture.get();
        }
        return response.getResult();
    }
}
