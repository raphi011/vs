package protocol;

import chatserver.UserStore;

public class ChatProtocol implements Protocol {
    private final UserStore userStore;

    public ChatProtocol(UserStore userStore) {
        this.userStore = userStore;
    }
}
