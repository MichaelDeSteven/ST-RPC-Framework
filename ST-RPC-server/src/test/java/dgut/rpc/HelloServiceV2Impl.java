package dgut.rpc;

import dgut.rpc.annotation.Service;

/**
 * @description: HelloServiceV2Impl
 * @author: Steven
 * @time: 2021/3/21 14:17
 */
@Service
public class HelloServiceV2Impl implements IHelloService {
    @Override
    public String hello(String name) {
        return "hello " + name + " v2";
    }
}
