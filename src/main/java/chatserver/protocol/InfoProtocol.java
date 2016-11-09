package chatserver.protocol;

import chatserver.User;
import chatserver.UserStore;
import connection.Protocol;

public class InfoProtocol extends Protocol {

    private final UserStore userStore;

    public InfoProtocol(UserStore userStore) {
        super(" ");
        this.userStore = userStore;
    }

    @Override
    protected boolean isCommand(String input) {
        return true;
    }

    @Override
    protected String selectCommand(String command, String params) {
        switch (command) {
            case "list": return list();
            default: return "Unknown command";
        }
    }

    public String list() {
        String usersString = "Online users:";

        for (User user : userStore.getOnlineUsers()) {
            usersString += String.format("* %s %s%s", user.getName(), System.lineSeparator());
        }

        return usersString;
    }
}
