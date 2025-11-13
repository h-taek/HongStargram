package Json;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.*;
import java.util.*;


public class Json {

    private final Path path;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Json(String filename) {
        this.path = Paths.get(filename);
    }

    public synchronized JsonObject loadOrCreate() throws IOException {
        if (!Files.exists(path)) {
            JsonObject root = new JsonObject();
            save(root);
            return root;
        }
        String json = Files.readString(path);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public synchronized void save(JsonObject root) throws IOException {
        Files.writeString(path, gson.toJson(root),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public synchronized void addUser(String id, String pw, String nName) throws IOException {
        JsonObject root = loadOrCreate();

        JsonObject u = new JsonObject();
        u.addProperty("id", id);
        u.addProperty("pw", pw);
        u.addProperty("nName", nName);
        u.add("friend", new JsonArray());
        u.add("friend_add", new JsonArray());
        u.add("friend_request", new JsonArray());
        u.add("post", new JsonArray());
        u.add("readable_post", new JsonArray());

        root.add(id, u); // 키를 id로 저장: "users": { "id": { ... } }
        save(root);
    }

    public synchronized boolean getUser(String id) throws IOException {
        JsonObject root = loadOrCreate();
        return root.has(id) ? true : false;
    }

    public synchronized String [] getUserInfo(String id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject temp = root.getAsJsonObject(id);

        String [] info = {temp.get("pw").getAsString(), temp.get("nName").getAsString()};
        return info;
    }

    public synchronized String getUserNName(String id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject temp = root.getAsJsonObject(id);

        if (temp == null || !temp.has("nName")) {
            return null; // 또는 "unknown" 등 기본값 반환
        }

        String nName = temp.get("nName").getAsString();
        return nName;
    }

    public synchronized void addChat(String sender, String receiver, String msg) throws IOException{
        JsonObject root = loadOrCreate();

        JsonObject chat = new JsonObject();
        chat.addProperty("sender", sender);
        chat.addProperty("msg", msg);

        JsonArray chat_arr = root.getAsJsonArray(receiver);
        if (chat_arr == null) {
            chat_arr = new JsonArray();
            root.add(receiver, chat_arr);
        }

        chat_arr.add(chat);

        save(root);
    }

    public synchronized String getChatList(String id) throws IOException{
        JsonObject root = loadOrCreate();
        Set<String> temp = root.keySet();
        String keys = gson.toJson(temp);
        return keys;
    }

    public synchronized String getChat(String receiver) throws IOException{        
        JsonObject root = loadOrCreate();

        JsonArray temp = root.getAsJsonArray(receiver);
        List<Map<String, String>> chat_arr = gson.fromJson(
            temp, new TypeToken<List<Map<String, String>>>(){}.getType());  

        String chat_log = gson.toJson(chat_arr).replace("\n", "").replace("\r", "");
        return chat_log;
    }

    public synchronized void friendAdd(String sender, String receiver, boolean flag) throws IOException {
        JsonObject root = loadOrCreate();
        
        if(flag) {    
            JsonObject temp = root.getAsJsonObject(receiver);
            JsonArray request_list = temp.getAsJsonArray("friend_request");
            request_list.add(sender);
            temp.add("friend_request", request_list);

            JsonObject temp2 = root.getAsJsonObject(sender);
            JsonArray add_list = temp2.getAsJsonArray("friend_add");
            add_list.add(receiver);
            temp2.add("friend_add", add_list);
        }
        else{
            JsonObject temp = root.getAsJsonObject(receiver);
            JsonArray request_list = temp.getAsJsonArray("friend_request");
            for (int i = 0; i < request_list.size(); i++) {
                if (request_list.get(i).getAsString().equals(sender)){
                    request_list.remove(i);
                    break;
                }
            }
            temp.add("friend_request", request_list);
            
            temp = root.getAsJsonObject(sender);
            JsonArray add_list = temp.getAsJsonArray("friend_add");
            for (int i = 0; i < add_list.size(); i++) {
                if (add_list.get(i).getAsString().equals(receiver)){
                    add_list.remove(i);
                    break;
                }
            }            
            temp.add("friend_add", add_list);
        }
        
        save(root);
    }

    public synchronized void friendRequest(String my_id, String your_id, boolean flag) throws IOException {
        JsonObject root = loadOrCreate();
        
        if(flag) {    
            JsonObject temp = root.getAsJsonObject(my_id);
            JsonArray friend_list = temp.getAsJsonArray("friend");
            friend_list.add(your_id);
            temp.add("friend", friend_list);
            
            JsonObject temp2 = root.getAsJsonObject(your_id);
            friend_list = temp2.getAsJsonArray("friend");
            friend_list.add(my_id);
            temp2.add("friend", friend_list);

            JsonArray my_readabel_post = temp.getAsJsonArray("readable_post");
            JsonArray your_readabel_post = temp2.getAsJsonArray("readable_post");
            JsonArray my_post = temp.getAsJsonArray("post");
            JsonArray your_post = temp2.getAsJsonArray("post");

            // readable_post 업데이트
            for (int i = 0; i < my_post.size(); i++) {
                String post_id = my_post.get(i).getAsString();
                if (!your_readabel_post.contains(new JsonPrimitive(post_id))) {
                    your_readabel_post.add(post_id);
                }
            }

            // readable_post 시간순 정렬
            Map<String, Integer> map = new HashMap<>();
            for (int i = 0; i < your_readabel_post.size(); i++) {
                Json post = new Json(".user_data/post.json");
                JsonObject root_post = post.loadOrCreate();
                String post_id = your_readabel_post.get(i).getAsString();
                int timestamp = root_post.getAsJsonObject(post_id).getAsJsonObject("timestamp").getAsInt();
                
                map.put(post_id, timestamp);
            }
            List<Map.Entry<String, Integer>> entryList = new ArrayList<>(map.entrySet());
            entryList.sort(Map.Entry.comparingByValue());
            
            your_readabel_post = new JsonArray();
            for (Map.Entry<String, Integer> entry : entryList) {
                your_readabel_post.add(entry.getKey());
            }
            map.clear();
            entryList.clear();
            
            for (int i = 0; i < your_post.size(); i++) {
                String post_id = your_post.get(i).getAsString();
                if (!my_readabel_post.contains(new JsonPrimitive(post_id))) {
                    my_readabel_post.add(post_id);  
                }
            }
            for (int i = 0; i < my_readabel_post.size(); i++) {
                Json post = new Json(".user_data/post.json");
                JsonObject root_post = post.loadOrCreate();
                String post_id = my_readabel_post.get(i).getAsString();
                int timestamp = root_post.getAsJsonObject(post_id).getAsJsonObject("timestamp").getAsInt();
                
                map.put(post_id, timestamp);
            }
            entryList = new ArrayList<>(map.entrySet());
            entryList.sort(Map.Entry.comparingByValue());

            my_readabel_post = new JsonArray();
            for (Map.Entry<String, Integer> entry : entryList) {
                my_readabel_post.add(entry.getKey());
            }

            temp.add("readable_post", my_readabel_post);
            temp2.add("readable_post", your_readabel_post);
        }
        
        JsonObject temp = root.getAsJsonObject(my_id);
        JsonArray request_list = temp.getAsJsonArray("friend_request");
        for (int i = 0; i < request_list.size(); i++) {
            if (request_list.get(i).getAsString().equals(your_id)){
                request_list.remove(i);
                break;
            }
        }
        temp.add("friend_request", request_list);
        
        temp = root.getAsJsonObject(your_id);
        JsonArray add_list = temp.getAsJsonArray("friend_add");
        for (int i = 0; i < add_list.size(); i++) {
            if (add_list.get(i).getAsString().equals(my_id)){
                add_list.remove(i);
                break;
            }
        }            
        temp.add("friend_add", add_list);

        save(root);
    }

    public synchronized String getFriend(String id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject temp = root.getAsJsonObject(id);
        JsonArray friend = temp.getAsJsonArray("friend");
        if (friend == null) {return null;}

        return gson.toJson(friend);
    }

    public synchronized String getFriendStatus(String id) throws IOException {
        JsonObject friend_status = new JsonObject();

        JsonObject root = loadOrCreate();
        JsonObject temp = root.getAsJsonObject(id);

        JsonArray friend_add = temp.getAsJsonArray("friend_add");
        friend_status.add("friend_add", friend_add);
        JsonArray friend_request = temp.getAsJsonArray("friend_request");
        friend_status.add("friend_request", friend_request);

        return gson.toJson(friend_status).replace("\n", "").replace("\r", "");
    }

    public synchronized String getReadablePostList(String id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject temp = root.getAsJsonObject(id);
        JsonArray post_list = temp.getAsJsonArray("readable_post");

        return gson.toJson(post_list).replace("\n", "").replace("\r", "");
    }

    public synchronized String getPost(String post_id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject post = root.getAsJsonObject(post_id);

        File img = new File(post.get("image_path").getAsString());
        String encodedImage = Base64.getEncoder().encodeToString(Files.readAllBytes(img.toPath()));

        post.remove("image_path");
        post.addProperty("image", encodedImage);
        
        return gson.toJson(post).replace("\n", "").replace("\r", "");
    }

    public synchronized void addComment(String post_id, String id, String text) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject post = root.getAsJsonObject(post_id);

        int comment_id = Integer.valueOf(root.get("comment_id").getAsString()) + 1;
        JsonArray comments = post.getAsJsonArray("comments");

        JsonObject comment = new JsonObject();
        comment.addProperty("id", id);
        comment.addProperty("comment", text);
        comment.addProperty("comment_id", comment_id+"");

        comments.add(comment);
        root.addProperty("comment_id", comment_id+"");

        save(root);
    }

    public synchronized void deleteComment(String post_id, String comment_id) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject post = root.getAsJsonObject(post_id);
        JsonArray comments = post.getAsJsonArray("comments");

        for (int i = 0; i < comments.size(); i++){
            JsonObject comment = comments.get(i).getAsJsonObject();
            if (comment.get("comment_id").getAsString().equals(comment_id)){
                comments.remove(i);
                break;
            }
        }

        save(root);
    }

    public synchronized void like(String post_id, String id, boolean flag) throws IOException {
        JsonObject root = loadOrCreate();
        JsonObject post = root.getAsJsonObject(post_id);
        JsonArray likes = post.getAsJsonArray("likes");

        if (flag) {likes.add(id);}
        else {
            for (int i = 0; i < likes.size(); i++){
                if (likes.get(i).getAsString().equals(id)) {
                    likes.remove(i);
                }
            }
        }

        save(root);
    }
}
