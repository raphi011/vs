package channel;

import java.io.IOException;
import java.net.ServerSocket;

public class TcpListener implements IListener {
    private final ServerSocket serverSocket;

    public TcpListener(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    @Override
    public IChannel accept() throws IOException {
        IChannel channel = new TcpChannel(serverSocket.accept());
        channel.open();
        return channel;
    }

    @Override
    public void shutdown() throws IOException {
        serverSocket.close();
    }
}
