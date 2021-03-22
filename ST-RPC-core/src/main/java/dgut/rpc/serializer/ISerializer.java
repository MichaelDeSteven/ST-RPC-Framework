package dgut.rpc.serializer;

/**
 * @description: ISerializer
 * @author: Steven
 * @time: 2021/3/5 22:51
 */
public interface ISerializer<T> {
    byte[] serialize(T object);

    T deserialize(byte[] bytes, Class<T> clazz);

    int getCode();

}
