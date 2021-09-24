package dgut.rpc.transport.socket.server;

import dgut.rpc.enumeration.RpcError;
import dgut.rpc.governance.CircuitBreaker;
import dgut.rpc.governance.ThreadPoolResourceManager;
import dgut.rpc.governance.impl.CircuitBreakerResourceManager;
import dgut.rpc.governance.impl.ThreadPoolResourceManagerImpl;
import dgut.rpc.threadpool.DynamicThreadPoolManager;
import dgut.rpc.handler.RpcRequestHandler;
import dgut.rpc.handler.SocketRequestThreadHandler;
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

    private CircuitBreakerResourceManager circuitBreakerResourceManager;

    private static ThreadPoolResourceManagerImpl manager;

    public SocketRpcServerImpl(int port) {
        super(port);
        manager = ThreadPoolResourceManagerImpl.getInstance();
        threadPoolExecutor = manager.getPublicThreadPoolExecutor();
        handler = new RpcRequestHandler(serviceProvider);
        this.circuitBreakerResourceManager = CircuitBreakerResourceManager.getInstance();
    }


    @Override
    public void start() {
        ServerSocket ss = null;
        logger.info("服务器启动中...");
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            logger.error(RpcError.UNKNOWN_ERROR.getMessage());
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
            logger.info("调用方：{}与本服务器：{}:{}已建立连接", socket.getInetAddress(), socket.getInetAddress(), port);

            // 服务隔离
            threadPoolExecutor.execute(new SocketRequestThreadHandler(socket, handler));
        }

    }
}
