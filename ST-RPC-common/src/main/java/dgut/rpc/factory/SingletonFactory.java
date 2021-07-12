package dgut.rpc.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: SingletonFactory
 * @author: Steven
 * @time: 2021/7/11 1:03
 */
public class SingletonFactory {

    private static Map<Class, Object> objectMap = new HashMap<>();

    private SingletonFactory() {}

    public static <T> T getInstance(Class<T> clazz) {
        if (!objectMap.containsKey(clazz)) {
            synchronized (clazz) {
                if (!objectMap.containsKey(clazz)) {
                    try {
                        objectMap.put(clazz, clazz.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return (T) objectMap.get(clazz);
    }
}
