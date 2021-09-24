package dgut.rpc.enumeration;

/**
 * @description: CircuitBreakerStatus
 * @author: Steven
 * @time: 2021/9/8 14:43
 */
public enum CircuitBreakerStatus {

    CLOSED(0, "关闭"),
    OPEN(1, "打开"),
    HALF_OPEN(2, "半打开")
    ;

    private int code;

    private String desc;

    CircuitBreakerStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
