package info.kgeorgiy.ja.antonov.hello.client;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.channels.*;
import java.util.Set;
import java.util.function.Consumer;

//java -cp . -p . -m info.kgeorgiy.java.advanced.hello client info.kgeorgiy.ja.antonov.hello.client.HelloNonblockingUDPClient

public class HelloNonblockingUDPClient implements HelloClient {
    private static final int TIME_OUT = 200;
    private static final int BUFFER_SIZE = 1000;

    @Override
    public void run(String name, int port, String msg, int n, int k) {

        SocketAddress address;
        try {
            address = new InetSocketAddress(InetAddress.getByName(name), port);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + name);
            System.err.println(e.getMessage());
            return;
        }

        try (Selector selector = Selector.open()) {


            for (int i = 0; i < n; i++) {
                try {
                    DatagramChannel channel = DatagramChannel.open();

                    try {
                        channel.configureBlocking(false);

                        HelloChannelInfo info = new HelloChannelInfo(i, TIME_OUT);
                        channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, info);
                    } catch (IOException e) {
                        channel.close();
                        throw e;
                    }

                } catch (IOException e) {
                    IOException exc = new IOException("Can't open socket or register a channel " + i);
                    exc.addSuppressed(e);
                    selector.close();
                    throw exc;
                }
            }

            Consumer<SelectionKey> writeConsumer = new HelloChanelWriterConsumer(address, BUFFER_SIZE, k, msg);
            Consumer<SelectionKey> readConsumer = new HelloChanelReaderConsumer(address, BUFFER_SIZE, k, msg);

            Set<SelectionKey> keys = selector.keys();

            while (!keys.isEmpty() && selector.isOpen()) {
                selector.select((key) -> {
                    if(!key.isValid()) { return; }
                    if(key.isReadable()){
                        readConsumer.accept(key);
                    } else {
                        writeConsumer.accept(key);
                    }
                }, TIME_OUT);
            }


        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }


    public static void main(String[] args) {
        HelloNonblockingUDPClient client = new HelloNonblockingUDPClient();
        client.run("localhost", 28800, "world!", 20, 20);
    }
}
