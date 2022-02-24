package info.kgeorgiy.ja.antonov.hello.client;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.Timer;
import java.util.function.Consumer;

class HelloChanelWriterConsumer extends HelloChanelConsumer {

    public HelloChanelWriterConsumer(SocketAddress address, int bufferSize, int k, String msg) {
        super(address, bufferSize, k, msg);
    }


    @Override
    public void accept(SelectionKey selectionKey) {
        if (!selectionKey.isValid()) {
            return;
        }
        HelloChannelInfo info = (HelloChannelInfo) selectionKey.attachment();
        if(info.isWaiting()){
            return;
        }

        DatagramChannel channel = (DatagramChannel) selectionKey.channel();

        try {
            String sending = String.format("%s%d_%d", msg, info.getNumber(), info.getCount());
            buffer.put(sending.getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            channel.send(buffer, address);
            System.out.println("Sent " + info.getNumber() + ": " + sending);
            info.setWaiting();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            buffer.clear();
        }


    }
}
