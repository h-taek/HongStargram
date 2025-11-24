package Back.Data;

import java.util.*;

import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FriendDB {
    //getFriend
    public String getFriend(String id) {
        String sql = "SELECT * FFROM FRIENDS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            List<String> friends = new ArrayList<>();
            while (rs.next()) {                
                String value = rs.getString(1);
                friends.add(value);
            }

            Gson gson = new Gson();
            return gson.toJson(friends);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getFriend Error");

            return null;
        }
    }

    //addFriendRequest
    public void addFriendRequest(String from_id, String to_id) {
        String sql = "INSERT INTO FRIEND_REQUESTS (FROM_USER_ID, TO_USER_ID) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, from_id);
            pstmt.setString(2, to_id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addFriendRequest Error");
        }
    }

    //deleteFriendRequest
    public void deleteFriendRequest(String from_id, String to_id) {
        String sql = "DELETE FROM FRIEND_REQUESTS WHERE FROM_USER_ID = ? AND TO_USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, from_id);
            pstmt.setString(2, to_id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteFriendRequest Error");
        }
    }

    //getFriendRequest
    public String getFriendRequest(String id) {
        String sql_receive = "SELECT FROM_USER_ID FROM FRIEND_REQUESTS WHERE FROM_USER_ID = ?";
        String sql_sent = "SELECT TO_USER_ID FROM FRIEND_REQUESTS WHERE TO_USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt_receive = conn.prepareStatement(sql_receive);
            PreparedStatement pstmt_send = conn.prepareStatement(sql_sent)) {
            
            Map<String, List<String>> requests = new HashMap<>();

            pstmt_receive.setString(1, id);
            ResultSet rs_receive = pstmt_receive.executeQuery();
            List<String> receive_requests = new ArrayList<>();
            while (rs_receive.next()) {                
                String value = rs_receive.getString("FROM_USER_ID");
                receive_requests.add(value);
            }
            requests.put("receive_request", receive_requests);

            pstmt_send.setString(1, id);
            ResultSet rs_send = pstmt_send.executeQuery();
            List<String> sent_requests = new ArrayList<>();
            while (rs_send.next()) {                
                String value = rs_send.getString("TO_USER_ID");
                sent_requests.add(value);
            }
            requests.put("sent_request", sent_requests);

            Gson gson = new Gson();
            return gson.toJson(requests);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getFriendRequest Error");

            return null;
        }
    }

    //acceptFriendRequest
    public void acceptFriendRequest(String from_id, String to_id) {
        String sql_delete = "DELETE FROM FRIEND_REQUESTS WHERE FROM_USER_ID = ? AND TO_USER_ID = ?";
        String sql_insert = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt_delete = conn.prepareStatement(sql_delete);
            PreparedStatement pstmt_insert = conn.prepareStatement(sql_insert)) {
            
            // Delete the friend request
            pstmt_delete.setString(1, from_id);
            pstmt_delete.setString(2, to_id);
            pstmt_delete.executeUpdate();

            // Insert into friends for both users
            pstmt_insert.setString(1, from_id);
            pstmt_insert.setString(2, to_id);
            pstmt_insert.executeUpdate();

            pstmt_insert.setString(1, to_id);
            pstmt_insert.setString(2, from_id);
            pstmt_insert.executeUpdate();


            // Add readable posts 
            // from_id
            PostDB PostDB = new PostDB();

            String old_readable_posts_str = PostDB.getReadablePostList(from_id);
            String add_readable_psots_str = PostDB.getPostList(to_id);
            Gson gson = new Gson();
            List<Integer> post_ids_int_list = gson.fromJson(old_readable_posts_str, new TypeToken<List<Integer>>(){}.getType());
            post_ids_int_list.addAll(gson.fromJson(add_readable_psots_str, new TypeToken<List<Integer>>(){}.getType()));
                
            List<String> post_ids_str_list = post_ids_int_list.stream().map(Object::toString).toList();
            String post_ids_str = String.join(",", post_ids_str_list);
            
            String sorted_post_ids_str = PostDB.sortPostList(post_ids_str);
            List<Integer> sorted_post_ids = gson.fromJson(sorted_post_ids_str, new TypeToken<List<Integer>>(){}.getType());
            PostDB.deleteAllReadablePost(from_id);
            for (int post_id : sorted_post_ids) {
                PostDB.addReadablePost(from_id, post_id);
            }

            // to_id
            old_readable_posts_str = PostDB.getReadablePostList(to_id);
            add_readable_psots_str = PostDB.getPostList(from_id);
            post_ids_int_list = gson.fromJson(old_readable_posts_str, new TypeToken<List<Integer>>(){}.getType());
            post_ids_int_list.addAll(gson.fromJson(add_readable_psots_str, new TypeToken<List<Integer>>(){}.getType()));

            post_ids_str_list = post_ids_int_list.stream().map(Object::toString).toList();
            post_ids_str = String.join(",", post_ids_str_list);

            sorted_post_ids_str = PostDB.sortPostList(post_ids_str);
            sorted_post_ids = gson.fromJson(sorted_post_ids_str, new TypeToken<List<String>>(){}.getType());
            PostDB.deleteAllReadablePost(to_id);
            for (int post_id : sorted_post_ids) {
                PostDB.addReadablePost(to_id, post_id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("acceptFriendRequest Error");
        }
    }

    //rejectFriendRequest
    public void rejectFriendRequest(String from_id, String to_id) {
        String sql = "DELETE FROM FRIEND_REQUESTS WHERE FROM_USER_ID = ? AND TO_USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            
            pstmt.setString(1, from_id);
            pstmt.setString(2, to_id);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("rejectFriendRequest Error");
        }
    }
}
