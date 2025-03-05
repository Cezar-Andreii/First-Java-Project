package org.example;

import java.io.*;
import java.net.*;

class Client {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int port = 12345;

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server.");
            System.out.println(in.readLine());

            System.out.print("Enter user type (admin/regular): ");
            String userType = console.readLine();
            out.println(userType);

            if ("regular".equalsIgnoreCase(userType)) {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server: " + serverResponse);

                    if (serverResponse.contains("Goodbye")) {
                        break;
                    }

                    if (serverResponse.contains("Send location coordinates") || serverResponse.contains("Invalid")) {
                        System.out.print("You: ");
                        String coordinates = console.readLine();
                        out.println(coordinates);
                    }
                }
            } else {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println("Server: " + serverResponse);

                    if (serverResponse.contains("Goodbye") || serverResponse.contains("Exiting")) {
                        break;
                    }

                    if (!serverResponse.contains("Weather forecast")) { // Trimite input doar când e așteptat
                        System.out.print("You: ");
                        String userInput = console.readLine();
                        out.println(userInput);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
