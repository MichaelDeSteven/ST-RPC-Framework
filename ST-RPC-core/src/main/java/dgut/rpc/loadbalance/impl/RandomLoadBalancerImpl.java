package dgut.rpc.loadbalance.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * @description: RandomLoadBalancerImpl
 * @author: Steven
 * @time: 2021/3/10 16:29
 */
public class RandomLoadBalancerImpl extends AbstractLoadBalance {

    @Override
    protected Instance doSelect(List<Instance> instances) {
        return instances.get(new Random().nextInt(instances.size()));
    }
}
