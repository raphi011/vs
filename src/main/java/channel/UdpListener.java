package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpListener implements IListener {
    private final DatagramSocket datagramSocket;

    public UdpListener(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public IChannel accept() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);

        IChannel channel = new UdpChannel(datagramSocket, packet);
        channel.open();
        return channel;
    }

    @Override
    public void shutdown() throws IOException {
        datagramSocket.close();
    }
}
