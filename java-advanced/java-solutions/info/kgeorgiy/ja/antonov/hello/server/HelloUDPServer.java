package info.kgeorgiy.ja.antonov.hello.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HelloUDPServer implements info.kgeorgiy.java.advanced.hello.HelloServer {

    private DatagramSocket socket;
    private ExecutorService executor;

    @Override
    public void start(int port, int i) {

        try {
            //TODO что если старт вызовут несколько раз?
            executor = Executors.newFixedThreadPool(i);
            socket = new DatagramSocket(port);

            for (int j = 0; j < i; j++) {
                executor.submit(receiverFactory(j));
            }

        } catch (IOException e) {
            //TODO
            System.err.println("Can't open ServerSocket");
            System.err.println(e.getMessage());
        }


    }

    private Runnable receiverFactory(int i) {

        return () -> {

            try {

                byte[] buffer = new byte[socket.getReceiveBufferSize()];

                while (!socket.isClosed() || !executor.isShutdown()) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

                        socket.receive(packet);


                        String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                        System.out.println("Received: " + msg);

                        packet.setData(String.format("Hello, %s", msg).getBytes(StandardCharsets.UTF_8));
                        try {
                            socket.send(packet);
                        } catch (IOException e) {
                            System.err.println("Receiver " + i + " Can't send packet");
                            System.err.println("Cause: " + e.getMessage());
                        }
                    } catch (IOException e) {
                        System.err.println("Receiver " + i + " Can't receive packet");
                        System.err.println("Cause: " + e.getMessage());
                    }

                }
            } catch (SocketException e) {
                System.err.println("Receiver " + i + " can't make buffer");
                System.err.println("Cause: " + e.getMessage());
            }
        };



    }

    @Override
    public void close() {
        socket.close();
        executor.shutdown();
        try {
            //TODO
            executor.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Can't terminate ExecutorService");
            System.err.println(e.getMessage());
        }

    }

    public static void main(String[] args) {
        HelloUDPServer server = new HelloUDPServer();
        server.start(28800, 2);
    }
}
