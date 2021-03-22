package dgut.rpc;

import dgut.rpc.annotation.Service;

/**
 * @description: dgut.rpc.HelloServiceImpl
 * @author: Steven
 * @time: 2021/3/5 0:20
 */
@Service(group = "dgut", version = "v1.0")
public class HelloServiceImpl implements IHelloService {

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
