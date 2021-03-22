package dgut.rpc.serializer.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import dgut.rpc.enumeration.SerializerCode;
import dgut.rpc.protocol.RpcRequest;
import dgut.rpc.protocol.RpcResponse;
import dgut.rpc.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @description: KryoSerializerImpl
 * @author: Steven
 * @time: 2021/3/22 14:05
 */
public class KryoSerializerImpl<T> implements ISerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializerImpl.class);

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(T object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new RuntimeException("序列化时发生错误");
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return (T) o;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new RuntimeException("序列化时发生错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}
