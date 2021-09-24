package dgut.rpc.coder.impl;

import dgut.rpc.coder.ByteBufAdapter;
import dgut.rpc.coder.ICommonDecoder;
import dgut.rpc.enumeration.RpcError;
import dgut.rpc.ioc.SerializerSingletonFactory;
import dgut.rpc.serializer.ISerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * @description: RpcDecoderImpl
 * @author: Steven
 * @time: 2021/3/7 16:32
 */
public class RpcDecoderImpl<T> extends ReplayingDecoder<T> implements ICommonDecoder<T> {

    private static final int MAGIC_NUMBER = 0xBABE;

    private static final Logger logger = LoggerFactory.getLogger(RpcDecoderImpl.class);

    private static final int MORE_LENGTH_MASK = 1 << 0;

    private int serializerCode;

    private Class<T> clazz;

    public RpcDecoderImpl() {}

    public RpcDecoderImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    public int getSerializerCode() {
        return serializerCode;
    }

    @Override
    public T decode(InputStream stream, Class<T> clazz) throws Exception {
        byte[] head = new byte[8];
        byte[] scaling = new byte[3];
        stream.read(head, 0, 8);
        int magicNumber = (head[0] << 12) + (head[1] << 8) + (head[2] << 4) + head[3];
        int packageType = head[4];
        int serializerType = head[5];
        int dataLength = getLength(head[6]);
        int option = head[7];

        if (MAGIC_NUMBER != magicNumber) {
            throw new Exception(RpcError.UNKNOWN_PROTOCOL.getMessage());
        }

        if (packageType != 0 && packageType != 1) {
            throw new Exception(RpcError.UNKNOWN_PACKAGE_TYPE.getMessage());
        }

        if ((option & MORE_LENGTH_MASK) != 0) {
            stream.read(scaling, 0, 3);
            dataLength = ((scaling[2] << 24) + (scaling[1] << 16) + (scaling[0] << 8)) + dataLength;
        }
        byte[] date = new byte[dataLength];
        stream.read(date, 0, dataLength);
        ISerializer serializer = SerializerSingletonFactory.getInstance().getBeanByCode(serializerType);

        T object = (T) serializer.deserialize(date, clazz);
        return object;
    }

    public int getLength(byte b) {
        return b & 0xFF;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf, List<Object> list) throws Exception {
        ByteBufAdapter byteBufAdapter = new ByteBufAdapter(byteBuf);
        list.add(decode(byteBufAdapter, clazz));
    }
}