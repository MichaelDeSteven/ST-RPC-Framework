package dgut.rpc.coder;

import java.io.InputStream;

/**
 * @description: ICommonDecoder
 * @author: Steven
 * @time: 2021/3/7 0:29
 */
public interface ICommonDecoder<T, V> {

    T decode(V stream, Class<T> clazz) throws Exception;
}
