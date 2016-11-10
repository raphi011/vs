package channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpChannel implements IChannel {

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    private DatagramPacket packet;
    private String content;
    private int pos;
    private int length;

    public UdpChannel(DatagramSocket socket,
                      DatagramPacket datagramPacket) {
        this.socket = socket;
        this.packet = datagramPacket;
        this.address = packet.getAddress();
        this.port = packet.getPort();

    }

    public UdpChannel(DatagramSocket socket,
                      InetAddress address,
                      int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    public void setPacket(DatagramPacket packet) {
        this.packet = packet;
    }

    @Override
    public void open() throws IOException {
        content = new String(packet.getData(), 0, packet.getLength());
        length = content.length();
        pos = 0;
    }

    @Override
    public void close() throws IOException {
        // intentionally does nothing
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public void writeLine(String line) throws IOException {
        byte[] buffer = line.getBytes();

        socket.send(new DatagramPacket(buffer,
                                               buffer.length,
                                               address,
                                               port));
    }

    @Override
    public String readLine() throws IOException {
        if (pos == length) {
            return null;
        }

        int newPos = content.indexOf('\n', pos);

        if (newPos == -1) {
            newPos = length;
        }

        String line = content.substring(pos,
                                        newPos == -1 ?
                                            content.length() :
                                            newPos);
        pos = newPos;

        return line;
    }
}