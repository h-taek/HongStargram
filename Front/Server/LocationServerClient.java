package Front.Server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LocationServerClient {
    private static final String SERVER_URL = "http://" + IP.ip + ":8006";

    // 위치 업데이트
    public boolean UpdateLocationRequest(String userId, double latitude, double longitude, String locationName) {
        try {
            URL url = new URL(SERVER_URL + "/UpdateLocation");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("latitude", String.valueOf(latitude));
            data.put("longitude", String.valueOf(longitude));
            data.put("location_name", locationName);

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 사용자 위치 조회
    public Map<String, Object> GetUserLocationRequest(String userId) {
        try {
            URL url = new URL(SERVER_URL + "/GetUserLocation");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(userId.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 1) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String response = br.readLine();
                    return new Gson().fromJson(response, new TypeToken<Map<String, Object>>() {
                    }.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 위치 알람 추가
    public String AddLocationAlertRequest(String userId, String targetUserId, double alertDistance) {
        try {
            URL url = new URL(SERVER_URL + "/AddLocationAlert");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("target_user_id", targetUserId);
            data.put("alert_distance", String.valueOf(alertDistance));

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 1) {
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

    // 알람 목록 조회
    public List<Map<String, Object>> GetLocationAlertsRequest(String userId) {
        try {
            URL url = new URL(SERVER_URL + "/GetLocationAlerts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(userId.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 1) {
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

    // 알람 삭제
    public boolean DeleteLocationAlertRequest(String alertId) {
        try {
            URL url = new URL(SERVER_URL + "/DeleteLocationAlert");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(alertId.getBytes(StandardCharsets.UTF_8));
            }

            return conn.getResponseCode() == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 근처 사용자 확인
    public List<Map<String, Object>> CheckNearbyUsersRequest(String userId, double maxDistance) {
        try {
            URL url = new URL(SERVER_URL + "/CheckNearbyUsers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            Map<String, String> data = new HashMap<>();
            data.put("user_id", userId);
            data.put("max_distance", String.valueOf(maxDistance));

            String jsonData = new Gson().toJson(data);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonData.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 1) {
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

    // 알람이 설정된 사용자들의 근처 여부 확인
    public List<Map<String, Object>> CheckAlertedNearbyUsersRequest(String userId) {
        try {
            URL url = new URL(SERVER_URL + "/CheckAlertedNearbyUsers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(userId.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 1) {
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
}
