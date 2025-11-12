package Server;

import com.sun.net.httpserver.*;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import com.google.gson.*;

import Json.Json;
import java.nio.charset.StandardCharsets;

class GetReadablePostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Json store = new Json(".user_data/user.json");
            String readable_post = store.getReadablePostList(id);
            
            byte[] payload = readable_post.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("PostServerHost.GetReadablePostHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class GetPostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String post_id = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            
            Json store = new Json(".user_data/post.json");
            String post = store.getPost(post_id);

            byte[] payload = post.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("PostServerHost.GetPostHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

public class PostServerHost {
    private static final int port = 8002;

    private static String getLoclaIp() throws IOException {
        try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
            socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        }
    }
    
    public PostServerHost() throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/test", exchange -> {
            String response = "Post server is connected";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });
        server.createContext("/GetReadablePost", new GetReadablePostHandler());
        server.createContext("/GetPost", new GetPostHandler());
        

        server.setExecutor(null);
        server.start();

        String host_ip = getLoclaIp();
        System.out.printf("Post server starting on %s : %d\n", host_ip, port);
    }
}
