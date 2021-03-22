package dgut.rpc.factory;

import dgut.rpc.enumeration.ResponseCode;
import dgut.rpc.protocol.RpcResponse;

/**
 * @description: RpcResponSingletonFactory
 * @author: Steven
 * @time: 2021/3/8 17:09
 */
public class RpcResponSingletonFactory {

    private static volatile RpcResponSingletonFactory instance;

    private RpcResponSingletonFactory() {}

    public static RpcResponSingletonFactory getInstance() {
        if (instance == null) {
            synchronized (RpcResponSingletonFactory.class) {
                if (instance == null) {
                    instance = new RpcResponSingletonFactory();
                }
            }
        }
        return instance;
    }

    public RpcResponse success(Object result, String requestId) {
        RpcResponse response = RpcResponse.builder()
                .result(result)
                .requestId(requestId)
                .statusCode(ResponseCode.SUCCESS.getCode())
                .message(ResponseCode.SUCCESS.getMessage()).build();
        return response;
    }

    public RpcResponse fail(ResponseCode responseCode, String requestId) {
        RpcResponse response = RpcResponse.builder()
                .statusCode(responseCode.getCode())
                .message(responseCode.getMessage())
                .requestId(requestId).build();
        return response;
    }
}
