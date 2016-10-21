package channel;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpChannel implements Channel {

    private final DatagramSocket datagramSocket;
    private final DatagramPacket datagramPacket;

    private String content;
    private int index;
    private int length;

    public UdpChannel(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
        this.datagramPacket = null;
    }

    private UdpChannel(DatagramPacket datagramPacket) {
        this.datagramSocket = null;
        this.datagramPacket = datagramPacket;
    }

    @Override
    public Channel accept() throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        datagramSocket.receive(packet);

        Channel channel = new UdpChannel(packet);
        channel.open();
        return channel;
    }

    @Override
    public void shutdown() throws IOException {
        datagramSocket.close();
    }

    @Override
    public void open() throws IOException {
        content = new String(datagramPacket.getData());
        length = datagramPacket.getLength();
        index = 0;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void writeLine(String line) throws IOException {
        InetAddress address = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        byte[] buffer = line.getBytes();


        datagramSocket.send(new DatagramPacket(buffer,
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
