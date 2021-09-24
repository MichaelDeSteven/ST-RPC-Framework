package dgut.rpc.ioc;

import dgut.rpc.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: SerializerSingletonFactory
 * @author: Steven
 * @time: 2021/3/22 15:36
 */
public class SerializerSingletonFactory
        implements IAbstractFactory<ISerializer> {

    private static final Logger logger = LoggerFactory.getLogger(SerializerSingletonFactory.class);

    private ConcurrentHashMap<String, ISerializer> serializerBeanMap = new ConcurrentHashMap<>();

    private SerializerSingletonFactory() {}

    private static class SerializerSingletonFactoryHolder {
        private static SerializerSingletonFactory instance = new SerializerSingletonFactory();
    }

    public static SerializerSingletonFactory getInstance() {
        return SerializerSingletonFactoryHolder.instance;
    }

    @Override
    public ISerializer getBean(Class clazz) {
        ISerializer instance = null;
        if (serializerBeanMap.contains(clazz.getName())) {
            System.out.println("已存在");
            return serializerBeanMap.get(clazz.getName());
        }
        try {
            instance = (ISerializer) clazz.newInstance();
            serializerBeanMap.put(clazz.getName(), instance);
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("实例化序列器有错误发生:", e);
            throw new RuntimeException("实例化序列器有错误发生");
        }
    }

    public ISerializer getBeanByCode(int code) {
        Class clazz = SerializerMapper.getSerializerClassByCode(code);
        ISerializer serializer = getBean(clazz);
        return serializer;
    }
}
