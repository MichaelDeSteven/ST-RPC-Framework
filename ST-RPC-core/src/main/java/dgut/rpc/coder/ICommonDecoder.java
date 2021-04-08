package dgut.rpc.coder;

import java.io.InputStream;

/**
 * @description: ICommonDecoder
 * @author: Steven
 * @time: 2021/3/7 0:29
 */
public interface ICommonDecoder<T> {

    T decode(InputStream stream, Class<T> clazz) throws Exception;
}
