package channel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpListener implements IListener {
    private final ServerSocket serverSocket;

    public TcpListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public IChannel accept() throws IOException {
       return accept(0);
    }

    @Override
    public IChannel accept(int timeout) throws IOException {
        Socket socket = serverSocket.accept();
        if (timeout > 0) {
            socket.setSoTimeout(timeout);
        }

        IChannel channel = new TcpChannel(socket);
        channel.open();
        return channel;
    }

    @Override
    public void shutdown() throws IOException {
        serverSocket.close();
    }
}
