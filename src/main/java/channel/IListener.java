package channel;

import java.io.IOException;

public interface IListener {
    IChannel accept() throws IOException;
    IChannel accept(int timeout) throws IOException;

    void shutdown() throws IOException;
}
