package Back.API;

import com.sun.net.httpserver.*;

import Back.Data.Json;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import com.google.gson.*;

import java.nio.charset.StandardCharsets;

class GetReadablePostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Json store = new Json("Back/.user_data/user.json");
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
            
            Json store = new Json("Back/.user_data/post.json");
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

class AddCommentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String post_id = data.get("post_id");
            String id = data.get("id");
            String text = data.get("text");
            Json store = new Json("Back/.user_data/post.json");

            store.addComment(post_id, id, text);;
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddCommentHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class DeleteCommentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String post_id = data.get("post_id");
            String comment_id = data.get("comment_id");
            Json store = new Json("Back/.user_data/post.json");

            store.deleteComment(post_id, comment_id);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddCommentHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class LikeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, Map.class);

            String post_id = data.get("post_id");
            String id = data.get("id");
            boolean flag = data.get("flag").equals("true") ? true : false;
            Json store = new Json("Back/.user_data/post.json");

            store.like(post_id, id, flag);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddCommentHandler Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class AddPost implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String post = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Json store = new Json("Back/.user_data/post.json");

            store.addPost(post);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddPost Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}

class DeletePost implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String post_id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Json store = new Json("Back/.user_data/post.json");

            store.deletePost(post_id);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.DeletePost Err!");
            e.printStackTrace();
        }
        
        exchange.close();
        return;
    }
}


public class PostServerHost {
    private static final int port = 8005;

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
        server.createContext("/AddComment", new AddCommentHandler());
        server.createContext("/DeleteComment", new DeleteCommentHandler());
        server.createContext("/Like", new LikeHandler());
        server.createContext("/AddPost", new AddPost());
        server.createContext("/DeletePost", new DeletePost());
        

        server.setExecutor(null);
        server.start();

        String host_ip = getLoclaIp();
        System.out.printf("Post server starting on %s : %d\n", host_ip, port);
    }
}
