package channel;

import java.io.Closeable;
import java.io.IOException;

public interface IChannel extends Closeable {

    void open() throws IOException;

    void close() throws IOException;

    boolean isOpen();

    void writeLine(String line) throws IOException;

    String readLine() throws IOException;
}
