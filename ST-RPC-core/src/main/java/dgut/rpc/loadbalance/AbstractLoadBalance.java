package dgut.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @description: AbstractLoadBalance
 * @author: Steven
 * @time: 2021/3/21 17:36
 */
public abstract class AbstractLoadBalance implements ILoadBalancer {
    @Override
    public Instance select(List<Instance> instances) {
        if (instances == null || instances.size() == 0) {
            return null;
        }
        if (instances.size() == 1) {
            return instances.get(0);
        }
        return doSelect(instances);
    }

    protected abstract Instance doSelect(List<Instance> instances);
}
