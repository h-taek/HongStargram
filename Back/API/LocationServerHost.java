package Back.API;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;

import Back.DB.LocationDB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class UpdateLocationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String userId = data.get("user_id");
            double latitude = Double.parseDouble(data.get("latitude"));
            double longitude = Double.parseDouble(data.get("longitude"));
            String locationName = data.get("location_name");

            LocationDB locationDB = new LocationDB();
            boolean success = locationDB.updateUserLocation(userId, latitude, longitude, locationName);

            exchange.sendResponseHeaders(success ? 1 : 999, -1);
        } catch (Exception e) {
            System.out.println("LocationServerHost.UpdateLocationHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetUserLocationHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String userId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            LocationDB locationDB = new LocationDB();
            String location = locationDB.getUserLocation(userId);

            if (location != null) {
                byte[] payload = location.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(1, payload.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(payload);
                }
            } else {
                exchange.sendResponseHeaders(999, -1);
            }
        } catch (Exception e) {
            System.out.println("LocationServerHost.GetUserLocationHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class AddLocationAlertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String userId = data.get("user_id");
            String targetUserId = data.get("target_user_id");
            double alertDistance = Double.parseDouble(data.get("alert_distance"));

            LocationDB locationDB = new LocationDB();
            String alertId = locationDB.addLocationAlert(userId, targetUserId, alertDistance);

            String response = alertId != null ? alertId : "Failed";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("LocationServerHost.AddLocationAlertHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetLocationAlertsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String userId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            LocationDB locationDB = new LocationDB();
            String alerts = locationDB.getLocationAlerts(userId);

            byte[] payload = alerts.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("LocationServerHost.GetLocationAlertsHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class DeleteLocationAlertHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String alertId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            LocationDB locationDB = new LocationDB();
            boolean success = locationDB.deleteLocationAlert(alertId);

            exchange.sendResponseHeaders(success ? 1 : 999, -1);
        } catch (Exception e) {
            System.out.println("LocationServerHost.DeleteLocationAlertHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class CheckNearbyUsersHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String userId = data.get("user_id");
            double maxDistance = Double.parseDouble(data.get("max_distance"));

            LocationDB locationDB = new LocationDB();
            String nearby = locationDB.checkNearbyUsers(userId, maxDistance);

            byte[] payload = nearby.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("LocationServerHost.CheckNearbyUsersHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class CheckAlertedNearbyUsersHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String userId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            LocationDB locationDB = new LocationDB();
            String alerts = locationDB.checkAlertedNearbyUsers(userId);

            byte[] payload = alerts.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("LocationServerHost.CheckAlertedNearbyUsersHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

public class LocationServerHost {
    private static final int port = 8006;

    public LocationServerHost() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/test", exchange -> {
            String response = "Location server is connected";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });

        server.createContext("/UpdateLocation", new UpdateLocationHandler());
        server.createContext("/GetUserLocation", new GetUserLocationHandler());
        server.createContext("/AddLocationAlert", new AddLocationAlertHandler());
        server.createContext("/GetLocationAlerts", new GetLocationAlertsHandler());
        server.createContext("/DeleteLocationAlert", new DeleteLocationAlertHandler());
        server.createContext("/CheckNearbyUsers", new CheckNearbyUsersHandler());
        server.createContext("/CheckAlertedNearbyUsers", new CheckAlertedNearbyUsersHandler());

        server.setExecutor(null);
        server.start();

        System.out.printf("Location server starting on htaeky.iptime.org : %d\n", port);
    }
}
