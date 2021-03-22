package dgut.rpc.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @description: RpcServicePropertyUtil
 * @author: Steven
 * @time: 2021/3/21 14:25
 */

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RpcServicePropertyUtil {

    private String serviceName;

    private String group;

    private String version;

    public String toRpcServiceName() {
        return this.serviceName + this.group + this.version;
    }
}
