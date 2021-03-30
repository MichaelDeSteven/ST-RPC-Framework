package dgut.rpc.transport.socket.server;

import dgut.rpc.enumeration.RpcError;
import dgut.rpc.factory.ThreadPoolFactory;
import dgut.rpc.handler.RpcRequestHandler;
import dgut.rpc.handler.SocketRequestThreadHandler;
import dgut.rpc.provider.impl.ServiceProviderImpl;
import dgut.rpc.registry.impl.NacosServiceRegistryImpl;
import dgut.rpc.transport.AbstractRpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: SocketRpcServerImpl
 * @author: Steven
 * @time: 2021/3/4 20:43
 */
public class SocketRpcServerImpl extends AbstractRpcServer<ServerSocket> {

    private static final Logger logger = LoggerFactory.getLogger(SocketRpcServerImpl.class);

    private ThreadPoolExecutor threadPoolExecutor;

    private RpcRequestHandler handler;


    public SocketRpcServerImpl(int port) {
        super(port);
        threadPoolExecutor = (ThreadPoolExecutor) ThreadPoolFactory.createDefaultThreadPool();
        handler = new RpcRequestHandler(serviceProvider);
    }


    @Override
    public void start() {
        ServerSocket ss = null;
        logger.info("服务器启动中...");
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            logger.error(RpcError.UNKNOWN_ERROR.getMessage());
        } finally {
            ThreadPoolFactory.shutdown();
        }
        logger.info("服务器启动成功");

        scanServices();
        Socket socket = null;
        while (true) {
            try {
                socket = ss.accept();
            } catch (IOException e) {
                logger.error(RpcError.UNKNOWN_ERROR.getMessage());
            }
            logger.info("调用方：{}与本服务器：{}:{}已建立连接",
                    socket.getInetAddress(), socket.getInetAddress(), port);
            threadPoolExecutor
                    .execute(new SocketRequestThreadHandler(socket, handler));
        }

    }
}
