package dgut.rpc.transport;

import java.io.IOException;

/**
 * @description: IRpcServer
 * @author: Steven
 * @time: 2021/3/4 20:40
 */
public interface IRpcServer {

    void start() throws Exception;

    <T> void publishService(String serviceName, T service);
}
