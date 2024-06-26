package chatserver.protocol;

import channel.IChannel;
import chatserver.User;
import chatserver.UserStore;
import connection.Protocol;

public class InfoProtocol extends Protocol {

    private final UserStore userStore;

    public InfoProtocol(UserStore userStore, IChannel channel) {
        super(" ", channel);
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
            usersString += String.format(
                    "%s* %s",
                    System.lineSeparator(),
                    user.getName());
        }

        usersString += System.lineSeparator();

        return usersString;
    }
}
