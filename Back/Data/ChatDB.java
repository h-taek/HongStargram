package Back.Data;

import java.util.*;

import java.sql.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChatDB {
    //addChatRoom
    public int addNewChatRoom(String user_ids) {
        String sql_get_room = "SELECT MAX(ROOM_ID) AS ROOM_ID FROM CHAT_ROOMS";
        String sql_add_room = "INSERT INTO CHAT_ROOMS (ROOM_ID) VALUES (?)";
        String sql_member = "INSERT INTO CHAT_ROOM_MEMBERS (ROOM_ID, USER_ID) VALUES (?, ?)";

        Gson gson = new Gson();
        List<String>user_id_list = gson.fromJson(user_ids, new TypeToken<List<String>>(){}.getType());

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_get_room = conn.prepareStatement(sql_get_room);
             PreparedStatement psmt_add_room = conn.prepareStatement(sql_add_room);
             PreparedStatement psmt_member = conn.prepareStatement(sql_member)) {
            
            psmt_get_room.executeQuery();
            ResultSet rs = psmt_get_room.getResultSet();
            int room_id = rs.getInt("ROOM_ID");

            psmt_add_room.setInt(1, room_id);
            psmt_add_room.executeUpdate();

            for (String user : user_id_list) {
                psmt_member.setInt(1, room_id);
                psmt_member.setString(2, user);
                psmt_member.executeUpdate();
            }

            return room_id;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addChatRoom Error");
            return -1;
        }
    }

    //addChat
    public void addChat(int chat_id, String user_id, String msg) {
        String sql = "INSERT INTO CHAT_MESSAGES (ROOM_ID, SENDER_ID, MESSAGE, SENT_AT) VALUES (?, ?, ?, SYSDATE)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, chat_id);
            psmt.setString(2, user_id);
            psmt.setString(3, msg);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addChat Error");
        }
    }

    //getChatList
    public String getChatList(String user_id) {
        String sql_room = "SELECT * FROM CHAT_ROOMS WHERE USER_ID = ?";
        String sql_member = "SELECT * FROM CHAT_ROOM_MEMBERS WHERE WHERE ROOM_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt_room = conn.prepareStatement(sql_room);
             PreparedStatement psmt_member = conn.prepareStatement(sql_member)) {
            
            psmt_room.setString(1, user_id);
            ResultSet rs_room = psmt_room.getResultSet();

            List<Integer> room_id_list = new ArrayList<>();
            while (rs_room.next()) {
                room_id_list.add(rs_room.getInt("ROOM_ID"));
            }

            Map<Integer, List<String>> chat_member_list = new HashMap<>();
            for (Integer room_id : room_id_list) {
                psmt_member.setInt(1, room_id);
                ResultSet rs_member = psmt_member.getResultSet();
                List<String> member_list = new ArrayList<>();
                while (rs_member.next()) {
                    member_list.add(rs_member.getString("USER_ID"));
                }
                chat_member_list.put(room_id, member_list);
            }

            Gson gson = new Gson();
            return gson.toJson(chat_member_list);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getChatList Error");
            return null;
        }
    }
            
    //getChat
    public String getChat(int chat_id) {
        String sql = "SELECT SENDER_ID, MESSAGE FROM CHAT_MESSSAGES WHERE ROOM_ID = ? ORDER BY SENT_AT ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            
            psmt.setInt(1, chat_id);
            ResultSet rs = psmt.getResultSet();

            List<Map<String, String>> chat_list = new ArrayList<>();
            while (rs.next()) {
                Map<String, String> chat = new HashMap<>();
                chat.put("sender_id", rs.getString("SENDER_ID"));
                chat.put("message", rs.getString("MESSAGE"));
                chat_list.add(chat);
            }

            Gson gson = new Gson();
            return gson.toJson(chat_list);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getChat Error");
            return null;
        }
    }
}
