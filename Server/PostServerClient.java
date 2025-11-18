package Server;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PostServerClient {
    static private String hostIp = IP.ip;
    static int port = 8005;

    public String [] GetReadablePostRequest(String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetReadablePost";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            // connect.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Gson gson = new Gson();
            String[] readable_post = gson.fromJson(response.toString(), String[].class);
            
            br.close();
            connect.disconnect();
            return readable_post;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.GetReadablePostRequest Err!");
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> GetPostRequest(String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetPost";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            connect.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Gson gson = new Gson();
            Map<String, Object> post = gson.fromJson(response.toString(), new TypeToken<Map<String, Object>>(){}.getType());

            br.close();
            connect.disconnect();
            return post;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.GetPostRequest Err!");
            return null;
        }   
    }

    public void AddCommentRequest(String post_id, String id, String text) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/AddComment";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"post_id\":\"" + post_id + "\", \"id\":\""+ id + "\", \"text\":\"" + text + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.AddCommentRequest Err!");
            e.printStackTrace();
            return;
        }
    }

    public void DeleteCommentRequest(String post_id, String comment_id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/DeleteComment";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"post_id\":\"" + post_id + "\", \"comment_id\":\"" + comment_id + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.DeleteCommentRequest Err!");
            e.printStackTrace();
            return;
        }
    }    

    public void LikeRequest(String post_id, String id, String flag) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/Like";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"post_id\":\"" + post_id + "\", \"id\":\""+ id + "\", \"flag\":\"" + flag + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.LikeRequest Err!");
            e.printStackTrace();
            return;
        }
    }

    public void AddPostRequest(String user_id, String content, String img) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/AddPost";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            );

            String json = "{\"user_id\":\"" + user_id + "\", \"content\":\"" + content + "\", \"timestamp\":\"" + timestamp + "\", \"image\":\"" + img + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.AddPostRequest Err!");
            e.printStackTrace();
            return;
        }
    }

        public void DeletePostRequest(String post_id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/DeletePost";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(post_id.getBytes(StandardCharsets.UTF_8));
            }        

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("PostServerClient.DeletePostRequest Err!");
            e.printStackTrace();
            return;
        }
    }

    public PostServerClient() {
        try {    
            String urlStr = "http://" + hostIp + ":" + port + "/test";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
                        
            BufferedReader br = new BufferedReader(
                new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            System.out.println(response.toString());

            br.close();
            connect.disconnect();
        } catch (IOException e) {
            System.out.println("PostServerClient ConErr!");
            e.printStackTrace();
        }
    }
}
