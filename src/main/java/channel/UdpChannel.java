package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpChannel implements IChannel {

    private final DatagramSocket socket;
    private final DatagramPacket packet;

    private String content;
    private int index;
    private int length;

    public UdpChannel(DatagramSocket socket, DatagramPacket datagramPacket) {
        this.socket = socket;
        this.packet = datagramPacket;
    }

    @Override
    public void open() throws IOException {
        content = new String(packet.getData());
        length = packet.getLength();
        index = 0;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public void writeLine(String line) throws IOException {
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        byte[] buffer = line.getBytes();


        socket.send(new DatagramPacket(buffer,
                                               buffer.length,
                                               address,
                                               port));
    }

    @Override
    public String readLine() throws IOException {
        int newLineIndex = content.indexOf('\n', index);
        String line = content.substring(index, newLineIndex);
        index = newLineIndex;

        return line;
    }
}