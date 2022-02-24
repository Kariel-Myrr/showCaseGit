package info.kgeorgiy.ja.antonov.hello.server;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class HelloNonblockingUDPServer implements HelloServer {

    private static final int BUFFER_SIZE = 2048;

    private ExecutorService executor;
    private ExecutorService serverExecutor;
    private Selector selector;
    private DatagramChannel channel;


    @Override
    public void start(int port, int n) {
        executor = Executors.newFixedThreadPool(n);

        try {
            selector = Selector.open();
        } catch (IOException e) {
            System.err.println("Can't open selector");
            System.err.println(e.getMessage());
            return;
        }

        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            System.err.println("Can't open channel");
            System.err.println(e.getMessage());
            return;
        }

        try {
            channel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {
            System.err.println("Can't register channel. It's closed");
            System.err.println(e.getMessage());
            return;
        }

        serverExecutor = Executors.newSingleThreadExecutor();

        serverExecutor.submit(() -> {
            while (!executor.isShutdown() && selector.isOpen()) {
                try {
                    if (selector.select(200) > 0) {
                        Set<SelectionKey> set = selector.selectedKeys();

                        Iterator<SelectionKey> iterator = set.iterator();

                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            executor.submit(() -> {
                                        Consumer<SelectionKey> consumer = new HelloServerChannelConsumer(BUFFER_SIZE);
                                        consumer.accept(key);
                                    }
                            );
                            iterator.remove();
                        }
                    }
                } catch (AsynchronousCloseException e) {
                    System.err.println("Server thread. Async close: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Server thread IOException: " + e.getMessage());
                }
            }
        });

        /*for (int i = 0; i < n; i++) {
            executor.submit(() -> {
                Consumer<SelectionKey> consumer = new HelloServerChannelConsumer(BUFFER_SIZE);
                while (!executor.isShutdown() && selector.isOpen()) {
                    try {
                        selector.select(consumer);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }
            });
        }*/


    }

    @Override
    public void close() {
        try {
            selector.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try {
            channel.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        executor.shutdownNow();
        serverExecutor.shutdownNow();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloServer server = new HelloNonblockingUDPServer();
        server.start(28800, 1);
        Thread.sleep(10000);
        server.close();
    }
}
