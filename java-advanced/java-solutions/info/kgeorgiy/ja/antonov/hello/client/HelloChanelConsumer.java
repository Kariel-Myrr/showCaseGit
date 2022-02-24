package info.kgeorgiy.ja.antonov.hello.client;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.function.Consumer;

public abstract class HelloChanelConsumer implements Consumer<SelectionKey> {

    protected final String msg;
    protected final SocketAddress address;
    protected final ByteBuffer buffer;
    protected final int k;

    public HelloChanelConsumer(SocketAddress address, int bufferSize, int k, String msg) {
        this.msg = msg;
        this.address = address;
        this.buffer = ByteBuffer.allocate(bufferSize);
        this.k = k;
    }
}
