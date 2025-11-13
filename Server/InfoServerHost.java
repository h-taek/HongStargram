package Server;

import com.sun.net.httpserver.*;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import com.google.gson.*;

import Json.Json;
import java.nio.charset.StandardCharsets;


class SignupHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String id = data.get("id");
            String pw = data.get("pw");
            String nName = data.get("nName");
            Json store = new Json(".user_data/user.json");
            
            String response = "";
            if (store.getUser(id)) {
                response = "User exists";
                exchange.sendResponseHeaders(999, response.length());
            } else {
                store.addUser(id, pw, nName);
                response = "Success";
                exchange.sendResponseHeaders(1, response.length());
            }

            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            System.out.println("InfoServerHost.SignupHandler Err!");
            e.printStackTrace();
            try {
                String error = "Server Error";
                exchange.sendResponseHeaders(500, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } finally {
            exchange.close();
        }
    }
}

class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String id = data.get("id");
            String pw = data.get("pw");
            Json store = new Json(".user_data/user.json");

            String [] info = store.getUserInfo(id);
            if (info[0].equals(pw)) {
                String nName = info[1];
                byte[] payload = nName.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(1, payload.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(payload);
                }
            } else {
                exchange.sendResponseHeaders(999, -1);
            }
        } catch (Exception e) {
            System.out.println("InfoServerHost.LoginHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class GetNNameHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Json store = new Json(".user_data/user.json");
            String nName = store.getUserNName(id);

            if (nName != null) {
                byte[] payload = nName.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(1, payload.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(payload);
                }
            }
            else {
                exchange.sendResponseHeaders(999, -1);
            }
        } catch (Exception e) {
            System.out.println("InfoServerHost.GetNNameHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class ChatListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            // System.out.println(id);

            Json store = new Json(".user_data/chat/chat_" + id + ".json");

            String chat_list = store.getChatList(id);
            // System.out.println(info);
            byte[] payload = chat_list.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.ChatListHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class FriendListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            
            Json store = new Json(".user_data/user.json");

            String friend_list = store.getFriend(id);
            byte[] payload = friend_list.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendListHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class FriendStatusHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            
            Json store = new Json(".user_data/user.json");

            String friend_status = store.getFriendStatus(id);
            byte[] payload = friend_status.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendStatusHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class FriendAddHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String sender = data.get("sender");
            String receiver = data.get("receiver");
            System.out.println("FriendAdd Request from " + sender + " to " + receiver);
            boolean flag;
            if (data.get("flag").equals("true")) flag = true;
            else flag = false;

            Json store = new Json(".user_data/user.json");
            store.friendAdd(sender, receiver, flag);
            exchange.sendResponseHeaders(1,-1);
        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendAddHandler Err!");
            e.printStackTrace();
        }

        exchange.close();
        return;
    }
}

class FriendRequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String my_id = data.get("my_id");
            String your_id = data.get("your_id");
            boolean flag;
            if (data.get("flag").equals("true")) flag = true;
            else flag = false;

            Json store = new Json(".user_data/user.json");
            store.friendRequest(my_id, your_id, flag);
            exchange.sendResponseHeaders(1,-1);
        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendRequestHandler Err!");
            e.printStackTrace();
        }

        exchange.close();
        return;
    }
}

public class InfoServerHost {
    private static final int port = 8000;

    private static String getLoclaIp() throws IOException {
        try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
            socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }
    
    public InfoServerHost() throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/test", exchange -> {
            String response = "Info server is connected";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.createContext("/Signup", new SignupHandler());
        server.createContext("/Login", new LoginHandler());
        server.createContext("/ChatList", new ChatListHandler());
        server.createContext("/GetNName", new GetNNameHandler());
        server.createContext("/FriendList", new FriendListHandler());
        server.createContext("/FriendStatus", new FriendStatusHandler());
        server.createContext("/FriendAdd", new FriendAddHandler());
        server.createContext("/FriendRequest", new FriendRequestHandler());
        

        server.setExecutor(null);
        server.start();

        String host_ip = getLoclaIp();
        System.out.printf("Info server starting on %s : %d\n", host_ip, port);
    }
}
