package dgut.rpc;

import dgut.rpc.annotation.ServiceScan;
import dgut.rpc.transport.IRpcServer;
import dgut.rpc.transport.socket.server.SocketRpcServerImpl;

/**
 * @description: dgut.rpc.SocketRpcTest
 * @author: Steven
 * @time: 2021/3/5 0:20
 */
@ServiceScan
public class SocketRpcTest {
    public static void main(String[] args) throws Exception {
        IRpcServer server = new SocketRpcServerImpl(9999);
        server.start();
    }
}
