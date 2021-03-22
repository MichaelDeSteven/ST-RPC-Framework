package dgut.rpc.coder.socket;

import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.enumeration.SerializerCode;
import dgut.rpc.ioc.SerializerSingletonFactory;
import dgut.rpc.serializer.ISerializer;
import dgut.rpc.serializer.protobuf.ProtobufSerializerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * @description: RpcDecoderImpl
 * @author: Steven
 * @time: 2021/3/7 16:32
 */
public class RpcDecoderImpl<T, V extends InputStream> implements ICommonDecoder<T, V> {

    private static final int MAGIC_NUMBER = 0xBABE;

    private static final Logger logger = LoggerFactory.getLogger(RpcDecoderImpl.class);

    private int serializerCode;

    public int getSerializerCode() {
        return serializerCode;
    }

    @Override
    public T decode(V stream, Class<T> clazz) throws Exception {
        byte[] head = new byte[8];
        byte[] scaling = new byte[3];
        stream.read(head, 0, 8);
        int magicNumber = (head[0] << 12) + (head[1] << 8) + (head[2] << 4) + head[3];
        int packageType = head[4];
        int serializerType = head[5];
        int dataLength = head[6];
        int Option = head[7];

        if (MAGIC_NUMBER != magicNumber) {
            throw new Exception(RpcError.UNKNOWN_PROTOCOL.getMessage());
        }

        if (packageType != 0 && packageType != 1) {
            throw new Exception(RpcError.UNKNOWN_PACKAGE_TYPE.getMessage());
        }

        if ((Option & (1 << 1)) != 0) {
            stream.read(scaling, 0, 3);
            dataLength = ((scaling[2] << 21) + (scaling[1] << 14) + (scaling[0] << 7))
                    + dataLength;
        }
        byte[] date = new byte[dataLength];
        stream.read(date, 0, dataLength);

        serializerCode = serializerType;
        ISerializer serializer = SerializerSingletonFactory
                .getInstance()
                .getBeanByCode(serializerType);

        T object = (T) serializer.deserialize(date, clazz);
        return object;
    }


}