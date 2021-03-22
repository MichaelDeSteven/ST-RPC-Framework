package dgut.rpc.serializer.protobuf;

import dgut.rpc.enumeration.SerializerCode;
import dgut.rpc.serializer.ISerializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;


/**
 * @description: ProtobufSerializerImpl
 * @author: Steven
 * @time: 2021/3/5 22:55
 */
public class ProtobufSerializerImpl<T> implements ISerializer<T> {

    private LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    public byte[] serialize(T object) {
        Schema schema = RuntimeSchema.getSchema(object.getClass());
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, linkedBuffer);
        } finally {
            linkedBuffer.clear();
        }
        return data;
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) {
        Schema schema = RuntimeSchema.getSchema(clazz);
        T message = (T) schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        return message;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }

}
