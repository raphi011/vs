package chatserver.protocol;

import chatserver.User;
import chatserver.UserStore;
import connection.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class ChatProtocol extends Protocol implements ISendMessage {
    private final Log log = LogFactory.getLog(ChatProtocol.class);

    private User loggedInUser;
    private final UserStore userStore;

    public ChatProtocol(UserStore userStore) {
        super(" ");
        this.userStore = userStore;
    }

    @Override
    protected boolean isCommand(String input) {
        return true;
    }

    @Override
    protected String selectCommand(String command, String input) {
        switch (command) {
            case "login": return login(input);
            case "logout": return logout();
            case "send": return send(input);
            case "lookup": return lookup(input);
            case "$lookup": return implicitLookup(input);
            case "register": return register(input);
            default: return "Unknown command";
        }
    }

    private String logout() {
        if (loggedInUser == null) {
            return "$logout|1|Not logged in.";
        }

        loggedInUser.logout();

        return "$logout|0|Successfully logged out.";
    }

    private String register(String input) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        loggedInUser.setPrivateAddress(input);

        return String.format("Successfully registered address for %s.", loggedInUser.getName());
    }

    private String send(String message) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        User[] onlineUsers = userStore.getOnlineUsers();

        for (User user : onlineUsers) {
            if (user != loggedInUser) {
                message = String.format("%s: %s", loggedInUser.getName(), message);
                user.getProtocol().sendMessage(message);
            }
        }

        return "";
    }

    private String implicitLookup(String username) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        User user = userStore.getUser(username);
        if (user == null || !user.getIsRegistered()) {
            return String.format("$lookup|1|%s|Wrong username or user not reachable.", username);
        }

        return String.format("$lookup|0|%s|%s", username, user.getPrivateAddress());
    }

    private String lookup(String username) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }
        User user = userStore.getUser(username);
        if (user == null || !user.getIsRegistered()) {
            return "Wrong username or user not registered.";
        }

        return user.getPrivateAddress();
    }

    private String login(String input) {
        if (loggedInUser != null) {
            return "$login|1|Already logged in.";
        }
        String[] credentials = input.split(argsDelimiter);
        String username = credentials[0];
        String password = credentials[1];

        if (credentials.length != 2) {
            return "$login|1|Wrong command format.";
        }

        if ((loggedInUser = userStore.Authenticate(username, password)) == null) {
            return "$login|1|Wrong username or password.";
        }

        loggedInUser.setProtocol(this);

        return String.format("$login|0|%s|Successfully logged in.", username);
    }

    @Override
    public void sendMessage(String message) {
        try {
            this.channel.writeLine(String.format("$send|0|%s", message));
        } catch (IOException ex) {
            log.error(ex);
        }
    }
}