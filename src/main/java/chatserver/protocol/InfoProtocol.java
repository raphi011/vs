package chatserver.protocol;

import chatserver.User;
import chatserver.UserStore;

public class InfoProtocol extends Protocol {

    private final UserStore userStore;

    public InfoProtocol(UserStore userStore) {
        this.userStore = userStore;
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
