package Front.Server;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class InfoServerClient {
    static private String hostIp = IP.ip;
    static int port = 8003;

    public int SignupRequest(String id, String pw, String nName) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/Signup";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"id\":\""+ id + "\", \"pw\":\"" + pw + "\", \"nName\":\"" + nName + "\"}";

            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            
            int code = connect.getResponseCode();
        
            connect.disconnect();
            return code;    
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("ServerClient.SignupRequest Err!");
            return -1;
        }
        
    }

    public String LoginRequest(String id, String pw) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/Login";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"id\":\""+ id + "\", \"pw\":\"" + pw + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = connect.getResponseCode();
            String nName;
            if (code == 999) {
                nName = null;
            }
            else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                nName = response.toString();
                br.close();
            }

            connect.disconnect();
            return nName;    
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ServerClient.LoginRequest Err!");
            return null;
        }
    }

    public String GetNNameRequest(String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetNName";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            int code = connect.getResponseCode();
            String nName;
            if (code == 999) {
                nName = null;
            }
            else {
                BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                
                nName = response.toString();
                br.close();
            }
            
            connect.disconnect();
            return nName;    
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetNNameRequest Err!");
            return null;
        }
    }    

    public Map<Integer, List<String>> GetChatListRequest(String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetChatList";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            Gson gson = new Gson();
            Map<Integer, List<String>> chat_list = gson.fromJson(response.toString(), new TypeToken<Map<Integer, List<String>>>(){}.getType());

            br.close();
            connect.disconnect();
            return chat_list;    
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetChatListRequest Err!");
            return null;
        }
    }
        
    public String [] GetFriendListRequest (String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetFriendList";
            
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            // System.out.println(response);
            Gson gson = new Gson();
            String [] friend_list = gson.fromJson(response.toString(), String[].class);

            br.close();
            connect.disconnect();
            return friend_list;    
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetFriendListRequest Err!");
            return null;
        }
    }

    public Map<String, String []> GetFriendRequestRequest (String id) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/GetFriendRequest";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }        
            
            BufferedReader br = new BufferedReader(new InputStreamReader(connect.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            Gson gson = new Gson();
            Map<String, String []> friend_request_response = gson.fromJson(response.toString(), new TypeToken<Map<String, String []>>(){}.getType());


            br.close();
            connect.disconnect();
            return friend_request_response;    
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetFriendRequestRequest Err!");
            return null;
        }
    }

    public void FriendRequestRequest(String from_id, String to_id, boolean flag) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/FriendRequest";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String flag_str = (flag) ? "true" : "false";

            String json = "{\"from_id\":\""+ from_id + "\", \"to_id\":\"" + to_id + "\", \"flag\":\"" + flag_str + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("InfoServerClient.FriendRequestRequest Err!");
            e.printStackTrace();
            return;
        }
    }

    public void FriendRequestResponseRequest(String my_id, String your_id, boolean flag) {
        try{
            String urlStr = "http://" + hostIp + ":" + port + "/FriendRequestResponse";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();
            
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String flag_str = flag ? "true" : "false";

            String json = "{\"my_id\":\""+ my_id + "\", \"your_id\":\"" + your_id + "\", \"flag\":\"" + flag_str + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            connect.getResponseCode();
            connect.disconnect();
            return;    
        }
        catch (Exception e) {
            System.out.println("ServerClient.FriendRequestResponseRequest Err!");
            e.printStackTrace();
            return;
        }
    }

    public InfoServerClient() {
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
            e.printStackTrace();
            System.out.println("InfoServerClient ConErr!");
        }
    }
}
