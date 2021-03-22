package dgut.rpc;

import dgut.rpc.proxy.RpcRequestProxy;
import dgut.rpc.transport.socket.client.SocketRpcClientImpl;
import dgut.rpc.util.RpcServicePropertyUtil;

/**
 * @description: SocketRpcTest
 * @author: Steven
 * @time: 2021/3/5 0:39
 */
public class SocketRpcTest {
    public static void main(String[] args) {
        RpcServicePropertyUtil rpcServicePropertyUtil = RpcServicePropertyUtil.builder()
                .group("")
                .version("")
                .build();
        RpcRequestProxy rpcRequestProxy = new RpcRequestProxy(new SocketRpcClientImpl(),
                rpcServicePropertyUtil);
        IHelloService iHelloService = rpcRequestProxy.getProxy(IHelloService.class);
        System.out.println(iHelloService.hello("rpc"));
    }
}
