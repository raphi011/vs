package chatserver;

import channel.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chatserver.protocol.Protocol;

import java.io.IOException;

public class Connection implements Runnable {
    private final Log log = LogFactory.getLog(Listener.class);

    private final Channel channel;
    private final Protocol protocol;

    public Connection(Channel channel, Protocol protocol) {
        this.channel = channel;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        String inputLine;
        try {
            channel.open();
            log.info("chatprotocol connected");

            while ((inputLine = channel.readLine()) != null) {
                channel.writeLine(inputLine);
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
