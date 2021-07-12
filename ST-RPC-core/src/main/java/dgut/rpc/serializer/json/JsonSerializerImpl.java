package dgut.rpc.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgut.rpc.enumeration.SerializerCode;
import dgut.rpc.serializer.ISerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @description: JsonSerializerImpl
 * @author: Steven
 * @time: 2021/3/22 13:57
 */
public class JsonSerializerImpl<T> implements ISerializer<T> {

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializerImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(T object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生:", e);
            throw new RuntimeException("序列化时发生错误");
        }
    }

    @Override
    public T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            T obj = objectMapper.readValue(bytes, clazz);
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生:", e);
            throw new RuntimeException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.JSON.getCode();
    }

}
