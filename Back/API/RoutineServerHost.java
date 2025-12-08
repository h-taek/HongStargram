package Back.API;

import java.util.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.*;

import Back.DB.RoutineDB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

class AddRoutineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String userId = data.get("user_id");
            String title = data.get("title");
            String description = data.get("description");
            String color = data.get("color");

            RoutineDB routineDB = new RoutineDB();
            String routineId = routineDB.addRoutine(userId, title, description, color);

            String response = routineId != null ? routineId : "Failed";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("RoutineServerHost.AddRoutineHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetRoutinesHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String userId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            RoutineDB routineDB = new RoutineDB();
            String routines = routineDB.getRoutines(userId);

            byte[] payload = routines.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("RoutineServerHost.GetRoutinesHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class DeleteRoutineHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String routineId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            RoutineDB routineDB = new RoutineDB();
            boolean success = routineDB.deleteRoutine(routineId);

            exchange.sendResponseHeaders(success ? 1 : 999, -1);
        } catch (Exception e) {
            System.out.println("RoutineServerHost.DeleteRoutineHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class AddRoutineEventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String routineId = data.get("routine_id");
            String eventDate = data.get("event_date");
            String eventTime = data.get("event_time");

            RoutineDB routineDB = new RoutineDB();
            String eventId = routineDB.addRoutineEvent(routineId, eventDate, eventTime);

            String response = eventId != null ? eventId : "Failed";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("RoutineServerHost.AddRoutineEventHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class GetRoutineEventsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Map<String, String> data = gson.fromJson(body, new TypeToken<Map<String, String>>() {
            }.getType());

            String userId = data.get("user_id");
            String startDate = data.get("start_date");
            String endDate = data.get("end_date");

            RoutineDB routineDB = new RoutineDB();
            String events = routineDB.getRoutineEvents(userId, startDate, endDate);

            byte[] payload = events.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(1, payload.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        } catch (Exception e) {
            System.out.println("RoutineServerHost.GetRoutineEventsHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class DeleteRoutineEventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String eventId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            RoutineDB routineDB = new RoutineDB();
            boolean success = routineDB.deleteRoutineEvent(eventId);

            exchange.sendResponseHeaders(success ? 1 : 999, -1);
        } catch (Exception e) {
            System.out.println("RoutineServerHost.DeleteRoutineEventHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

class ToggleEventCompletionHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) {
        try {
            InputStream in = exchange.getRequestBody();
            String eventId = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            RoutineDB routineDB = new RoutineDB();
            boolean success = routineDB.toggleEventCompletion(eventId);

            exchange.sendResponseHeaders(success ? 1 : 999, -1);
        } catch (Exception e) {
            System.out.println("RoutineServerHost.ToggleEventCompletionHandler Err!");
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }
}

public class RoutineServerHost {
    private static final int port = 8005;

    public RoutineServerHost() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        server.createContext("/test", exchange -> {
            String response = "Routine server is connected";
            byte[] payload = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        });

        server.createContext("/AddRoutine", new AddRoutineHandler());
        server.createContext("/GetRoutines", new GetRoutinesHandler());
        server.createContext("/DeleteRoutine", new DeleteRoutineHandler());
        server.createContext("/AddRoutineEvent", new AddRoutineEventHandler());
        server.createContext("/GetRoutineEvents", new GetRoutineEventsHandler());
        server.createContext("/DeleteRoutineEvent", new DeleteRoutineEventHandler());
        server.createContext("/ToggleEventCompletion", new ToggleEventCompletionHandler());

        server.setExecutor(null);
        server.start();

        System.out.printf("Routine server starting on htaeky.iptime.org : %d\n", port);
    }
}
