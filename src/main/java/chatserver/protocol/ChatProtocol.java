package chatserver.protocol;

import channel.IChannel;
import chatserver.User;
import chatserver.UserStore;
import connection.Protocol;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class ChatProtocol extends Protocol {
    private final Log log = LogFactory.getLog(ChatProtocol.class);

    private User loggedInUser;
    private final UserStore userStore;

    public ChatProtocol(UserStore userStore, IChannel channel) {
        super(" ", channel);
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

    @Override
    public void onClosed() {
        if (loggedInUser == null) {
            return;
        }

        synchronized (loggedInUser) {
            if (loggedInUser.isOnline()) {
                loggedInUser.logout();
            }
        }
    }

    private String logout() {
        if (loggedInUser == null) {
            return "$logout|1|Not logged in.";
        }

        synchronized (loggedInUser) {
            loggedInUser.logout();
        }

        loggedInUser = null;

        return "$logout|0|Successfully logged out.";
    }

    private String register(String input) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        synchronized (loggedInUser) {
            loggedInUser.setPrivateAddress(input);
        }

        return String.format("Successfully registered address for %s.", loggedInUser.getName());
    }

    private String send(String message) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        User[] onlineUsers = userStore.getOnlineUsers();

        for (User user : onlineUsers) {
            synchronized (user) {
                // also check if user is still logged in
                if (user != loggedInUser && user.isOnline()) {
                    message = String.format("%s: %s", loggedInUser.getName(), message);
                    user.getProtocol().sendMessage(message);
                }
            }
        }

        return "";
    }

    private String implicitLookup(String username) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        User user = userStore.getUser(username);
        String address;

        if (user == null) {
            return String.format("$lookup|1|%s|Wrong username or user not reachable.", username);
        }

        synchronized (user) {
            if (!user.isRegistered()) {
                return String.format("$lookup|1|%s|Wrong username or user not reachable.", username);
            }

            address = user.getPrivateAddress();
        }

        return String.format("$lookup|0|%s|%s", username, address);
    }

    private String lookup(String username) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        User user = userStore.getUser(username);

        if (user == null) {
            return "Wrong username or user not registered.";
        }

        String address;

        synchronized (user) {
            if (!user.isRegistered()) {
                return "Wrong username or user not registered.";
            }
            address = user.getPrivateAddress();
        }

        return address;
    }

    private String login(String input) {
        String[] credentials = input.split(argsDelimiter);

        if (credentials.length != 2) {
            return "$login|1|Wrong command format.";
        }

        String username = credentials[0];
        String password = credentials[1];

        User user = userStore.getUser(username);

        if (user == null) {
            return "$login|1|Wrong username or password.";
        }

        synchronized (user) {
            if (loggedInUser != null) {
                return "$login|1|Already logged in.";
            }
            if (user != null && user.isOnline()) {
                return "$login|1|User already logged in on another client.";
            }

            if (user == null || !user.login(password)) {
                return "$login|1|Wrong username or password.";
            }
            user.setProtocol(this);
        }

        loggedInUser = user;

        return String.format("$login|0|%s|Successfully logged in.", username);
    }

    public void sendMessage(String message) {
        if (!this.channel.isOpen()) {
           return;
        }
        try {
            this.channel.writeLine(String.format("$send|0|%s", message));
        } catch (IOException ex) {
            log.error(ex);
        }
    }
}