package dgut.rpc.ioc;

/**
 * @description: IAbstractFactory
 * @author: Steven
 * @time: 2021/3/22 15:34
 */
public interface IAbstractFactory<T> {

    T getBean(Class<T> clazz);
}
