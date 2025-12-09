package Front.Server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RoutineServerClient {
    private static final String SERVER_URL = "http://" + IP.ip + ":8005";

    // 루틴 추가
    public String AddRoutineRequest(String userId, String title, String description, String color) {
        try {
            URL url = new URL(SERVER_URL + "/AddRoutine");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("title", title);
            data.put("description", description);
            data.put("color", color);

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 루틴 목록 조회
    public List<Map<String, Object>> GetRoutinesRequest(String userId) {
        try {
            URL url = new URL(SERVER_URL + "/GetRoutines");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(userId.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String response = br.readLine();
                    return new Gson().fromJson(response, new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // 루틴 삭제
    public boolean DeleteRoutineRequest(String routineId) {
        try {
            URL url = new URL(SERVER_URL + "/DeleteRoutine");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(routineId.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 루틴 이벤트 추가
    public String AddRoutineEventRequest(String routineId, String eventDate, String eventTime) {
        try {
            URL url = new URL(SERVER_URL + "/AddRoutineEvent");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("routine_id", routineId);
            data.put("event_date", eventDate);
            data.put("event_time", eventTime);

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    return br.readLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 루틴 이벤트 조회
    public List<Map<String, Object>> GetRoutineEventsRequest(String userId, String startDate, String endDate) {
        try {
            URL url = new URL(SERVER_URL + "/GetRoutineEvents");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("start_date", startDate);
            data.put("end_date", endDate);

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String response = br.readLine();
                    return new Gson().fromJson(response, new TypeToken<List<Map<String, Object>>>() {
                    }.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // 루틴 이벤트 삭제
    public boolean DeleteRoutineEventRequest(String eventId) {
        try {
            URL url = new URL(SERVER_URL + "/DeleteRoutineEvent");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(eventId.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 루틴 이벤트 완료 상태 토글
    public boolean ToggleEventCompletionRequest(String eventId) {
        try {
            URL url = new URL(SERVER_URL + "/ToggleEventCompletion");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(eventId.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
