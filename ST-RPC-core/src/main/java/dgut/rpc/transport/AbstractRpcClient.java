package dgut.rpc.transport;

import lombok.Data;

/**
 * @description: AbstractRpcClient
 * @author: Steven
 * @time: 2021/3/12 14:45
 */
@Data
public abstract class AbstractRpcClient implements IRpcClient {

    private String host;

    private int port;
}
