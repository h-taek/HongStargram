package Back.API;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;

import Back.DB.ChatDB;
import Back.DB.FriendDB;
import Back.DB.UsersDB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class SignupHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            String id = data.get("id");
            String pw = data.get("pw");
            String nName = data.get("nName");
            UsersDB usersDB = new UsersDB();
            
            String response = "";
            if (usersDB.isUser(id)) {
                response = "User exists";
                exchange.sendResponseHeaders(999, response.length());
            } else {
                usersDB.addUser(id, pw, nName);
                response = "Success";
                exchange.sendResponseHeaders(1, response.length());
            }

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e) {
            System.out.println("InfoServerHost.SignupHandler Err!");
            e.printStackTrace();
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
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            String id = data.get("id");
            String pw = data.get("pw");
            UsersDB usersDB = new UsersDB();


            if (!usersDB.isUser(id)) {
                exchange.sendResponseHeaders(999, -1);
                exchange.close();
                return;
            }
            
            String DB_pw = usersDB.getUserPassword(id);
            if (DB_pw.equals(pw)) {
                String nName = usersDB.getUserNName(id);
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
        } finally {
            exchange.close();
        }
    }
}

class GetNNameHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            UsersDB usersDB = new UsersDB();
            String nName = usersDB.getUserNName(id);

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
        } finally {
            exchange.close();
        }
    }
}

class GetChatListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String user_id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            ChatDB chatDB = new ChatDB();
            String chat_list = chatDB.getChatList(user_id);

            byte[] payload = chat_list.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.GetChatListHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetFriendListHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String user_id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            
            FriendDB friendDB = new FriendDB();
            String friend_list = friendDB.getFriend(user_id);

            byte[] payload = friend_list.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.GetFriendListHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetFriendRequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            
            FriendDB friendDB = new FriendDB();
            String friend_request = friendDB.getFriendRequest(id);

            byte[] payload = friend_request.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("InfoServerHost.GetFriendRequestHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class FriendRequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            String from_id = data.get("from_id");
            String to_id = data.get("to_id");
            
            FriendDB friendDB = new FriendDB();
            if (data.get("flag").equals("true")) friendDB.addFriendRequest(from_id, to_id);
            else friendDB.deleteFriendRequest(from_id, to_id);

            exchange.sendResponseHeaders(1,-1);
        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendRequestHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class FriendRequestResponseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            String my_id = data.get("my_id");
            String your_id = data.get("your_id");
            
            FriendDB friendDB = new FriendDB();
            if (data.get("flag").equals("true")) friendDB.acceptFriendRequest(your_id, my_id);
            else friendDB.rejectFriendRequest(your_id, my_id);

            exchange.sendResponseHeaders(1,-1);
        } catch (Exception e) {
            System.out.println("InfoServerHost.FriendRequestResponseHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

public class InfoServerHost {
    private static final int port = 8003;
    
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
        server.createContext("/GetNName", new GetNNameHandler());
        server.createContext("/GetChatList", new GetChatListHandler());
        server.createContext("/GetFriendList", new GetFriendListHandler());
        server.createContext("/GetFriendRequest", new GetFriendRequestHandler());
        server.createContext("/FriendRequest", new FriendRequestHandler());
        server.createContext("/FriendRequestResponse", new FriendRequestResponseHandler());
        
        server.setExecutor(null);
        server.start();

        System.out.printf("Info server starting on htaeky.iptime.org : %d\n", port);
    }
}
