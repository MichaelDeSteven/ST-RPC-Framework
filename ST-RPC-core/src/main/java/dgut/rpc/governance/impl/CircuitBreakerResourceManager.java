package dgut.rpc.governance.impl;

import com.alibaba.fastjson.JSON;
import dgut.rpc.config.AbstractServiceConfigImpl;
import dgut.rpc.domain.CircuitBreakerEntity;
import dgut.rpc.domain.ThreadPoolEntity;
import dgut.rpc.enumeration.CircuitBreakerStatus;
import dgut.rpc.governance.CircuitBreaker;
import dgut.rpc.governance.ThreadPoolResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * @description: CircuitBreakerResourceManager
 * @author: Steven
 * @time: 2021/9/12 15:56
 */
public class CircuitBreakerResourceManager extends AbstractServiceConfigImpl {

    private final static Logger logger = LoggerFactory.getLogger(CircuitBreakerResourceManager.class);

    public final Map<String, CircuitBreaker> cache = new ConcurrentHashMap(8);

    private static ThreadPoolResourceManagerImpl threadPoolResourceManager = ThreadPoolResourceManagerImpl.getInstance();

    private static class CircuitBreakerResourceManagerHolder {
        private static CircuitBreakerResourceManager circuitBreakerResourceManager =
                new CircuitBreakerResourceManager();
    }


    public static CircuitBreakerResourceManager getInstance() {
        return CircuitBreakerResourceManagerHolder.circuitBreakerResourceManager;
    }

    public void register(CircuitBreakerEntity entity) {
        cache.computeIfAbsent(entity.getCircuitBreakerName(), rn -> {
            CircuitBreaker circuitBreaker = new SlidingWindowCircuitBreaker(entity.getCircuitBreakerName(),
                    entity.getFailureThreshold(), entity.getErrorPercentThreshold(), entity.getExecutionTimeout(),
                    entity.getCircuitBreakerName());
            return circuitBreaker;
        });
    }

    public CircuitBreaker get(String resourceName) {
        return Optional.ofNullable(cache.get(resourceName)).orElseThrow(()
                -> new IllegalArgumentException(resourceName));
    }

    public void report(String name, CircuitBreakerStatus o, CircuitBreakerStatus n) {
        logger.info(String.format("断路器[%s]状态变更,[%s]->[%s]", name, o, n));
    }

    public void report(String name, String errorInfo) {
        logger.error(String.format("断路器[%s]-[%s]", name, errorInfo));
    }

    public void reset(String name) {
        logger.info(String.format("断路器[%s]重置", name));
    }

    @Override
    protected void updateConfig(String s) {
        List<CircuitBreakerEntity> entityList = JSON.parseArray(s, CircuitBreakerEntity.class);
        Set<String> set = new HashSet();
        for (CircuitBreakerEntity entity : entityList) {
            SlidingWindowCircuitBreaker circuitBreaker =
                    (SlidingWindowCircuitBreaker) cache.get(entity.getCircuitBreakerName());
            if (circuitBreaker == null) {
                register(entity);
                circuitBreaker = (SlidingWindowCircuitBreaker) cache.get(entity.getCircuitBreakerName());
            } else {

            }
            set.add(entity.getCircuitBreakerName());

        }
        for (String circuitBreakerName : cache.keySet()) {
            if (!set.contains(circuitBreakerName)) {
                CircuitBreaker circuitBreaker = cache.get(circuitBreakerName);
                cache.remove(circuitBreakerName);
            }
        }
    }
}
