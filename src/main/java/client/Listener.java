package client;

import channel.IChannel;

import java.io.IOException;
import java.io.PrintStream;

public class Listener extends Thread {
    private final IChannel channel;
    private final PrintStream out;

    public Listener(IChannel channel, PrintStream out) {
        this.channel = channel;
        this.out = out;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = channel.readLine();
                out.println(message);
            }
        } catch (IOException ex) {
            return;
        }
    }
}
