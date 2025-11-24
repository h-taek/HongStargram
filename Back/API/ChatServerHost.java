package Back.API;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import Back.Data.ChatDB;

public class ChatServerHost {
    static final int port = 8004;
    private static final Map<String, PrintWriter> CLIENTS = 
                Collections.synchronizedMap(new HashMap<>()); // 모든 클라이언트의 출력 스트림을 보관 (동기화된 Set)


    // 메세지 전송
    private static void sendMessage(String sender, List<String> receiver, int chat_id, 
                String msg, ChatDB chatDB) {
        try {
            chatDB.addChat(chat_id, sender, msg);
            
            for (String r : receiver) {
                PrintWriter target = CLIENTS.get(r);
                if (target != null) {
                    String json = "{\"sender\":\"" + sender + "\",\"msg\":\"" + msg + "\"}";
                    target.println(json);
                    target.flush();
                }
            }
        } catch (Exception e) {System.out.println("ChatServerHost.sendMessage Err");}
    }

    // 각 클라이언트를 전담 처리하는 스레드
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String sender;
        private BufferedReader in;
        private PrintWriter out;

        ChatDB chatDB;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true); // autoFlush
                
                // 첫 입력은 id
                String body = in.readLine();
                Gson gson = new Gson();
                Map<String, String> chat_json = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

                int chat_id = Integer.parseInt(chat_json.get("chat_id").toString());
                String sender = chat_json.get("sender").toString();
                String receivers = chat_json.get("receiver").toString();
                List<String> receiver_list = gson.fromJson(receivers, new TypeToken<List<String>>(){}.getType());

                // 접속 즉시 출력 스트림을 등록
                CLIENTS.put(sender ,out);
                chatDB = new ChatDB();

                // 채팅방 생성
                if (chat_id == -1) {chat_id = chatDB.addNewChatRoom(receivers);}

                // 접속하면 서버의 기록을 전송
                String chat_log = chatDB.getChat(chat_id);
                out.println(chat_log);
                
                InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
                System.out.printf("Client connected : %s (%s)\n", sender, isa.getAddress().getHostAddress());

                //메세지를 입력받고 전송
                String line;
                while ((line = in.readLine()) != null) {
                    sendMessage(sender, receiver_list, chat_id, line, chatDB);
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
        System.out.printf("Chat server starting on htaeky.iptime.org : %d\n", port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ChatServerHost Err!");
        }
    }
}
