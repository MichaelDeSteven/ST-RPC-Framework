package dgut.rpc.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @description: ILoadBalancer
 * @author: Steven
 * @time: 2021/3/10 16:28
 */
public interface ILoadBalancer {

    Instance select(List<Instance> instances);

    InetSocketAddress selectAddr(List<Instance> instances);
}
