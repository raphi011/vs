package channel;

import java.io.IOException;

public interface IListener {
    IChannel accept() throws IOException;

    void shutdown() throws IOException;
}
