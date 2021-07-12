package dgut.rpc.factory;

/**
 * @description: MethodFactory
 * @author: Steven
 * @time: 2021/7/11 1:01
 */
public interface MethodFactory<T> {

    /**
     * 获取对象实例
     * @return
     */
    T getInstance();
}
