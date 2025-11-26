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
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/Signup";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"id\":\"" + id + "\", \"pw\":\"" + pw + "\", \"nName\":\"" + nName + "\"}";

            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            int code = connect.getResponseCode();
            return code;
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("ServerClient.SignupRequest Err!");
            return -1;
        }
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public String LoginRequest(String id, String pw) {
        HttpURLConnection connect = null;
        try {
            String urlStr = "http://" + hostIp + ":" + port + "/Login";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String json = "{\"id\":\"" + id + "\", \"pw\":\"" + pw + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = connect.getResponseCode();
            if (code == 999) {
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            String nName = response.toString();
            return nName;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ServerClient.LoginRequest Err!");
            return null;
        }
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public String GetNNameRequest(String id) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/GetNName";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }

            int code = connect.getResponseCode();
            if (code == 999) {
                return null;
            }

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            String nName = response.toString();
            return nName;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetNNameRequest Err!");
            return null;
        }
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public Map<Integer, List<String>> GetChatListRequest(String id) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/GetChatList";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            Gson gson = new Gson();
            Map<Integer, List<String>> chat_list = gson.fromJson(response.toString(),new TypeToken<Map<Integer, List<String>>>(){}.getType());
            return chat_list;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetChatListRequest Err!");
            return null;
        } finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public String[] GetFriendListRequest(String id) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/GetFriendList";

            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            Gson gson = new Gson();
            String[] friend_list = gson.fromJson(response.toString(), String[].class);
            return friend_list;
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetFriendListRequest Err!");
            return null;
        }
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public Map<String, String[]> GetFriendRequestRequest(String id) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/GetFriendRequest";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

            try (OutputStream os = connect.getOutputStream()) {
                os.write(id.getBytes(StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            Gson gson = new Gson();
            Map<String, String[]> friend_request_response = gson.fromJson(response.toString(),new TypeToken<Map<String, String[]>>(){}.getType());
            return friend_request_response;
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.GetFriendRequestRequest Err!");
            return null;
        } 
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public void FriendRequestRequest(String from_id, String to_id, boolean flag) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/FriendRequest";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String flag_str = (flag) ? "true" : "false";

            String json = "{\"from_id\":\"" + from_id + "\", \"to_id\":\"" + to_id + "\", \"flag\":\"" + flag_str + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            connect.getResponseCode();
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("InfoServerClient.FriendRequestRequest Err!");
        } 
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public void FriendRequestResponseRequest(String my_id, String your_id, boolean flag) {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/FriendRequestResponse";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            String flag_str = flag ? "true" : "false";

            String json = "{\"my_id\":\"" + my_id + "\", \"your_id\":\"" + your_id + "\", \"flag\":\"" + flag_str + "\"}";
            try (OutputStream os = connect.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }
            connect.getResponseCode();
        } 
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("ServerClient.FriendRequestResponseRequest Err!");
        }
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }

    public InfoServerClient() {
        HttpURLConnection connect = null;

        try {
            String urlStr = "http://" + hostIp + ":" + port + "/test";
            URI uri = URI.create(urlStr);
            URL url = uri.toURL();

            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (InputStream inputStream = connect.getInputStream();
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }
            System.out.println(response.toString());
        } 
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("InfoServerClient ConErr!");
        } 
        finally {
            if (connect != null) {
                connect.disconnect();
            }
        }
    }
}
