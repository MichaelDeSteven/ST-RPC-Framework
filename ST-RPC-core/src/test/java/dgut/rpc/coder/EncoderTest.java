package dgut.rpc.coder;

import dgut.rpc.coder.impl.RpcEncoderImpl;
import dgut.rpc.protocol.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @description: EncoderTest
 * @author: Steven
 * @time: 2021/3/7 17:32
 */
@Slf4j
public class EncoderTest {
    public static void main(String[] args) throws IOException {
        ICommonEncoder encoder = new RpcEncoderImpl();
        byte[] bytes = encoder.encode(new RpcRequest());
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i] + " ");
        }
    }
}
