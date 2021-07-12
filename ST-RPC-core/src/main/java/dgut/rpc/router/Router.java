package dgut.rpc.router;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description: Router
 * @author: Steven
 * @time: 2021/7/4 22:57
 */
public interface Router {

    List<Instance> route(List<Instance> instants);
}
