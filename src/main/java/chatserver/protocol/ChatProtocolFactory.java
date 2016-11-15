package chatserver.protocol;

import channel.IChannel;
import chatserver.UserStore;
import connection.IProtocolFactory;
import connection.Protocol;

public class ChatProtocolFactory implements IProtocolFactory {

    private UserStore userStore;

    public ChatProtocolFactory(UserStore userStore) {
        this.userStore = userStore;
    }

    public Protocol newProtocol(IChannel channel) {
        return new ChatProtocol(userStore, channel);
    }
}
