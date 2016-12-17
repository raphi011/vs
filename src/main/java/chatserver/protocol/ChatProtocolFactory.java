package chatserver.protocol;

import channel.IChannel;
import chatserver.AddressStore;
import chatserver.UserStore;
import connection.IProtocolFactory;
import connection.Protocol;

public class ChatProtocolFactory implements IProtocolFactory {

    private final AddressStore addressStore;
    private final UserStore userStore;

    public ChatProtocolFactory(UserStore userStore, AddressStore addressStore) {
        this.userStore = userStore;
        this.addressStore = addressStore;
    }

    public Protocol newProtocol(IChannel channel) {
        return new ChatProtocol(userStore, addressStore, channel);
    }
}
