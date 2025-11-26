package Back.DB;

import java.sql.*;

public class UsersDB {
    public void addUser(String id, String pw, String nName) {
        String sql = "INSERT INTO USERS (USER_ID, PW, NICKNAME) VALUES (?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            pstmt.setString(2, pw);
            pstmt.setString(3, nName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addUser Error");
        }
    }

    public boolean isUser(String id) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int cnt = rs.getInt(1);
                    return cnt > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("isUser Error");
        }
        return false;
    }

    public String getUserPassword(String id) {
        String sql = "SELECT PW FROM USERS WHERE USER_ID = ?";
        String pw = null;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    pw = rs.getString("PW");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getUserPassword Error");
        }
        return pw;
    }

    public String getUserNName(String id) {
        String sql = "SELECT NICKNAME FROM USERS WHERE USER_ID = ?";
        String nName = null;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nName = rs.getString("NICKNAME");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getUserNName Error");
        }
        return nName;
    }
}
