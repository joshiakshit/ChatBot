# Java Chat Application

A simple client-server chat application written in Java, featuring both console and GUI (Swing) clients. Multiple users can connect to the server locally, send messages, and see them broadcast to all connected clients. Each user is assigned a unique color in the GUI for easy identification.

---

## Features

- **Server**
    - Handles multiple client connections.
    - Broadcasts messages to all connected clients.
- **GUI Client**
    - User-friendly chat window built with Swing.
    - Each user gets a unique color for their messages.
    - Usernames are displayed in bold; messages use Aptos font.
    - System messages (like joining/leaving) are shown in gray.
    - Responsive design and auto-scrolling.
    - Spacing between messages for readability.

---

## Getting Started

### Prerequisites

- Java 8 or higher
- (Optional) IntelliJ IDEA or any Java IDE

---

### Running the Server

1. Open `ChatServer.java` in your IDE.
2. Run the `main` method.
3. Console shows:
   ```
   Server started on port 12345
   ```

---

### Running GUI Clients

#### **Option 1: From IDE (Recommended for single instance)**

- Open `ChatClientGUI.java`.
- Run the `main` method.
- Enter a username when prompted.
- Chat window opens. You can send and receive messages.

#### **Option 2: Multiple Instances (Recommended for testing)**

- **Via Terminal:**
    1. Compile the project:
       ```sh
       javac ChatClientGUI.java
       ```
    2. In multiple terminal windows, run:
       ```sh
       java ChatClientGUI
       ```
    3. Enter different usernames to simulate multiple users.

- **Via IntelliJ:**
    - Enable "Allow parallel run" in your Run Configuration for `ChatClientGUI`, or use multiple Run Configurations.

---

## How It Works

- The server listens for client connections.
- Each client connects, enters a username, and joins the chat.
- Messages are sent to the server and broadcast to all clients.
- In the GUI, each user’s messages are shown in a unique color, with bold usernames.

---

## Troubleshooting

- **Connection refused?**
    - Make sure the server is running before starting clients.
    - Ensure both server and clients use the same port (`12345` by default).
- **Want multiple clients?**
    - Use terminal or enable parallel running in your IDE.

---

## Customization

- Change the port in both `ChatServer` and `ChatClientGUI` if needed.
- To customize colors, edit the `colorPalette` array in `ChatClientGUI.java`.

---

## License

This project is provided for educational purposes and is not production-ready.  
Feel free to modify and use it for learning or personal use.

---