package dgut.rpc.handler;

import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: UnprocessRequestHandler
 * @author: Steven
 * @time: 2021/3/22 23:02
 */
public class UnprocessedRequestsHandler {


    private static class UnprocessedRequestsHandlerHolder {
        private static UnprocessedRequestsHandler instance = new UnprocessedRequestsHandler();
    }

    private UnprocessedRequestsHandler() {

    }

    public static UnprocessedRequestsHandler getInstance() {
        return UnprocessedRequestsHandlerHolder.instance;
    }

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>>
            unprocessedRequestsMap = new ConcurrentHashMap<>();

    public void put(RpcRequest request, CompletableFuture<RpcResponse> completableFuture) {
        unprocessedRequestsMap.put(request.getRequestId(), completableFuture);
    }

    public void remove(String requestId) {
        unprocessedRequestsMap.remove(requestId);
    }

    public void complete(RpcResponse response) {
        CompletableFuture<RpcResponse> completableFuture =
                unprocessedRequestsMap.get(response.getRequestId());
        completableFuture.complete(response);
    }

}
