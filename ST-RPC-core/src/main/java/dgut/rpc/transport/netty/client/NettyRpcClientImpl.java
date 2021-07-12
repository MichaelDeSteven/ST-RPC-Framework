package dgut.rpc.transport.netty.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.factory.SingletonFactory;
import dgut.rpc.handler.UnprocessedRequestsHandler;
import dgut.rpc.loadbalance.ILoadBalancer;
import dgut.rpc.loadbalance.impl.RandomLoadBalancerImpl;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.registry.IServiceDiscovery;
import dgut.rpc.registry.impl.NacosServiceDiscoveryImpl;
import dgut.rpc.transport.AbstractRpcClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @description: NettyRpcClientImpl
 * @author: Steven
 * @time: 2021/3/12 14:44
 */
public class NettyRpcClientImpl extends AbstractRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyRpcClientImpl.class);

    private int serializerCode;

    private ILoadBalancer loadBalancer;

    private static UnprocessedRequestsHandler unprocessedRequestsHandler
            = UnprocessedRequestsHandler.getInstance();


    public NettyRpcClientImpl(int serializerCode, ILoadBalancer loadBalancer) {
        this.serializerCode = serializerCode;
        this.loadBalancer = loadBalancer;
    }

    public NettyRpcClientImpl() {
        this(0, new RandomLoadBalancerImpl());
    }

    public NettyRpcClientImpl(int serializerCode) {
        this(serializerCode, new RandomLoadBalancerImpl());
    }

    public NettyRpcClientImpl(ILoadBalancer loadBalancer) {
        this(0, loadBalancer);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest request) {
        IServiceDiscovery discovery =
                SingletonFactory.getInstance(NacosServiceDiscoveryImpl.class);
        InetSocketAddress inetSocketAddress =
                loadBalancer.selectAddr(discovery.lookupService(request.getInterfaceName()));
        CompletableFuture<RpcResponse> completableFuture = new CompletableFuture<>();
        try {
            Channel channel = ChannelProvider.get(inetSocketAddress, serializerCode);

            unprocessedRequestsHandler.put(request, completableFuture);

            channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", request.toString()));
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    logger.error("发送消息时有错误发生: ", future.cause());
                }
            });
        } catch (InterruptedException e) {
            unprocessedRequestsHandler.remove(request.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return completableFuture;
    }



}
