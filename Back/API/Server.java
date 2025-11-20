package Back.API;

import java.io.IOException;

public class Server {
    public static void main(String[] args) {
        try {
            new Thread(() -> {
                try { new InfoServerHost(); }
                catch (IOException e) { e.printStackTrace(); }
            }).start();
            new Thread(() -> {new ChatServerHost();}).start();
            new Thread(() -> {
                try { new PostServerHost(); }
                catch (IOException e) { e.printStackTrace(); }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
