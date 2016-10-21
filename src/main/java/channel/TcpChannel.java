package channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpChannel implements Channel {

    private final ServerSocket serverSocket;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public TcpChannel(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.socket = null;
    }

    private TcpChannel(Socket socket) {
        this.serverSocket = null;
        this.socket = socket;
    }

    @Override
    public Channel accept() throws IOException {
        Channel channel = new TcpChannel(serverSocket.accept());
        channel.open();
        return channel;
    }

    @Override
    public void shutdown() throws IOException {
        serverSocket.close();
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
    public void writeLine(String line) {
        out.println(line);
    }

    @Override
    public String readLine() throws IOException {
        return in.readLine();
    }
}
