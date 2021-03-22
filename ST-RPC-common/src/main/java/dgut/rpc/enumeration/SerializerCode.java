package dgut.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: SerializerCode
 * @author: Steven
 * @time: 2021/3/22 15:27
 */

@AllArgsConstructor
@Getter
public enum SerializerCode {

    KRYO(0, "dgut.rpc.serializer.kryo.KryoSerializerImpl"),
    JSON(1, "dgut.rpc.serializer.json.JsonSerializerImpl"),
    HESSIAN(2, "dgut.rpc.serializer.hessian.HessianSerializerImpl"),
    PROTOBUF(3, "dgut.rpc.serializer.protobuf.ProtobufSerializerImpl");

    private final int code;

    private final String path;

}