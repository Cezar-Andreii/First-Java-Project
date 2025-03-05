package org.example;// Server.java
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;
import java.util.Optional;

class Server {
    private static Map<String, WeatherData> weatherDatabase = new HashMap<>();
    private static final int PORT = 12345;

    public static void main(String[] args) {
        loadWeatherData("src/main/java/org/example/weather_data.json");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void loadWeatherData(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String location = obj.getString("location");
                double latitude = obj.getDouble("latitude");
                double longitude = obj.getDouble("longitude");
                JSONArray forecastArray = obj.getJSONArray("forecast");
                List<WeatherForecast> forecast = new ArrayList<>();

                for (int j = 0; j < forecastArray.length(); j++) {
                    JSONObject dayForecast = forecastArray.getJSONObject(j);
                    String condition = dayForecast.getString("condition");
                    double temperature = dayForecast.getDouble("temperature");
                    forecast.add(new WeatherForecast(condition, temperature));
                }

                weatherDatabase.put(location, new WeatherData(location, latitude, longitude, forecast));
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error loading weather data: " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                out.println("Connected to Weather Server. Identify as 'admin' or 'regular'.");

                String userType = in.readLine();
                if ("admin".equalsIgnoreCase(userType)) {
                    handleAdminRequests(in, out);
                } else {
                    handleRegularRequests(in, out);
                }

            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private void handleAdminRequests(BufferedReader in, PrintWriter out) throws IOException {
            out.println("Admin mode activated. Send 'add' to add new data, or 'exit' to quit.");

            String command;
            while ((command = in.readLine()) != null) {
                if ("add".equalsIgnoreCase(command)) {
                    out.println("Enter location name:");
                    String location = in.readLine();
                    out.println("Enter latitude:");
                    double latitude = Double.parseDouble(in.readLine());
                    out.println("Enter longitude:");
                    double longitude = Double.parseDouble(in.readLine());

                    out.println("Enter number of forecast days:");
                    int numDays = Integer.parseInt(in.readLine());
                    List<WeatherForecast> forecast = new ArrayList<>();

                    for (int i = 0; i < numDays; i++) {
                        out.println("Enter condition for day " + (i + 1) + ":");
                        String condition = in.readLine();
                        out.println("Enter temperature for day " + (i + 1) + ":");
                        double temperature = Double.parseDouble(in.readLine());
                        forecast.add(new WeatherForecast(condition, temperature));
                    }

                    // Add new data to weatherDatabase
                    WeatherData newData = new WeatherData(location, latitude, longitude, forecast);
                    weatherDatabase.put(location, newData);

                    // Update JSON file
                    saveWeatherData("src/main/java/org/example/weather_data.json");

                    out.println("Data added successfully for location: " + location);
                } else if ("exit".equalsIgnoreCase(command)) {
                    out.println("Exiting admin mode.");
                    break;
                } else {
                    out.println("Unknown command.");
                }
            }
        }

        private static void saveWeatherData(String filePath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                JSONArray jsonArray = new JSONArray();

                for (WeatherData data : weatherDatabase.values()) {
                    JSONObject obj = new JSONObject();
                    obj.put("location", data.location);
                    obj.put("latitude", data.latitude);
                    obj.put("longitude", data.longitude);

                    JSONArray forecastArray = new JSONArray();
                    for (WeatherForecast forecast : data.forecast) {
                        JSONObject forecastObj = new JSONObject();
                        forecastObj.put("condition", forecast.condition);
                        forecastObj.put("temperature", forecast.temperature);
                        forecastArray.put(forecastObj);
                    }
                    obj.put("forecast", forecastArray);

                    jsonArray.put(obj);
                }

                writer.write(jsonArray.toString(4)); // Pretty-print with indentation
            } catch (IOException e) {
                System.err.println("Error saving weather data: " + e.getMessage());
            }
        }


        private void handleRegularRequests(BufferedReader in, PrintWriter out) throws IOException {
            out.println("Regular user mode. Send location coordinates (latitude,longitude) or 'exit' to quit.");

            String input;
            while ((input = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(input)) {
                    out.println("Goodbye!");
                    break;
                }

                String[] parts = input.split(",");
                if (parts.length == 2) {
                    try {
                        double latitude = Double.parseDouble(parts[0].trim());
                        double longitude = Double.parseDouble(parts[1].trim());
                        //WeatherData nearest = findNearestWeatherData(latitude, longitude);
                        Optional<WeatherData> nearest = findNearestWeatherData(latitude, longitude);

                        if (nearest.isPresent()) {
                            WeatherData data = nearest.get();
                            StringBuilder response = new StringBuilder();
                            response.append("Nearest location: ").append(data.location).append("\n");
                            response.append("Weather forecast for the next 3 days:\n");

                            int days = Math.min(data.forecast.size(), 3); // Afișează cel mult 3 zile
                            for (int i = 0; i < days; i++) {
                                response.append(data.forecast.get(i).toString()).append("\n");
                            }

                            out.println(response.toString());
                        } else {
                            out.println("No weather data available for the provided coordinates.");
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid coordinates format. Use: latitude,longitude");
                    }
                } else {
                    out.println("Invalid input. Use: latitude,longitude");
                }
                //if(!"exit".equalsIgnoreCase(input)) {
                out.println("Send location coordinates (latitude,longitude) or 'exit' to quit.");

                //}
            }

        }

        private static Optional<WeatherData> findNearestWeatherData(double latitude, double longitude) {
            WeatherData nearest = null;
            double minDistance = Double.MAX_VALUE;

            for (WeatherData data : weatherDatabase.values()) {
                double distance = calculateDistance(latitude, longitude, data.latitude, data.longitude);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = data;
                }
            }

            return Optional.ofNullable(nearest);
        }

        private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            final int R = 6371; // Radius of the Earth in km
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                    + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                    * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            return R * c;
        }
    }
}