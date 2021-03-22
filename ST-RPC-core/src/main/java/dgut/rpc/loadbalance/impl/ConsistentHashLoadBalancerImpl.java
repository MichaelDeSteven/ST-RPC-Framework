package dgut.rpc.loadbalance.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.loadbalance.AbstractLoadBalance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: ConsistentHashLoadBalancerImpl
 * @author: Steven
 * @time: 2021/3/21 17:21
 */
public class ConsistentHashLoadBalancerImpl extends AbstractLoadBalance {

    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors =
            new ConcurrentHashMap<>();

    public List<String> getServiceAddresses(List<Instance> instances) {
        List<String> serviceAddress = new ArrayList();
        for (int i = 0; i < instances.size(); i++) {
            serviceAddress.add(instances.get(i).getIp());
        }
        return serviceAddress;
    }

    public Instance getInstance(List<Instance> instances, String serviceAddress) {
        for (Instance instance : instances) {
            if (instance.getIp().equals(serviceAddress))
                return instance;
        }
        return null;
    }

    @Override
    protected Instance doSelect(List<Instance> instances) {
        List<String> serviceAddresses = getServiceAddresses(instances);

        String serviceName = instances.get(0).getServiceName();
        int identityHashCode = System.identityHashCode(serviceAddresses);

        ConsistentHashSelector selector = selectors.get(identityHashCode);

        if (selector == null || selector.identityHashCode != identityHashCode) {
            selector = new ConsistentHashSelector(serviceAddresses,
                    160, identityHashCode);
            selectors.put(serviceName, selector);
        }

        return getInstance(instances, selector.select(serviceName));
    }


    static class ConsistentHashSelector{
        private final TreeMap<Long, String> virtualInvokers;

        private final int identityHashCode;

        ConsistentHashSelector(List<String> invokers, int replicaNumber,
                               int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, invoker);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24
                    | (long) (digest[2 + idx * 4] & 255) << 16
                    | (long) (digest[1 + idx * 4] & 255) << 8
                    | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        public String select(String rpcServiceName) {
            byte[] digest = md5(rpcServiceName);
            return selectForKey(hash(digest, 0));
        }


        public String selectForKey(long hashCode) {
            Map.Entry<Long, String> entry = virtualInvokers
                    .tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
