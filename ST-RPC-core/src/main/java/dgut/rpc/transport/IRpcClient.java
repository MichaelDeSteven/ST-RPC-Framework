package dgut.rpc.transport;

import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;

import java.io.IOException;

/**
 * @description: IRpcClient
 * @author: Steven
 * @time: 2021/3/4 20:40
 */
public interface IRpcClient<T> {

    T sendRequest(RpcRequest request);
}
