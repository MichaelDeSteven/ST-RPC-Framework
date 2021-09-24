package dgut.rpc;

import dgut.rpc.proxy.RpcRequestProxy;
import dgut.rpc.serializer.protobuf.ProtobufSerializerImpl;
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
        RpcRequestProxy rpcRequestProxy = new RpcRequestProxy(new SocketRpcClientImpl(), rpcServicePropertyUtil);
        IHelloService iHelloService = rpcRequestProxy.getProxy(IHelloService.class);
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 500; i++) sb.append("rpc");
        System.out.println(iHelloService.hello(sb.toString()));
    }
}
