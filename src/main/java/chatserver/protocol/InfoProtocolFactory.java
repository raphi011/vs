package chatserver.protocol;

import chatserver.UserStore;
import connection.IProtocolFactory;
import connection.Protocol;

public class InfoProtocolFactory implements IProtocolFactory {

    private final UserStore userStore;

    public InfoProtocolFactory(UserStore userStore) {
        this.userStore = userStore;
    }

    @Override
    public Protocol newProtocol() {
        return new InfoProtocol(userStore);
    }
}
