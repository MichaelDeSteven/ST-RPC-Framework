package dgut.rpc.handler;

/**
 * @description: Handler
 * @author: Steven
 * @time: 2021/3/8 20:29
 */
public interface Handler<T> {

    Object handle(T obj) throws Exception;
}
