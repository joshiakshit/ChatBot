import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClientGUI {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JTextPane chatPane;
    private JTextField inputField;
    private JButton sendButton;
    private String userName;
    private JLabel userLabel;
    private StyledDocument doc;

    private Map<String, Color> userColors = new HashMap<>();

    private final Color[] colorPalette = {
            new Color(0x1976D2),
            new Color(0x388E3C),
            new Color(0xFBC02D),
            new Color(0xD32F2F),
            new Color(0x7B1FA2),
            new Color(0xF57C00),
            new Color(0x0097A7),
            new Color(0x5D4037),
            new Color(0x455A64),
            new Color(0xC2185B)
    };
    private int colorIndex = 0;

    public ChatClientGUI(String serverAddress, int port) {
        setupGUI();

        try {
            socket = new Socket(serverAddress, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            userName = JOptionPane.showInputDialog(frame, "Enter your username:");
            if (userName == null || userName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty. Exiting.");
                socket.close();
                frame.dispose();
                return;
            }
            out.println(userName);
            userLabel.setText("Logged in as: " + userName);
            assignUserColor(userName);
            appendSystemMessage("You have joined the chat as " + userName);

            ActionListener sendAction = e -> {
                String message = inputField.getText();
                if (!message.trim().isEmpty()) {
                    out.println(message);
                    appendUserMessage(userName, message, true);
                    inputField.setText("");
                    sendButton.setEnabled(false);
                    if (message.equalsIgnoreCase("/exit")) {
                        try { socket.close(); } catch (IOException ex) {}
                        frame.dispose();
                    }
                }
            };
            sendButton.addActionListener(sendAction);
            inputField.addActionListener(sendAction);

            inputField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { checkInput(); }
                public void checkInput() {
                    sendButton.setEnabled(!inputField.getText().trim().isEmpty());
                }
            });

            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        if (msg.contains(": ")) {
                            int idx = msg.indexOf(": ");
                            String sender = msg.substring(0, idx);
                            String text = msg.substring(idx + 2);
                            assignUserColor(sender);
                            appendUserMessage(sender, text, false);
                        } else {
                            appendSystemMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    appendSystemMessage("Connection closed.");
                }
            }).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Unable to connect: " + e.getMessage());
            frame.dispose();
        }
    }

    private void setupGUI() {
        frame = new JFrame("Chat Client");
        frame.setMinimumSize(new Dimension(520, 440));
        frame.setLayout(new BorderLayout(10, 10));

        userLabel = new JLabel();
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(userLabel, BorderLayout.NORTH);

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setFont(new Font("Aptos", Font.PLAIN, 16));
        chatPane.setBackground(new Color(245, 245, 245));
        doc = chatPane.getStyledDocument();
        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        frame.add(scrollPane, BorderLayout.CENTER);

        inputField = new JTextField(30);
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.add(inputField);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(sendButton);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void assignUserColor(String username) {
        if (!userColors.containsKey(username)) {
            userColors.put(username, colorPalette[colorIndex % colorPalette.length]);
            colorIndex++;
        }
    }

    private void appendUserMessage(String username, String message, boolean isOwn) {
        try {
            Style style = chatPane.addStyle("userStyle", null);
            StyleConstants.setFontFamily(style, "Aptos");
            StyleConstants.setFontSize(style, 16);
            StyleConstants.setForeground(style, userColors.get(username));
            StyleConstants.setBold(style, true);
            StyleConstants.setLeftIndent(style, 8f);
            StyleConstants.setSpaceAbove(style, 6f);
            StyleConstants.setSpaceBelow(style, 10f);

            doc.insertString(doc.getLength(), username, style);

            Style msgStyle = chatPane.addStyle("msgStyle", null);
            StyleConstants.setFontFamily(msgStyle, "Aptos");
            StyleConstants.setFontSize(msgStyle, 16);
            StyleConstants.setForeground(msgStyle, userColors.get(username));
            StyleConstants.setBold(msgStyle, false);
            StyleConstants.setLeftIndent(msgStyle, 8f);
            StyleConstants.setSpaceAbove(msgStyle, 6f);
            StyleConstants.setSpaceBelow(msgStyle, 10f);

            doc.insertString(doc.getLength(), ": " + message + "\n", msgStyle);

            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) {
        }
    }

    private void appendSystemMessage(String message) {
        try {
            Style style = chatPane.addStyle("sysStyle", null);
            StyleConstants.setFontFamily(style, "Arial");
            StyleConstants.setFontSize(style, 14);
            StyleConstants.setForeground(style, Color.GRAY);
            StyleConstants.setBold(style, true);
            StyleConstants.setSpaceAbove(style, 8f);
            StyleConstants.setSpaceBelow(style, 12f);
            doc.insertString(doc.getLength(), "[System]: " + message + "\n", style);
            chatPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException ex) {
        }
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;
        SwingUtilities.invokeLater(() -> new ChatClientGUI(serverAddress, port));
    }
}