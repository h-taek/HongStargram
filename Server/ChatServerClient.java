package Server;

import java.io.*;
import java.net.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChatServerClient {
    private static final String host_ip = IP.ip; // 서버 IP
    private static final int port = 8001;               // 서버 포트

    private String sender;
    private String receiver;
    public List<Map<String, String>> chat_log;
    private String msg;

    private Socket socket;
    BufferedReader in;
    PrintWriter out;

    private ChatListener cl;

    public ChatServerClient(String sender, String receiver, ChatListener cl) {
        this.sender = sender;
        this.receiver = receiver;
        this.cl = cl;

        try {
            socket = new Socket(host_ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // id 전송
            out.println(sender + " " + receiver);

            // 첫 입력은 채팅 히스토리
            String temp = in.readLine();
            Gson gson = new Gson();
            chat_log = gson.fromJson(temp, new TypeToken<List<Map<String, String>>>(){}.getType());

            System.out.println("Server connected : " + socket.getRemoteSocketAddress());
            
            // 서버에서 오는 메시지를 스레드로 수신
            Thread reader = new Thread(() -> {
                try {
                    while ((msg = in.readLine()) != null) {
                        if (cl != null) {cl.onMessage(msg);}
                    }
                } catch (IOException ignore) {
                } finally {
                    try { 
                        in.close();
                        out.close();
                        socket.close(); 
                    } catch (IOException ignore) {}
                    System.out.printf("Server disconnected");
            }
            });
            reader.setDaemon(true);
            reader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 입력을 서버로 전송
    public void sendMessage(String msg) {
        if (out != null && msg != null) {out.println(msg);}
    }
}
