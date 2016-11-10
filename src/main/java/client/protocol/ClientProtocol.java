package client.protocol;

import channel.TcpChannel;
import connection.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.AbstractMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientProtocol extends Protocol {
    private Log log = LogFactory.getLog(ClientProtocol.class);

    private AbstractMap<String, Queue<String>> messages;
    private String lastMessage;
    private String username;

    public ClientProtocol() {
        super("\\|");
        messages = new ConcurrentHashMap<>();
    }

    public void addPrivateMessage(String username, String message) {
        if (!messages.containsKey(username)) {
            messages.put(username, new ConcurrentLinkedQueue<String>());
        }

        messages.get(username).add(message);
    }

    protected String selectCommand(String command, String params) {
        switch (command) {
            case "$lookup": return lookup(params);
            case "$send": return send(params);
            case "$login": return login(params);
            case "$logout": return logout(params);
            default: return params;
        }
    }

    public String getUsername() {
        return username;
    }

    private String logout(String input) {
        String[] params = splitParams(input);
        String resultCode = params[0];
        String message = params[1];

        if ("0".equals(resultCode)) {
            username = null;
        }

        return message;
    }

    private String login(String input) {
        String[] params = splitParams(input);
        String resultCode = params[0];
        String message;

        if ("0".equals(resultCode)) {
            username = params[1];
            message = params[2];
        } else {
            message = params[1];

        }

        return message;
    }

    public String getLastMessage() {
        return lastMessage;
    }


    public String send(String input) {
        String[] params = splitParams(input);
        String message = lastMessage = params[1];

        return message;
    }

    public String lookup(String input) {
        String[] params = splitParams(input);
        String username = params[1];
        String message = messages.get(username).poll();

        if ("0".equals(params[0])) {
            String address = params[2];

            if (message == null) {
                return null;
            }

            return sendMessage(username, address, message);
        } else {
            return params[2];
        }
    }

    private String sendMessage(String recipient, String address, String message) {
        String[] addressParts = address.split(":");
        String host = addressParts[0];
        int port = Integer.parseInt(addressParts[1]);

        try (Socket socket = new Socket(host, port)) {
            TcpChannel channel = new TcpChannel(socket);
            channel.open();
            channel.writeLine(String.format("%s|%s", username, message));
            String response = channel.readLine();

            return String.format("%s replied with %s.", recipient, response);
        } catch (IOException ex) {
            log.error("error sending private message", ex);
        }

        return null;
    }

    @Override
    protected boolean isCommand(String input) {
        return input.startsWith("$");
    }
}
