package dgut.rpc;

import dgut.rpc.proxy.RpcRequestProxy;
import dgut.rpc.transport.netty.client.NettyRpcClientImpl;
import dgut.rpc.util.RpcServicePropertyUtil;

/**
 * @description: NettyRpcTest
 * @author: Steven
 * @time: 2021/3/12 15:15
 */
public class NettyRpcTest {
    public static void main(String[] args) {
        RpcServicePropertyUtil rpcServicePropertyUtil = RpcServicePropertyUtil.builder()
                .group("dgut")
                .version("v1.0")
                .build();
        RpcRequestProxy rpcRequestProxy = new RpcRequestProxy(new NettyRpcClientImpl(),
                rpcServicePropertyUtil);
        IHelloService iHelloService = rpcRequestProxy.getProxy(IHelloService.class);
        System.out.println(iHelloService.hello("rpc"));
    }
}
