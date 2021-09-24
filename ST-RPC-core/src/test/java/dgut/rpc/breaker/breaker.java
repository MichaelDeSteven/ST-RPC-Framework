package dgut.rpc.breaker;

import dgut.rpc.governance.Bucket;
import dgut.rpc.governance.BucketCircular;

import java.util.stream.Stream;

/**
 * @description: breaker
 * @author: Steven
 * @time: 2021/9/14 15:30
 */
public class breaker {
    public static void main(String[] args) {
        BucketCircular circular = new BucketCircular(5);
        circular.addTail(new Bucket(111L));
        circular.addTail(new Bucket(System.currentTimeMillis()));
        circular.addTail(new Bucket(System.currentTimeMillis()));
        circular.addTail(new Bucket(System.currentTimeMillis()));
        circular.addTail(new Bucket(System.currentTimeMillis()));
        circular.addTail(new Bucket(System.currentTimeMillis()));
        circular.addTail(new Bucket(222L));
        Stream.of(circular.getArray()).forEach(System.out::println);
    }
}
