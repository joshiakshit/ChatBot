import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String userName;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            userName = in.readLine();
            System.out.println(userName + " has joined the chat.");
            server.broadcastMessage(userName + " has joined the chat.", this);
        } catch (IOException e) {
            System.out.println("Error initializing client handler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                String formattedMsg = userName + ": " + message;
                System.out.println(formattedMsg);
                server.broadcastMessage(formattedMsg, this);
            }
        } catch (IOException e) {
            System.out.println(userName + " disconnected.");
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }
}