package dgut.rpc.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import dgut.rpc.enumeration.SerializerCode;
import dgut.rpc.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description: HessianSerializerImpl
 * @author: Steven
 * @time: 2021/3/22 14:10
 */
public class HessianSerializerImpl<T> implements ISerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializerImpl.class);

    @Override
    public byte[] serialize(T object) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new RuntimeException("序列化时发生错误");
        } finally {
            if (hessianOutput != null) {
                try {
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭流时有错误发生:", e);
                }
            }
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessianInput = new HessianInput(byteArrayInputStream);
            return (T) hessianInput.readObject();
        } catch (IOException e) {
            logger.error("序列化时有错误发生:", e);
            throw new RuntimeException("序列化时发生错误");
        } finally {
            if (hessianInput != null) hessianInput.close();
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.HESSIAN.getCode();
    }
}
