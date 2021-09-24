package dgut.rpc.loadbalance.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: RoundRobinLoadBalancerImpl
 * @author: Steven
 * @time: 2021/3/20 16:51
 */
public class RoundRobinLoadBalancerImpl extends AbstractLoadBalance {

    private AtomicInteger index;

    @Override
    protected Instance doSelect(List<Instance> instances) {
        if (index.get() >= instances.size()) {
            index.set(0);
        }
        return instances.get(index.getAndIncrement());
    }
}
