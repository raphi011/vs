package connection;

import channel.IChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Connection implements Runnable {
    private final Log log = LogFactory.getLog(ConnectionAgent.class);

    private final IChannel channel;
    private final Protocol protocol;
    private PrintStream overrideOut;

    public Connection(IChannel channel, Protocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    public void overrideOut(PrintStream out) {
        overrideOut = out;
    }

    @Override
    public void run() {
        try {
            channel.open();

            while (!Thread.interrupted()) {
                try {
                    String input = channel.readLine();

                    if (input == null) {
                        break;
                    }
                    String output = protocol.nextCommand(input);
                    if (output != null && !output.equals("")) {
                        if (overrideOut != null) {
                           overrideOut.println(output);
                        } else {
                            channel.writeLine(output);
                        }
                    }
                } catch (SocketTimeoutException ex) { }
            }
        } catch (SocketException e) {
            if (!e.getMessage().contains("closed")) {
                log.error(e);
            }
        } catch (IOException e) {
            log.error(e);
        } finally {
            try {
                channel.close();
            } catch (IOException ex) {
                log.error(ex);
            }
            protocol.onClosed();
        }
    }
}