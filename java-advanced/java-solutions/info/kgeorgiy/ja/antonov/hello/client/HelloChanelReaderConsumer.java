package info.kgeorgiy.ja.antonov.hello.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.function.Consumer;

public class HelloChanelReaderConsumer extends HelloChanelConsumer {


    public HelloChanelReaderConsumer(SocketAddress address, int bufferSize, int k, String msg) {
        super(address, bufferSize, k, msg);
    }


    @Override
    public void accept(SelectionKey selectionKey) {
        if (!selectionKey.isValid()) {
            return;
        }
        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        HelloChannelInfo info = (HelloChannelInfo) selectionKey.attachment();
        try {
            channel.receive(buffer);

            String expected = String.format("%s%d_%d", msg, info.getNumber(), info.getCount());
            String received = new String(buffer.array(), 0, buffer.position());
            if (received.contains(expected)) {
                System.out.println("Received " + info.getNumber() + ": " + received);
                info.inc();
                info.notWaiting();
                if (info.getCount() >= k) {
                    selectionKey.cancel();
                    channel.close();
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            buffer.clear();
        }
    }
}
