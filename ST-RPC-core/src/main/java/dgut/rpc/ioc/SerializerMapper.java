package dgut.rpc.ioc;

import dgut.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: SerializerMapper
 * @author: Steven
 * @time: 2021/3/22 16:12
 */
public class SerializerMapper {

    private static final Logger logger = LoggerFactory.getLogger(SerializerMapper.class);

    private static final Map<Integer, Class> map = new HashMap<>();

    static {
        for (SerializerCode code : SerializerCode.values()) {
            Class clazz = null;
            try {
                clazz = Class.forName(code.getPath());
            } catch (ClassNotFoundException e) {
                logger.error("加载序列器类有错误发生:", e);
                throw new RuntimeException("加载序列器类有错误发生");
            }
            map.put(code.getCode(), clazz);
        }
    }

    public static Class getSerializerClassByCode(int code) {
        return map.get(code);
    }

}
