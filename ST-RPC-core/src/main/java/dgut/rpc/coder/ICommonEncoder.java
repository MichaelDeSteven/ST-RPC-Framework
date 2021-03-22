package dgut.rpc.coder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description: ICommonEncoder
 * @author: Steven
 * @time: 2021/3/7 0:29
 */
public interface ICommonEncoder {

    byte[] encode(Object object) throws IOException;
}
