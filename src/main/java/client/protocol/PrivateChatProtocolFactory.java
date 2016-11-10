package client.protocol;

import connection.IProtocolFactory;
import connection.Protocol;

import java.io.PrintStream;

public class PrivateChatProtocolFactory implements IProtocolFactory {
    private final String username;
    private final PrintStream out;

    public PrivateChatProtocolFactory(String username, PrintStream out) {
        this.username = username;
        this.out = out;
    }

    @Override
    public Protocol newProtocol() {
        return new PrivateChatProtocol(username, out);
    }
}
