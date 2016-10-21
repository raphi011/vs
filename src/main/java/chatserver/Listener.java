package chatserver;

import channel.Channel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import chatserver.protocol.Protocol;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener extends Thread {
    private final Log log = LogFactory.getLog(Listener.class);

    private final Channel channel;
    private final Protocol protocol;
    private final String name;

    public Listener(String name, Channel channel, Protocol protocol) {
        this.name = name;
        this.channel = channel;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        log.info(String.format("%s starting up", name));

        ExecutorService pool = Executors.newFixedThreadPool(512);

        while (true) {
            try {
                pool.execute(new Connection(channel.accept(), protocol));
            } catch (IOException ex) {
                // shutdown
                break;
            }
        }

        log.info("shutting down");
        pool.shutdown();
    }

    public void shutdown() throws IOException {
        channel.shutdown();
    }
}
