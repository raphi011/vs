package chatserver.protocol;

import chatserver.UserStore;
import connection.IProtocolFactory;
import connection.Protocol;

public class ChatProtocolFactory implements IProtocolFactory {

    private UserStore userStore;

    public ChatProtocolFactory(UserStore userStore) {
        this.userStore = userStore;
    }

    public Protocol newProtocol() {
        return new ChatProtocol(userStore);
    }
}
