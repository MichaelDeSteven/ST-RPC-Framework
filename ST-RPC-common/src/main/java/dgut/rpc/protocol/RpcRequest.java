package dgut.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: RpcRequest
 * @author: Steven
 * @time: 2021/3/4 20:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    /**
     * 请求包请求id
    */
    private String requestId;

    /**
     *调用接口名称
     */
    private String interfaceName;

    /**
     * 调用方法名称
     */
    private String methodName;

    /**
     * 调用方法的参数类型列表
     */
    private Class<?>[] paramTypes;

    /**
     * 调用方法的参数值列表
     */
    private Object[] parameters;

    /**
     * 方法所属组
     */
    private String group;

    /**
     * 方法版本
     */
    private String version;

    /**
     * 是否为心跳包
     */
    private boolean heartBeat;

    /**
     * 所属线程池组
     */
    private String threadPoolTag;

}
