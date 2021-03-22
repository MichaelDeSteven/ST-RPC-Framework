package dgut.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @description: RpcResponse
 * @author: Steven
 * @time: 2021/3/4 20:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 响应请求id
     */
    private String requestId;

    /**
     * 状态码
     */
    private int statusCode;

    /**
     * 额外信息，比如响应错误需要给客户返回错误信息
     */
    private String message;

    /**
     * 响应结果
     */
    private Object result;

}
