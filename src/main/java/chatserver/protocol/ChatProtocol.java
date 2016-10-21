package chatserver.protocol;

import chatserver.User;
import chatserver.UserStore;

public class ChatProtocol extends Protocol {
    private User loggedInUser;
    private final UserStore userStore;

    public ChatProtocol(UserStore userStore) {
        this.userStore = userStore;

    }

    @Override
    protected String selectCommand(String command, String input) {
        switch (command) {
            case "login":
                return login(input);
            case "send":
                return send(input);
            case "lookup":
                return lookup(input);
            case "register":
                return register(input);
            default:
                return "Unknown command";
        }
    }

    private String register(String input) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }

        loggedInUser.setPrivateAddress(input);

        return String.format("Successfully registered address for %s.", loggedInUser.getName());
    }

    private String send(String input) {
        return "Not implemented";
    }

    private String lookup(String input) {
        if (loggedInUser == null) {
            return "Not logged in.";
        }
        User user = userStore.getUser(input);
        if (user == null || !user.getIsRegistered()) {
            return "Wrong username or user not registered.";
        }

        return user.getPrivateAddress();
    }

    private String login(String input) {
        if (loggedInUser != null) {
            return "Already logged in.";
        }
        String[] credentials = input.split(" ");

        if (credentials.length != 2) {
            return "Wrong command format.";
        }

        if ((loggedInUser = userStore.Authenticate(credentials[0], credentials[1])) == null) {
            return "Wrong username or password.";
        }

        return "Successfully logged in.";
    }
}
