package dgut.rpc.governance;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Consumer;

/**
 * @description: BucketCircular
 * @author: Steven
 * @time: 2021/9/12 17:02
 */
public class BucketCircular implements Iterable<Bucket> {

    private final AtomicReference<BucketArray> bucketArray;

    private final int dataLength;

    private final int numBuckets;

    public BucketCircular(int bucketNumber) {
        /**
         * dataLength = numBuckets + 1是为了方便环形队列的增加删除操作
         *
         */
        AtomicReferenceArray<Bucket> buckets = new AtomicReferenceArray(bucketNumber + 1);
        this.bucketArray = new AtomicReference(new BucketArray(buckets, 0, 0));
        this.dataLength = buckets.length();
        this.numBuckets = bucketNumber;
    }

    /**
     * 用于操作循环队列
     */
    private class BucketArray {

        private final int head;

        private final int tail;

        private final int size;

        private final AtomicReferenceArray<Bucket> buckets;

        private BucketArray(AtomicReferenceArray<Bucket> buckets, int head, int tail) {
            this.buckets = buckets;
            this.head = head;
            this.tail = tail;
            if (head == 0 && tail == 0) {
                size = 0;
            } else {
                this.size = (tail - head + dataLength) % dataLength;
            }
        }

        public Bucket tail() {
            return buckets.get(convert(size - 1));
        }

        public BucketArray addBucket(Bucket bucket) {
            buckets.set(tail, bucket);
            return incrementTail();
        }

        public int getSize() {
            return (tail - head + dataLength) % dataLength;
        }

        public BucketArray clear() {
            return new BucketArray(new AtomicReferenceArray(dataLength), 0, 0);
        }

        private BucketArray incrementTail() {
            // 环形队列
            if (size == numBuckets) {
                return new BucketArray(buckets, (head + 1) % dataLength, (tail + 1) % dataLength);
            } else {
                return new BucketArray(buckets, head, (tail + 1) % dataLength);
            }
        }

        private Bucket[] getArray() {
            ArrayList<Bucket> array = new ArrayList();
            for (int i = 0; i < size; i++) {
                array.add(buckets.get(convert(i)));
            }
            return array.toArray(new Bucket[array.size()]);
        }

        private int convert(int index) {
            return (head + index) % dataLength;
        }
    }

    /**
     * 在环形队列尾部添加一个桶
     * @param bucket
     */
    public void addTail(Bucket bucket) {
        BucketArray bucketArray = this.bucketArray.get();
        BucketArray newBucketArray = bucketArray.addBucket(bucket);
        if (this.bucketArray.compareAndSet(bucketArray, newBucketArray)) {
            return;
        }
    }

    public Bucket getTail() {
        return this.bucketArray.get().tail();
    }

    public int size() {
        return this.bucketArray.get().getSize();
    }

    public void clear() {
        while (true) {
            BucketArray bucketArray = this.bucketArray.get();
            BucketArray clear = bucketArray.clear();
            if (this.bucketArray.compareAndSet(bucketArray, clear)) {
                return;
            }
        }
    }

    public Bucket[] getArray() {
        return this.bucketArray.get().getArray();
    }

    @Override
    public Iterator<Bucket> iterator() {
        return Collections.unmodifiableList(Arrays.asList(getArray())).iterator();
    }

    @Override
    public void forEach(Consumer<? super Bucket> action) {

    }

    @Override
    public Spliterator<Bucket> spliterator() {
        return null;
    }
}
