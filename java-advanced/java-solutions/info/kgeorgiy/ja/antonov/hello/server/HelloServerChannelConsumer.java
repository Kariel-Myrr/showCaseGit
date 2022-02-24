package info.kgeorgiy.ja.antonov.hello.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class HelloServerChannelConsumer implements Consumer<SelectionKey> {

    private final ByteBuffer buffer;

    public HelloServerChannelConsumer(int bufferSize) {
        this.buffer = ByteBuffer.allocate(bufferSize);
    }


    @Override
    public void accept(SelectionKey selectionKey) {
        if(!selectionKey.isValid()){
            return;
        }
        DatagramChannel channel = (DatagramChannel) selectionKey.channel();
        try {
            buffer.clear();
            if(!channel.isOpen()){ return; }
            SocketAddress address = channel.receive(buffer);

            String msg = new String(buffer.array(), 0, buffer.position());

            buffer.clear();

            buffer.put(String.format("Hello, %s", msg).getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            if(!channel.isOpen()) { return; }
            channel.send(buffer, address);
        } catch (AsynchronousCloseException e){
            System.err.println("Consumer. Async close: " + e.getMessage());
        } catch (ClosedChannelException e){
            System.err.println("Consumer. Channel closed: " + e.getMessage());
            //e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Consumer. IOException: " + e.getMessage());
        }
    }
}
