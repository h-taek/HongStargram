package Back.API;

import java.io.IOException;

public class Server {
    private static String getLoclaIp() throws IOException {
        try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
            socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }

    public static void main(String[] args) {
        try {
            String host_ip = getLoclaIp();
            System.out.printf("Server starting on local ip: %s\n", host_ip);

            new Thread(() -> {
                try {
                    new InfoServerHost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                new ChatServerHost();
            }).start();
            new Thread(() -> {
                try {
                    new PostServerHost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    new RoutineServerHost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            new Thread(() -> {
                try {
                    new LocationServerHost();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
