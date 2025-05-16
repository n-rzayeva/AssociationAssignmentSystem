package associationassignmentsystem;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerClientHandler implements Runnable {

    private final Socket clientSocket;
    private final AssignmentServer server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerClientHandler(Socket socket, AssignmentServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            StringBuilder rawRequest = new StringBuilder();
            String line;
            int contentLength = 0;

            // Read headers
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                rawRequest.append(line).append("\r\n");
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            rawRequest.append("\r\n");

            // Read body
            char[] bodyChars = new char[contentLength];
            int read = in.read(bodyChars);
            String body = (read > 0) ? new String(bodyChars, 0, read).trim() : "";

            String request = rawRequest.toString();
            System.out.println("Received HTTP Request:\n" + request + body);

            String[] requestLines = request.split("\r\n");
            if (requestLines.length > 0) {
                String[] firstLine = requestLines[0].split(" ");
                if (firstLine.length == 3) {
                    String method = firstLine[0];
                    String path = firstLine[1];

                    if ("POST".equals(method) && "/volunteers".equals(path)) {
                        handleCreateVolunteer();
                    } else if ("POST".equals(method) && path.matches("/volunteers/\\d+/preferences")) {
                        handlePostPreferences(path, body);
                    } else if ("PUT".equals(method) && path.matches("/volunteers/\\d+/preferences")) {
                        handlePutPreferences(path, body);
                    } else if ("POST".equals(method) && "/optimize".equals(path)) {
                        handleTriggerOptimization();
                    } else if ("GET".equals(method) && "/assignments".equals(path)) {
                        handleGetAssignments();
                    } else if ("GET".equals(method) && "/assignments/detailed".equals(path)) {
                        handleGetDetailedAssignments();
                    } else {
                        sendHttpResponse("HTTP/1.1 404 Not Found", "application/json", new JSONObject().put("error", "Resource not found").toString());
                    }
                } else {
                    sendHttpResponse("HTTP/1.1 400 Bad Request", "application/json", new JSONObject().put("error", "Invalid HTTP request line").toString());
                }
            }

        } catch (IOException e) {
            System.err.println("Error handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleCreateVolunteer() {
        int volunteerId = server.createVolunteer();
        JSONObject response = new JSONObject();
        response.put("volunteerId", volunteerId);
        sendHttpResponse("HTTP/1.1 201 Created", "application/json", response.toString());
    }

    private void handlePostPreferences(String path, String body) {
        int volunteerId = extractVolunteerId(path);
        if (volunteerId == -1) {
            sendHttpResponse("HTTP/1.1 400 Bad Request", "application/json", new JSONObject().put("error", "Invalid volunteer ID in path").toString());
            return;
        }

        List<Integer> serviceIds = parseServiceIds(body);
        if (serviceIds == null) {
            sendHttpResponse("HTTP/1.1 400 Bad Request", "application/json", new JSONObject().put("error", "Invalid service ID format").toString());
            return;
        }

        server.setVolunteerPreferences(volunteerId, serviceIds);
        sendHttpResponse("HTTP/1.1 200 OK", "application/json", new JSONObject().put("message", "Preferences set").toString());
    }

    private void handlePutPreferences(String path, String body) {
        int volunteerId = extractVolunteerId(path);
        if (volunteerId == -1) {
            sendHttpResponse("HTTP/1.1 400 Bad Request", "application/json", new JSONObject().put("error", "Invalid volunteer ID in path").toString());
            return;
        }

        List<Integer> serviceIds = parseServiceIds(body);
        if (serviceIds == null) {
            sendHttpResponse("HTTP/1.1 400 Bad Request", "application/json", new JSONObject().put("error", "Invalid service ID format").toString());
            return;
        }

        server.updateVolunteerPreferences(volunteerId, serviceIds);
        sendHttpResponse("HTTP/1.1 200 OK", "application/json", new JSONObject().put("message", "Preferences updated").toString());
    }

    private void handleTriggerOptimization() {
        server.triggerOptimization();
        sendHttpResponse("HTTP/1.1 200 OK", "application/json", new JSONObject().put("message", "Optimization triggered").toString());
    }

    private void handleGetAssignments() {
        List<JSONObject> assignments = server.getAssignments();
        sendHttpResponse("HTTP/1.1 200 OK", "application/json", new JSONArray(assignments).toString());
    }

    private void handleGetDetailedAssignments() {
        List<JSONObject> detailedAssignments = server.getDetailedAssignments();
        sendHttpResponse("HTTP/1.1 200 OK", "application/json", new JSONArray(detailedAssignments).toString());
    }

    private int extractVolunteerId(String path) {
        Pattern pattern = Pattern.compile("/volunteers/(\\d+)/preferences");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    private List<Integer> parseServiceIds(String body) {
        try {
            JSONArray jsonArray = new JSONArray(body.trim());
            List<Integer> serviceIds = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                serviceIds.add(jsonArray.getInt(i));
            }
            return serviceIds;
        } catch (Exception e) {
            System.err.println("Failed to parse service IDs: " + body);
            e.printStackTrace();
            return null;
        }
    }

    private void sendHttpResponse(String statusLine, String contentType, String body) {
        out.println(statusLine);
        out.println("Content-Type: " + contentType + "; charset=UTF-8");
        out.println("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length);
        out.println();
        out.println(body);
    }
}
