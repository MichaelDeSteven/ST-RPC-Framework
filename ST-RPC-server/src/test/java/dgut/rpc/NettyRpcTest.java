package dgut.rpc;

import dgut.rpc.annotation.ServiceScan;
import dgut.rpc.transport.IRpcServer;
import dgut.rpc.transport.netty.server.NettyRpcServerImpl;

/**
 * @description: NettyRpcTest
 * @author: Steven
 * @time: 2021/3/12 15:10
 */
@ServiceScan
public class NettyRpcTest {
    public static void main(String[] args) throws Exception {
        IRpcServer server = new NettyRpcServerImpl(9999);
        server.start();
    }
}
