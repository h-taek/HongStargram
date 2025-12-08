package Back.DB;

import java.sql.*;
import java.util.*;
import com.google.gson.Gson;

public class LocationDB {

    // 사용자 위치 업데이트 (INSERT or UPDATE)
    public boolean updateUserLocation(String userId, double latitude, double longitude, String locationName) {
        String sql = "MERGE INTO USER_LOCATIONS ul " +
                "USING (SELECT ? AS USER_ID, ? AS LATITUDE, ? AS LONGITUDE, ? AS LOCATION_NAME FROM DUAL) src " +
                "ON (ul.USER_ID = src.USER_ID) " +
                "WHEN MATCHED THEN UPDATE SET ul.LATITUDE = src.LATITUDE, ul.LONGITUDE = src.LONGITUDE, " +
                "ul.LOCATION_NAME = src.LOCATION_NAME, ul.UPDATED_AT = SYSDATE " +
                "WHEN NOT MATCHED THEN INSERT (USER_ID, LATITUDE, LONGITUDE, LOCATION_NAME) " +
                "VALUES (src.USER_ID, src.LATITUDE, src.LONGITUDE, src.LOCATION_NAME)";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setDouble(2, latitude);
            pstmt.setDouble(3, longitude);
            pstmt.setString(4, locationName);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("updateUserLocation Error");
        }
        return false;
    }

    // 사용자 위치 조회
    public String getUserLocation(String userId) {
        String sql = "SELECT LATITUDE, LONGITUDE, LOCATION_NAME, UPDATED_AT FROM USER_LOCATIONS WHERE USER_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> location = new HashMap<>();
                    location.put("user_id", userId);
                    location.put("latitude", rs.getDouble("LATITUDE"));
                    location.put("longitude", rs.getDouble("LONGITUDE"));
                    location.put("location_name", rs.getString("LOCATION_NAME"));
                    location.put("updated_at", rs.getDate("UPDATED_AT").toString());
                    return new Gson().toJson(location);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getUserLocation Error");
        }
        return null;
    }

    // 위치 알람 추가
    public String addLocationAlert(String userId, String targetUserId, double alertDistance) {
        String sql = "INSERT INTO LOCATION_ALERTS (ALERT_ID, USER_ID, TARGET_USER_ID, ALERT_DISTANCE) " +
                "VALUES (ALERT_SEQ.NEXTVAL, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "ALERT_ID" })) {

            pstmt.setString(1, userId);
            pstmt.setString(2, targetUserId);
            pstmt.setDouble(3, alertDistance);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addLocationAlert Error");
        }
        return null;
    }

    // 사용자의 알람 목록 조회
    public String getLocationAlerts(String userId) {
        String sql = "SELECT a.ALERT_ID, a.TARGET_USER_ID, u.NICKNAME, a.ALERT_DISTANCE, a.IS_ACTIVE, a.CREATED_AT " +
                "FROM LOCATION_ALERTS a " +
                "JOIN USERS u ON a.TARGET_USER_ID = u.USER_ID " +
                "WHERE a.USER_ID = ? ORDER BY a.CREATED_AT DESC";

        List<Map<String, Object>> alerts = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("alert_id", rs.getLong("ALERT_ID"));
                    alert.put("target_user_id", rs.getString("TARGET_USER_ID"));
                    alert.put("target_nickname", rs.getString("NICKNAME"));
                    alert.put("alert_distance", rs.getDouble("ALERT_DISTANCE"));
                    alert.put("is_active", rs.getString("IS_ACTIVE"));
                    alert.put("created_at", rs.getDate("CREATED_AT").toString());
                    alerts.add(alert);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getLocationAlerts Error");
        }

        return new Gson().toJson(alerts);
    }

    // 알람 삭제
    public boolean deleteLocationAlert(String alertId) {
        String sql = "DELETE FROM LOCATION_ALERTS WHERE ALERT_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, Long.parseLong(alertId));
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteLocationAlert Error");
        }
        return false;
    }

    // 근처 사용자 확인 (거리 계산 포함)
    public String checkNearbyUsers(String userId, double maxDistance) {
        // Haversine 공식을 사용한 거리 계산
        String sql = "SELECT ul2.USER_ID, u.NICKNAME, ul2.LATITUDE, ul2.LONGITUDE, ul2.LOCATION_NAME, " +
                "(6371 * ACOS(COS(RADIANS(ul1.LATITUDE)) * COS(RADIANS(ul2.LATITUDE)) * " +
                "COS(RADIANS(ul2.LONGITUDE) - RADIANS(ul1.LONGITUDE)) + " +
                "SIN(RADIANS(ul1.LATITUDE)) * SIN(RADIANS(ul2.LATITUDE)))) * 1000 AS DISTANCE " +
                "FROM USER_LOCATIONS ul1 " +
                "CROSS JOIN USER_LOCATIONS ul2 " +
                "JOIN USERS u ON ul2.USER_ID = u.USER_ID " +
                "WHERE ul1.USER_ID = ? AND ul2.USER_ID != ul1.USER_ID " +
                "HAVING (6371 * ACOS(COS(RADIANS(ul1.LATITUDE)) * COS(RADIANS(ul2.LATITUDE)) * " +
                "COS(RADIANS(ul2.LONGITUDE) - RADIANS(ul1.LONGITUDE)) + " +
                "SIN(RADIANS(ul1.LATITUDE)) * SIN(RADIANS(ul2.LATITUDE)))) * 1000 <= ? " +
                "ORDER BY DISTANCE";

        List<Map<String, Object>> nearby = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setDouble(2, maxDistance);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> user = new HashMap<>();
                    user.put("user_id", rs.getString("USER_ID"));
                    user.put("nickname", rs.getString("NICKNAME"));
                    user.put("latitude", rs.getDouble("LATITUDE"));
                    user.put("longitude", rs.getDouble("LONGITUDE"));
                    user.put("location_name", rs.getString("LOCATION_NAME"));
                    user.put("distance", Math.round(rs.getDouble("DISTANCE")));
                    nearby.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("checkNearbyUsers Error");
        }

        return new Gson().toJson(nearby);
    }

    // 알람이 설정된 사용자들의 근처 여부 확인
    public String checkAlertedNearbyUsers(String userId) {
        String sql = "SELECT a.ALERT_ID, a.TARGET_USER_ID, u.NICKNAME, a.ALERT_DISTANCE, " +
                "ul2.LATITUDE, ul2.LONGITUDE, ul2.LOCATION_NAME, " +
                "(6371 * ACOS(COS(RADIANS(ul1.LATITUDE)) * COS(RADIANS(ul2.LATITUDE)) * " +
                "COS(RADIANS(ul2.LONGITUDE) - RADIANS(ul1.LONGITUDE)) + " +
                "SIN(RADIANS(ul1.LATITUDE)) * SIN(RADIANS(ul2.LATITUDE)))) * 1000 AS DISTANCE " +
                "FROM LOCATION_ALERTS a " +
                "JOIN USER_LOCATIONS ul1 ON a.USER_ID = ul1.USER_ID " +
                "JOIN USER_LOCATIONS ul2 ON a.TARGET_USER_ID = ul2.USER_ID " +
                "JOIN USERS u ON a.TARGET_USER_ID = u.USER_ID " +
                "WHERE a.USER_ID = ? AND a.IS_ACTIVE = 'Y' " +
                "HAVING (6371 * ACOS(COS(RADIANS(ul1.LATITUDE)) * COS(RADIANS(ul2.LATITUDE)) * " +
                "COS(RADIANS(ul2.LONGITUDE) - RADIANS(ul1.LONGITUDE)) + " +
                "SIN(RADIANS(ul1.LATITUDE)) * SIN(RADIANS(ul2.LATITUDE)))) * 1000 <= a.ALERT_DISTANCE " +
                "ORDER BY DISTANCE";

        List<Map<String, Object>> alerts = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> alert = new HashMap<>();
                    alert.put("alert_id", rs.getLong("ALERT_ID"));
                    alert.put("user_id", rs.getString("TARGET_USER_ID"));
                    alert.put("nickname", rs.getString("NICKNAME"));
                    alert.put("alert_distance", rs.getDouble("ALERT_DISTANCE"));
                    alert.put("latitude", rs.getDouble("LATITUDE"));
                    alert.put("longitude", rs.getDouble("LONGITUDE"));
                    alert.put("location_name", rs.getString("LOCATION_NAME"));
                    alert.put("distance", Math.round(rs.getDouble("DISTANCE")));
                    alerts.add(alert);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("checkAlertedNearbyUsers Error");
        }

        return new Gson().toJson(alerts);
    }
}
