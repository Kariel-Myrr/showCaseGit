package info.kgeorgiy.ja.antonov.hello.client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//java -cp . -p . -m info.kgeorgiy.java.advanced.hello client info.kgeorgiy.ja.antonov.hello.client.HelloUDPClient

public class HelloUDPClient implements info.kgeorgiy.java.advanced.hello.HelloClient {

    private final int TIME_OUT = 200;

    private ExecutorService executors;
    private SocketAddress address;

    @Override
    public void run(String name, int port, String msg, int n, int k) {

        try {
            address = new InetSocketAddress(InetAddress.getByName(name), port);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + name);
            System.err.println(e.getMessage());
            return;
        }

        executors = Executors.newFixedThreadPool(n);

        CountDownLatch count = new CountDownLatch(n);

        for (int i = 0; i < n; i++) {
            executors.submit(senderFactory(msg, i, k, count));
        }

        try {
            count.await();
        } catch (InterruptedException ignored) {

        } finally {
            executors.shutdownNow();
        }
    }


    private Runnable senderFactory(String prefix, int n, int k, CountDownLatch count) {

        return () -> {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIME_OUT);
                int cur = 0;
                byte[] buffer = new byte[socket.getReceiveBufferSize()];
                while (cur < k) {
                    String msg = String.format("%s%d_%d", prefix, n, cur);

                    while (!socket.isClosed() && !executors.isShutdown()) {
                        DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length, address);
                        packet.setData(msg.getBytes(StandardCharsets.UTF_8));
                        try {
                            socket.send(packet);
                            System.out.println("Send: " + msg);


                            DatagramPacket receivePacket = new DatagramPacket(buffer, 0, buffer.length);
                            socket.receive(receivePacket);

                            String resMsg = new String(receivePacket.getData(),
                                    receivePacket.getOffset(),
                                    receivePacket.getLength(),
                                    StandardCharsets.UTF_8);

                            if (resMsg.contains(msg)) {
                                System.out.println("Received: " + resMsg);
                                break;
                            } else {
                                System.out.println("Received not exp: " + resMsg);
                            }
                        } catch (SocketTimeoutException ignored) {
                            System.out.println("Sender: " + n + " Time Out. Resending " + cur);
                        } catch (IOException e) {
                            System.err.println("Sender " + n + " Can't send/receive packet");
                            System.err.println("Cause: " + e.getMessage());
                        }
                    }

                    //System.out.println("Cur: " + cur + " in Sender: " + n );

                    cur++;
                }
            } catch (SocketException e) {
                System.err.println("Sender + " + n + "Can't open socket");
                System.err.println(e.getMessage());
            } finally {
                count.countDown();
            }


        };

    }

    public static void main(String[] args) {
        HelloUDPClient client = new HelloUDPClient();
        client.run("localhost", 28800, "world!", 8, 2);
    }
}
