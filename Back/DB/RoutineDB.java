package Back.DB;

import java.sql.*;
import java.util.*;
import com.google.gson.Gson;

public class RoutineDB {

    // 루틴 추가
    public String addRoutine(String userId, String title, String description, String color) {
        String sql = "INSERT INTO ROUTINES (ROUTINE_ID, USER_ID, TITLE, DESCRIPTION, COLOR) VALUES (ROUTINE_SEQ.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "ROUTINE_ID" })) {

            pstmt.setString(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setString(4, color);
            pstmt.executeUpdate();

            // 생성된 ROUTINE_ID 반환
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addRoutine Error");
        }
        return null;
    }

    // 사용자의 모든 루틴 조회
    public String getRoutines(String userId) {
        String sql = "SELECT ROUTINE_ID, TITLE, DESCRIPTION, COLOR, CREATED_AT FROM ROUTINES WHERE USER_ID = ? ORDER BY CREATED_AT DESC";
        List<Map<String, Object>> routines = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> routine = new HashMap<>();
                    routine.put("routine_id", rs.getLong("ROUTINE_ID"));
                    routine.put("title", rs.getString("TITLE"));
                    routine.put("description", rs.getString("DESCRIPTION"));
                    routine.put("color", rs.getString("COLOR"));
                    routine.put("created_at", rs.getDate("CREATED_AT").toString());
                    routines.add(routine);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getRoutines Error");
        }

        return new Gson().toJson(routines);
    }

    // 루틴 삭제
    public boolean deleteRoutine(String routineId) {
        String sql = "DELETE FROM ROUTINES WHERE ROUTINE_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, Long.parseLong(routineId));
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteRoutine Error");
        }
        return false;
    }

    // 루틴 이벤트 추가
    public String addRoutineEvent(String routineId, String eventDate, String eventTime) {
        String sql = "INSERT INTO ROUTINE_EVENTS (EVENT_ID, ROUTINE_ID, EVENT_DATE, EVENT_TIME) VALUES (EVENT_SEQ.NEXTVAL, ?, TO_DATE(?, 'YYYY-MM-DD'), ?)";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "EVENT_ID" })) {

            pstmt.setLong(1, Long.parseLong(routineId));
            pstmt.setString(2, eventDate);
            pstmt.setString(3, eventTime);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return String.valueOf(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("addRoutineEvent Error");
        }
        return null;
    }

    // 특정 기간의 루틴 이벤트 조회
    public String getRoutineEvents(String userId, String startDate, String endDate) {
        String sql = "SELECT e.EVENT_ID, e.ROUTINE_ID, r.TITLE, r.COLOR, e.EVENT_DATE, e.EVENT_TIME, e.IS_COMPLETED " +
                "FROM ROUTINE_EVENTS e " +
                "JOIN ROUTINES r ON e.ROUTINE_ID = r.ROUTINE_ID " +
                "WHERE r.USER_ID = ? AND e.EVENT_DATE BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') " +
                "ORDER BY e.EVENT_DATE, e.EVENT_TIME";

        List<Map<String, Object>> events = new ArrayList<>();

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            pstmt.setString(2, startDate);
            pstmt.setString(3, endDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> event = new HashMap<>();
                    event.put("event_id", rs.getLong("EVENT_ID"));
                    event.put("routine_id", rs.getLong("ROUTINE_ID"));
                    event.put("title", rs.getString("TITLE"));
                    event.put("color", rs.getString("COLOR"));
                    event.put("event_date", rs.getDate("EVENT_DATE").toString());
                    event.put("event_time", rs.getString("EVENT_TIME"));
                    event.put("is_completed", rs.getString("IS_COMPLETED"));
                    events.add(event);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("getRoutineEvents Error");
        }

        return new Gson().toJson(events);
    }

    // 루틴 이벤트 삭제
    public boolean deleteRoutineEvent(String eventId) {
        String sql = "DELETE FROM ROUTINE_EVENTS WHERE EVENT_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, Long.parseLong(eventId));
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("deleteRoutineEvent Error");
        }
        return false;
    }

    // 루틴 이벤트 완료 상태 변경
    public boolean toggleEventCompletion(String eventId) {
        String sql = "UPDATE ROUTINE_EVENTS SET IS_COMPLETED = CASE WHEN IS_COMPLETED = 'Y' THEN 'N' ELSE 'Y' END, " +
                "COMPLETED_AT = CASE WHEN IS_COMPLETED = 'N' THEN SYSDATE ELSE NULL END WHERE EVENT_ID = ?";

        try (Connection conn = DBManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, Long.parseLong(eventId));
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("toggleEventCompletion Error");
        }
        return false;
    }
}
