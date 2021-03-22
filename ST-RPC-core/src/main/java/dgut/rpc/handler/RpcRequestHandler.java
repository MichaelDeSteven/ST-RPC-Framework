package dgut.rpc.handler;

import dgut.rpc.enumeration.ResponseCode;
import dgut.rpc.factory.RpcResponSingletonFactory;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.provider.IServiceProvider;
import dgut.rpc.util.RpcServicePropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @description: RpcRequestHandler
 * @author: Steven
 * @time: 2021/3/8 16:46
 */
public class RpcRequestHandler implements Handler<RpcRequest>  {
    private static IServiceProvider serviceProvider;

    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);

    public RpcRequestHandler(IServiceProvider provider) {
        serviceProvider = provider;
    }

    private RpcResponse fail(ResponseCode responseCode, String requestId) {
        return RpcResponSingletonFactory
                .getInstance()
                .fail(responseCode, requestId);
    }

    private RpcResponse success(Object result, String requestId) {
        return RpcResponSingletonFactory
                .getInstance()
                .success(result, requestId);
    }

    @Override
    public Object handle(RpcRequest request) throws Exception {
        Object service = serviceProvider.getServiceProvider(RpcServicePropertyUtil
                .builder()
                .serviceName(request.getInterfaceName())
                .group(request.getGroup())
                .version(request.getVersion())
                .build().toRpcServiceName());

        Method method = null;
        Object result = null;

        try {
            method = service.getClass().getMethod(request.getMethodName(),
                    request.getParamTypes());
            result = method.invoke(service, request.getParameters());
            logger.info("调用方调用了{}#{}方法", service.getClass().getName(),
                    request.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            logger.error("未找到指定方法");
            result = fail(ResponseCode.METHOD_NOT_FOUND, request.getRequestId());
        } catch (InvocationTargetException e) {
            logger.error("调用方法失败");
            result = fail(ResponseCode.FAIL, request.getRequestId());
        }
        return success(result, request.getRequestId());
    }
}
