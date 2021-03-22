package dgut.rpc.loadbalance.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;

/**
 * @description: LRULoadBalancerImpl
 * @author: Steven
 * @time: 2021/3/21 17:56
 */
public class LRULoadBalancerImpl extends AbstractLoadBalance {

    @Override
    protected Instance doSelect(List<Instance> instances) {
        return null;
    }
}
