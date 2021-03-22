package dgut.rpc.coder.netty;

import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.ioc.SerializerSingletonFactory;
import dgut.rpc.serializer.ISerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @description: NettyCommonDecoder
 * @author: Steven
 * @time: 2021/3/19 17:36
 */
public class NettyCommonDecoder<T, V extends ByteBuf> extends ReplayingDecoder<T>
        implements ICommonDecoder<T, V> {

    private static final int MAGIC_NUMBER = 0xBABE;

    private Class<T> clazz;

    public NettyCommonDecoder(Class<T> clazz) {
        this.clazz = clazz;
    }


    @Override
    public T decode(V stream, Class<T> clazz) throws Exception {
        byte[] head = new byte[8];
        byte[] scaling = new byte[3];
        stream.readBytes(head, 0, 8);
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
            stream.readBytes(scaling, 0, 3);
            dataLength = ((scaling[2] << 21) + (scaling[1] << 14) + (scaling[0] << 7))
                    + dataLength;
        }
        byte[] date = new byte[dataLength];
        stream.readBytes(date, 0, dataLength);

        ISerializer serializer = SerializerSingletonFactory
                .getInstance()
                .getBeanByCode(serializerType);

        T object = (T) serializer.deserialize(date, clazz);
        return object;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        list.add(decode((V) byteBuf, clazz));
    }
}
