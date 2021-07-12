package dgut.rpc.router;

import lombok.Data;

import java.net.URL;

/**
 * @description: AbstractRouter
 * @author: Steven
 * @time: 2021/7/4 22:57
 */
@Data
public abstract class AbstractRouter implements Router {

    protected String consumerIp;

    public AbstractRouter() {
    }
}
