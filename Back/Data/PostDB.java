package Back.Data;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PostDB {
    //getPost
    public String getPost(int post_id) {
        String sql_post = "SELECT * FROM POSTS WHERE POST_ID = ?";
        String sql_comment = "SELECT * FROM COMMENTS WHERE POST_ID = ?";
        String sql_like = "SELECT * FROM POST_LIKES WHERE POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_post = conn.prepareStatement(sql_post);
             PreparedStatement psmt_comment = conn.prepareStatement(sql_comment);
             PreparedStatement psmt_like = conn.prepareStatement(sql_like)) {
            
            Map<String, Object> post = new HashMap<>();                 

            // Post Info
            psmt_post.setInt(1, post_id);
            ResultSet rs_post = psmt_post.executeQuery();
            
            ResultSetMetaData meta = rs_post.getMetaData();
            int cnt_post = meta.getColumnCount();
            while (rs_post.next()) {
                for (int i = 1; i <= cnt_post; i++) {
                    String columnName = meta.getColumnLabel(i);  
                    Object value = rs_post.getObject(i);         
                    post.put(columnName, value);
                }
            }

            // Image
            File file = new File(post.get("IMAGE_PATH").toString());
            String encodedImg = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            post.remove("IMAGE_PATH");
            post.put("IMAGE", encodedImg);

            
            // Comments
            psmt_comment.setInt(1, post_id);
            ResultSet rs_comment = psmt_comment.executeQuery();
            List<Map<String, Object>> comments = new ArrayList<>();
            ResultSetMetaData commentMeta = rs_comment.getMetaData();
            int cnt_comment = commentMeta.getColumnCount();
            while (rs_comment.next()) {
                Map<String, Object> comment = new HashMap<>();
                for (int i = 1; i <= cnt_comment; i++) {
                    String columnName = commentMeta.getColumnLabel(i);
                    Object value = rs_comment.getObject(i);
                    comment.put(columnName, value);
                }
                comments.add(comment);
            }
            post.put("COMMENTS", comments);

            // Likes
            psmt_like.setInt(1, post_id);
            ResultSet rs_like = psmt_like.executeQuery();
            List<String> likes = new ArrayList<>();
            while (rs_like.next()) {
                String user_id = rs_like.getString("USER_ID");
                likes.add(user_id);
            }
            post.put("LIKES", likes);


            Gson gson = new Gson();
            return gson.toJson(post);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getPost SQL Error");

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("getPost IO Error");

            return null;
        }
    }
    
    //getPostList
    public String getPostList(String user_id) {
        String sql = "SELECT POST_ID FROM POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
             
            psmt.setString(1, user_id);
            ResultSet rs = psmt.executeQuery();
            List<Integer> post_ids = new ArrayList<>();
            while (rs.next()) {                
                int post_id = rs.getInt("POST_ID");
                post_ids.add(post_id);
            }

            Gson gson = new Gson();
            return gson.toJson(post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getPostList Error");

            return null;
        }
    }

    //sortPostList
    public String sortPostList(String post_ids) {
        String sql = "SELECT POST_ID FROM POSTS WHERE POST_ID IN (" + post_ids + ") ORDER BY CREATED_AT DESC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
             
            ResultSet rs = psmt.executeQuery();
            List<Integer> sorted_post_ids = new ArrayList<>();
            while (rs.next()) {                
                int post_id = rs.getInt("POST_ID");
                sorted_post_ids.add(post_id);
            }

            Gson gson = new Gson();
            return gson.toJson(sorted_post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("sortPostList Error");

            return null;
        }
    }

    //getReadablePostList
    public String getReadablePostList(String user_id) {
        String sql = "SELECT POST_ID FROM USER_READABLE_POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
             
            psmt.setString(1, user_id);
            ResultSet rs = psmt.executeQuery();
            List<String> post_ids = new ArrayList<>();
            while (rs.next()) {                
                String post_id = rs.getString("POST_ID");
                post_ids.add(post_id);
            }

            Gson gson = new Gson();
            return gson.toJson(post_ids);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getReadablePostList Error");

            return null;
        }
    }

    //addReadablePost
    public void addReadablePost(String user_id, int post_id) {
        String sql = "INSERT INTO USER_READABLE_POSTS (USER_ID, POST_ID) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)){
            
            psmt.setString(1, user_id);
            psmt.setInt(2, post_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addReadablePost Error");
        }
    }
    
    //deleteReadablePost
    public void deleteReadablePost(String user_id, int post_id) {
        String sql = "DELETE FROM USER_READABLE_POSTS WHERE USER_ID = ? AND POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)){
            
            psmt.setString(1, user_id);
            psmt.setInt(2, post_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteReadablePost Error");
        }
    }

    //deleteAllReadablePost
    public void deleteAllReadablePost(String user_id) {
        String sql = "DELETE FROM USER_READABLE_POSTS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)){
            
            psmt.setString(1, user_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteAllReadablePost Error");
        }
    }

    //addPost
    public void addPost(String post) {
        String sql_get = "SELECT MAX(POST_ID) AS POST_ID FROM POSTS";
        String sql_add = "INSERT INTO POSTS (POST_ID, USER_ID, CONTENT, IMAGE_PATH, CREATED_AT) VALUES (?, ?, ?, ?, SYSDATE)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get = conn.prepareStatement(sql_get);
             PreparedStatement psmt_add = conn.prepareStatement(sql_add)){
            
            Gson gson = new Gson();
            Map<String, String> postMap = gson.fromJson(post, new TypeToken<Map<String, String>>(){}.getType());

            ResultSet rs = psmt_get.executeQuery();
            int post_id = rs.getInt("POST_ID") + 1;

            String encodedImg = postMap.get("image");
            byte[] decodedImg = Base64.getDecoder().decode(encodedImg);
            String imagePath = "./image/img_" + post_id + ".jpg";
            try (FileOutputStream fos = new FileOutputStream(imagePath)) {fos.write(decodedImg);}

            psmt_add.setInt(1, post_id);                
            psmt_add.setString(2, postMap.get("user_id"));
            psmt_add.setString(3, postMap.get("content"));
            psmt_add.setString(4, imagePath);
            
            psmt_add.executeUpdate();

            // Add readable post to friends
            String friends = new FriendDB().getFreind(postMap.get("user_id"));
            List<String> friendList = gson.fromJson(friends, new TypeToken<List<String>>(){}.getType());
            for (String friend_id : friendList) {
                addReadablePost(friend_id, post_id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addPost SQL Error");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("addPost IO Error");
        }
    }

    //deletePost
    public void deletePost(int post_id) {
        String sql_get = "SELECT USER_ID FROM POSTS WHERE POST_ID = ?";
        String sql_delete = "DELETE FROM POSTS WHERE POST_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement psmt_get = conn.prepareStatement(sql_get);
            PreparedStatement psmt_delete = conn.prepareStatement(sql_delete)){
            
            psmt_get.setInt(1, post_id);
            ResultSet rs = psmt_get.executeQuery();
            String user_id = rs.getString("USER_ID");

            psmt_delete.setInt(1, post_id);

            psmt_delete.executeUpdate();

            // Delete readable posts
            String friends = new FriendDB().getFreind(user_id);
            List<String> friendList = new Gson().fromJson(friends, new TypeToken<List<String>>(){}.getType());
            for (String friend_id : friendList) {
                deleteReadablePost(friend_id, post_id);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deletePost Error");
        }
    }
        
    //addComment
    public void addComment(int post_id, String user_id, String content) {
        String sql_add = "INSERT INTO COMMENTS (COMMENT_ID, POST_ID, USER_ID, CONTENT) VALUES (?, ?, ?, ?)";
        String sql_get = "SELECT MAX(COMMENT_ID) AS COMMENT_ID FROM COMMENTS";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get = conn.prepareStatement(sql_get);
             PreparedStatement psmt_add = conn.prepareStatement(sql_add)){
            
            ResultSet rs = psmt_get.executeQuery();
            int comment_id = rs.getInt("COMMENT_ID") + 1;
            
            psmt_add.setInt(1, comment_id);                
            psmt_add.setInt(2, post_id);
            psmt_add.setString(3, user_id);
            psmt_add.setString(4, content);
            
            psmt_add.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addComment Error");
        }
    }

    //deleteCommnet
    public void deleteComment(int comment_id) {
        String sql = "DELETE FROM COMMENTS WHERE COMMENT_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement psmt = conn.prepareStatement(sql)){
            
            psmt.setInt(1, comment_id);

            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteComment Error");
        }
    }

    //like
}
