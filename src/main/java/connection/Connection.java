package connection;

import channel.IChannel;
import chatserver.ConnectionAgent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.omg.CORBA.Environment;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

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
        String input;
        try {
            channel.open();
            protocol.setChannel(channel);
            log.info("chatprotocol connected");

            while ((input = channel.readLine()) != null) {
                String output = protocol.nextCommand(input);
                if (output != null && !output.equals("")) {
                    if (overrideOut != null) {
                       overrideOut.println(output);
                    } else {
                        channel.writeLine(output);
                    }
                }
            }
        } catch (IOException e) {
            log.warn(e);
        } finally {
            try {
                channel.close();
            } catch (IOException ex) {
                log.warn(ex);
            }
        }
    }
}
