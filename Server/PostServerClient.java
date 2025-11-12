package Server;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PostServerClient {
    static private String hostIp = IP.ip;
    static int port = 8002;

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
