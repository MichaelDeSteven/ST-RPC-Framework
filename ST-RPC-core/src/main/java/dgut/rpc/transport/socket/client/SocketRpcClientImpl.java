package dgut.rpc.transport.socket.client;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.coder.ICommonEncoder;
import dgut.rpc.coder.impl.RpcDecoderImpl;
import dgut.rpc.coder.impl.RpcEncoderImpl;
import dgut.rpc.factory.SingletonFactory;
import dgut.rpc.loadbalance.ILoadBalancer;
import dgut.rpc.loadbalance.impl.RandomLoadBalancerImpl;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.registry.IServiceDiscovery;
import dgut.rpc.registry.impl.NacosServiceDiscoveryImpl;
import dgut.rpc.serializer.ISerializer;
import dgut.rpc.transport.AbstractRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @description: SocketRpcClientImpl
 * @author: Steven
 * @time: 2021/3/4 23:35
 */
public class SocketRpcClientImpl extends AbstractRpcClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketRpcClientImpl.class);

    private int serializerCode;

    private ILoadBalancer loadBalancer;

    public SocketRpcClientImpl() {
        this(0, new RandomLoadBalancerImpl());
    }

    public SocketRpcClientImpl(int serializerCode, ILoadBalancer loadBalancer) {
        this.serializerCode = serializerCode;
        this.loadBalancer = loadBalancer;
    }

    public SocketRpcClientImpl(ISerializer serializer) {
        this(0, new RandomLoadBalancerImpl());
    }

    public SocketRpcClientImpl(ILoadBalancer loadBalancer) {
        this(0, loadBalancer);
    }

    @Override
    public RpcResponse sendRequest(RpcRequest request) {
        IServiceDiscovery discovery =
                SingletonFactory.getInstance(NacosServiceDiscoveryImpl.class);
        InetSocketAddress inetSocketAddress =
                loadBalancer.selectAddr(discovery.lookupService(request.getInterfaceName()));
        OutputStream os = null;
        InputStream is = null;
        ICommonEncoder encoder = new RpcEncoderImpl((byte)serializerCode);
        ICommonDecoder decoder = new RpcDecoderImpl();
        try {
            Socket s = new Socket(inetSocketAddress.getAddress(),
                    inetSocketAddress.getPort());
            os = s.getOutputStream();
            is = s.getInputStream();
            os.write(encoder.encode(request));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return (RpcResponse) (decoder).decode(is, RpcResponse.class);
        } catch (Exception e) {
            logger.error("解码时发生错误:", e);
            throw new RuntimeException("解码时发生错误");
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
