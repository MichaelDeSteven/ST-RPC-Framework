package dgut.rpc.handler;

import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.coder.ICommonEncoder;
import dgut.rpc.coder.impl.RpcDecoderImpl;
import dgut.rpc.coder.impl.RpcEncoderImpl;
import dgut.rpc.enumeration.ResponseCode;
import dgut.rpc.governance.ThreadPoolResourceManager;
import dgut.rpc.governance.impl.ThreadPoolResourceManagerImpl;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @description: SocketRequestThreadHandler(Socket多线程处理客户端请求)
 * @author: Steven
 * @time: 2021/3/8 16:31
 */
public class SocketRequestThreadHandler implements Runnable {

    private Socket socket;

    private RpcRequestHandler handler;

    private RpcRequest request;

    private static ThreadPoolResourceManagerImpl threadPoolResourceManager = ThreadPoolResourceManagerImpl.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(SocketRequestThreadHandler.class);

    private ICommonDecoder<RpcRequest> decoder;

    private ICommonEncoder encoder;

    public SocketRequestThreadHandler(Socket socket, RpcRequestHandler handler) {
        this.socket = socket;
        this.handler = handler;
        threadPoolResourceManager = ThreadPoolResourceManagerImpl.getInstance();
        this.decoder = new RpcDecoderImpl();
        this.encoder = new RpcEncoderImpl();
    }


    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream()) {
            this.request = this.decoder.decode(is, RpcRequest.class);
            logger.info("{} 解析后的请求 {}", Thread.currentThread().getName(), request);
            /**
             * 服务隔离策略：针对不同的请求，使用不同类型的线程池执行
             * 1. 先根据获取线程池类型标识
             * 2，不存在先获取服务名
             * 3. 不存在则获取粒度更小的方法名
             * 4. 不存在则采用兜底方案，公共线程池
             */
            ThreadPoolExecutor executor =
                    (ThreadPoolExecutor) threadPoolResourceManager.getThreadPoolByName(request.getThreadPoolTag());
            if (null == executor) {
                executor = (ThreadPoolExecutor) threadPoolResourceManager.getThreadPoolByName(request.getInterfaceName());
            }
            if (null == executor) {
                executor = (ThreadPoolExecutor)threadPoolResourceManager.getThreadPoolByName(request.getMethodName());
            }
            if (null == executor) {
                executor = threadPoolResourceManager.getPublicThreadPoolExecutor();
            }
            Future<RpcResponse> future = executor.submit(new Executor(handler, request));
            byte[] bytes = encoder.encode(future.get());
            os.write(bytes);
        } catch (Exception e) {
            logger.error("调用时发生了错误：{}", e);
        }

    }

    private class Executor implements Callable {

        private RpcRequest rpcRequest;

        private Handler handler;

        public Executor(Handler handler, RpcRequest rpcRequest) {
            this.rpcRequest = rpcRequest;
            this.handler = handler;
        }

        @Override
        public Object call() throws Exception {
            Object result = handler.handle(rpcRequest);
            logger.info("线程{} 执行请求ID={}完毕 结果为 {}", Thread.currentThread(), rpcRequest.getRequestId(), result);
            return result;
        }
    }
}
