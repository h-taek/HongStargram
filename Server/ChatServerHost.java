package Server;

import java.io.*;
import java.net.*;
import java.util.*;

// import com.google.gson.JsonObject;

import Json.Json;

public class ChatServerHost {
    static final int port = 8001;
    private static final Map<String, PrintWriter> CLIENTS = 
                Collections.synchronizedMap(new HashMap<>()); // 모든 클라이언트의 출력 스트림을 보관 (동기화된 Set)

    // 현재 서버의 로컬 IP 확인
    private static String getLocalIp() throws IOException {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }

    // 한 명에게만 메세지 전송
    private static void sendMessage(String sender, String receiver, 
                String msg, Json chat_sender, Json chat_receiver) {
        PrintWriter target = CLIENTS.get(receiver);
        try {
            chat_sender.addChat(sender, receiver, msg);
            chat_receiver.addChat(sender, sender, msg);
            
            if (target != null) {
                target.println(msg);
                target.flush();
            }
        } catch (Exception e) {System.out.println("ChatServerHost.sendMessage Err");}
    }

    // 각 클라이언트를 전담 처리하는 스레드
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String sender;
        private String receiver;
        private BufferedReader in;
        private PrintWriter out;

        Json chat_sender;
        Json chat_receiver;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true); // autoFlush
                
                // 첫 입력은 id
                String [] id = in.readLine().split(" ");
                if (id[1].equals("/")) return;

                sender = id[0]; receiver = id[1];

                // 접속 즉시 출력 스트림을 등록
                CLIENTS.put(sender ,out);

                chat_sender = new Json(".user_data/chat/chat_" + sender + ".json");
                chat_receiver = new Json(".user_data/chat/chat_" + receiver + ".json");

                // 접속하면 서버의 기록을 전송
                String chat_log = chat_sender.getChat(receiver);
                out.println(chat_log);
                

                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.printf("Client connected : %s (%s)\n", sender, isa.getAddress().getHostAddress());

                //메세지를 입력받고 전송
                String line;
                while ((line = in.readLine()) != null) {
                    sendMessage(sender, receiver, line, chat_sender, chat_receiver);
                }
            } catch (IOException e) { 
            } finally {
                if (out != null) {CLIENTS.remove(sender);}
                try { 
                    in.close();
                    out.close();
                    socket.close(); 
                } catch (IOException ignore) {}
                System.out.printf("Client disconnected : %s\n", sender);
            }
        }
    }

    public ChatServerHost() {            
        try{
            String ip = getLocalIp();
            System.out.printf("Chat server starting on %s : %d%n", ip, port);
        } catch (IOException ignore) {}

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
