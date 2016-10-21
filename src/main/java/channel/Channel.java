package channel;

import java.io.Closeable;
import java.io.IOException;

public interface Channel extends Closeable {
    Channel accept() throws IOException;
    void shutdown() throws IOException;

    void open() throws IOException;
    void close() throws IOException;
    void writeLine(String line) throws IOException;
    String readLine() throws IOException;
}
