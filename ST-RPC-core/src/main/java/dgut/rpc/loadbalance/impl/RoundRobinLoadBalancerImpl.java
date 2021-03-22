package dgut.rpc.loadbalance.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * @description: RoundRobinLoadBalancerImpl
 * @author: Steven
 * @time: 2021/3/20 16:51
 */
public class RoundRobinLoadBalancerImpl extends AbstractLoadBalance {

    private int index;

    @Override
    protected Instance doSelect(List<Instance> instances) {
        if (index >= instances.size()) {
            index %= instances.size();
        }
        return instances.get(index++);
    }
}
