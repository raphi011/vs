package chatserver.protocol;

import chatserver.UserStore;

public class ChatProtocolFactory implements IProtocolFactory {

    private UserStore userStore;

    public ChatProtocolFactory(UserStore userStore) {
        this.userStore = userStore;
    }

    public ChatProtocol newProtocol() {
        return new ChatProtocol(userStore);
    }
}
