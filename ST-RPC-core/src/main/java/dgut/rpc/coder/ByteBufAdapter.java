package dgut.rpc.coder;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description: ByteBufAdapter
 * @author: Steven
 * @time: 2021/4/8 16:25
 */
public class ByteBufAdapter extends InputStream {

    private final int startIndex;
    private final int endIndex;
    private final ByteBuf byteBuf;

    public ByteBufAdapter(ByteBuf byteBuf) {
        this(byteBuf, byteBuf.readableBytes());
    }

    public ByteBufAdapter(ByteBuf byteBuf, int length) {
        if (length < 0) {
            byteBuf.release();
            throw new IllegalArgumentException("length: " + length);

        } else if (length > byteBuf.readableBytes()) {
            throw new IndexOutOfBoundsException("读取长度 " + length + " 超过最大可读字节" + byteBuf.readableBytes());
        } else {
            this.byteBuf = byteBuf;
            this.startIndex = byteBuf.readerIndex();
            this.endIndex = this.startIndex + length;
        }
    }

    public int available() throws IOException {
        return this.endIndex - this.byteBuf.readerIndex();
    }

    @Override
    public int read() throws IOException {
        int available = this.available();
        return available == 0 ? -1 : this.byteBuf.readByte() & 255;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        int available = this.available();
        if (available == 0) {
            return -1;
        } else {
            len = Math.min(available, len);
            this.byteBuf.readBytes(b, off, len);
            return len;
        }
    }

    public void close() throws IOException {
        try {
            super.close();
        } finally {
            this.byteBuf.release();
        }
    }
}
