package client;

import connection.Protocol;

import java.util.AbstractMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ClientProtocol extends Protocol {

    private AbstractMap<String, Queue<String>> messages;

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
            default: return params;
        }
    }

    private String[] splitParams(String input) {
        return input.split(argsDelimiter);
    }

    public String lookup(String input) {
        String[] params = splitParams(input);
        String username = params[1];

        if ("0".equals(params[0])) {
            String address = params[2];

            for (String message : getMessages(username)) {
                sendMessage(address, message);
            }
        } else {
            // error
            removeMessages(username);
            return params[2];
        }

        return "Messages sent";
    }

    private void removeMessages(String username) {

    }

    private void sendMessage(String address, String message) {

    }

    public Queue<String> getMessages(String username) {
        if (!messages.containsKey(username)) {
            return new LinkedList();
        }

        return messages.get(username);
    }

    @Override
    protected boolean isCommand(String input) {
        return input.startsWith("$");
    }
}
