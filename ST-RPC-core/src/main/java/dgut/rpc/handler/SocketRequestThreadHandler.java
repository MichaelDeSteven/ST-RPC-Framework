package dgut.rpc.handler;

import dgut.rpc.coder.ICommonEncoder;
import dgut.rpc.coder.socket.RpcDecoderImpl;
import dgut.rpc.coder.socket.RpcEncoderImpl;
import dgut.rpc.protocol.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @description: SocketRequestThreadHandler(Socket多线程处理客户端请求)
 * @author: Steven
 * @time: 2021/3/8 16:31
 */
public class SocketRequestThreadHandler implements Runnable {

    private Socket socket;

    private RpcRequestHandler handler;

    private static final Logger logger =
            LoggerFactory.getLogger(SocketRequestThreadHandler.class);

    public SocketRequestThreadHandler(Socket socket, RpcRequestHandler handler) {
        this.socket = socket;
        this.handler = handler;
    }


    @Override
    public void run() {
        try (InputStream is = socket.getInputStream();
        OutputStream os = socket.getOutputStream()) {
            RpcDecoderImpl<RpcRequest, InputStream> decoder = new RpcDecoderImpl();
            RpcRequest request = decoder.decode(is, RpcRequest.class);
            Object result = handler.handle(request);

            ICommonEncoder encoder = new RpcEncoderImpl();

            byte[] bytes = encoder.encode(result);
            os.write(bytes);
        } catch (Exception e) {
            logger.error("调用时发生了错误：{}", e);
        }

    }
}
