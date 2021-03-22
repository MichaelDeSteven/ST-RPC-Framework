package dgut.rpc.transport;

/**
 * @description: AbstractRpcClient
 * @author: Steven
 * @time: 2021/3/12 14:45
 */
public abstract class AbstractRpcClient implements IRpcClient {

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
