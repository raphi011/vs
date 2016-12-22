package channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class TcpChannel implements IChannel {

    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TcpChannel(Socket socket) {
        this.socket = socket;
    }

    public void setTimeout(int timeout) throws SocketException {
        socket.setSoTimeout(timeout);
    }

    @Override
    public void open() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void close() throws IOException {
        // this automatically closes in- and output streams
        socket.close();
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public void writeLine(String line) {
        out.println(line);
    }

    @Override
    public String readLine() throws IOException {
        return in.readLine();
    }
}
