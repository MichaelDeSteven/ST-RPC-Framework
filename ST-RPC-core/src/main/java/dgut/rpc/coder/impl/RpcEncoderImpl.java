package dgut.rpc.coder.impl;

import dgut.rpc.coder.ICommonEncoder;
import dgut.rpc.ioc.SerializerSingletonFactory;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.serializer.ISerializer;
import dgut.rpc.serializer.protobuf.ProtobufSerializerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.Serializable;

/**
 * @description: RpcEncoderImpl
 * @author: Steven
 * @time: 2021/3/7 16:35
 */
public class RpcEncoderImpl extends MessageToByteEncoder implements ICommonEncoder {

    private static final int MAGIC_NUMBER = 0xBABE;

    private static final int MAX_DATA_LENGTH = (1 << 8) - 1;

    private static final byte MORE_LENGTH = 1 << 0;

    private int serializerCode;

    public RpcEncoderImpl() {
        serializerCode = 1;
    }

    public RpcEncoderImpl(int serializerCode) {
        this.serializerCode = serializerCode;
    }

    @Override
    public byte[] encode(Object object) {
        ISerializer serializer = SerializerSingletonFactory.getInstance().getBeanByCode(serializerCode);

        byte[] data = serializer.serialize(object);
        byte[] stream = null;
        int dstPos = 0;
        byte option = 0;
        if (data.length <= MAX_DATA_LENGTH) {
            stream = new byte[8 + data.length];
            dstPos = 8;

        } else {
            stream = new byte[11 + data.length];
            dstPos = 11;
            option |= MORE_LENGTH;

        }

        byte packageType = 0;
        if (object instanceof RpcResponse) packageType = 1;
        assign(data, packageType, (byte) serializerCode, stream, dstPos, option);
        return stream;
    }

    public void assign(byte[] src, byte packageType, byte serializerType, byte[] dst, int dstPos, byte option) {
        dst[0] = 0XB;
        dst[1] = 0XA;
        dst[2] = 0XB;
        dst[3] = 0XE;
        dst[4] = packageType;
        dst[5] = serializerType;
        dst[6] = (byte) (src.length & 0xFF);
        dst[7] = option;
        if (src.length > MAX_DATA_LENGTH) {
            dst[8] = (byte) ((src.length >> 8) & 0xFF);
            dst[9] = (byte) ((src.length >> 16) & 0XFF);
            dst[10] = (byte) ((src.length >> 24) & 0XFF);
        }
        System.arraycopy(src, 0, dst, dstPos, src.length);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(encode(o)));
    }
}