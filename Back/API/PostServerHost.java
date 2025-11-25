package Back.API;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.*;

import Back.DB.PostDB;

import com.google.gson.reflect.TypeToken;
import com.google.gson.*;

class GetReadablePostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String user_id = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            PostDB postDB = new PostDB();
            String readable_post = postDB.getReadablePostList(user_id);
            
            byte[] payload = readable_post.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("PostServerHost.GetReadablePostHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetPostHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            int post_id = Integer.parseInt(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            
            PostDB postDB = new PostDB();
            String post = postDB.getPost(post_id);

            byte[] payload = post.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }

        } catch (Exception e) {
            System.out.println("PostServerHost.GetPostHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class AddCommentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            int post_id = Integer.parseInt(data.get("post_id"));
            String user_id = data.get("user_id");
            String comment = data.get("comment");
            PostDB postDB = new PostDB();

            postDB.addComment(post_id, user_id, comment);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddCommentHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class DeleteCommentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            int comment_id = Integer.parseInt(body);

            PostDB postDB = new PostDB();
            postDB.deleteComment(comment_id);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.DeleteCommentHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class LikeHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try{
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>(){}.getType());

            int post_id = Integer.parseInt(data.get("post_id"));
            String user_id = data.get("user_id");
            
            PostDB postDB = new PostDB();
            if (data.get("flag").equals("true")) {
                postDB.addLike(post_id, user_id);
            } else {
                postDB.deleteLike(post_id, user_id);
            }
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.LikeHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class AddPost implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            String post = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            PostDB postDB = new PostDB();
            postDB.addPost(post);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.AddPost Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class DeletePost implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange){
        try{
            InputStream in = exchange.getRequestBody();
            int post_id = Integer.parseInt(new String(in.readAllBytes(), StandardCharsets.UTF_8));

            PostDB postDB = new PostDB();
            postDB.deletePost(post_id);
            exchange.sendResponseHeaders(1, -1);
        } catch (Exception e) {
            System.out.println("PostServerHost.DeletePost Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}


public class PostServerHost {
    private static final int port = 8005;
    
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

        System.out.printf("Post server starting on htaeky.iptime.org : %d\n", port);
    }
}
