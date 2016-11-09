package chatserver;

import channel.IListener;
import chatserver.protocol.IProtocolFactory;
import connection.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionAgent extends Thread {
    private final Log log = LogFactory.getLog(ConnectionAgent.class);

    private final String name;
    private final IProtocolFactory protocolFactory;
    private final IListener listener;

    public ConnectionAgent(String name, IListener listener, IProtocolFactory protocolFactory) {
        this.name = name;
        this.listener = listener;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public void run() {
        log.info(String.format("%s starting up", name));

        ExecutorService pool = Executors.newFixedThreadPool(512);

        while (true) {
            try {
                pool.execute(new Connection(listener.accept(), protocolFactory.newProtocol()));
            } catch (IOException ex) {
                // shutdown
                break;
            }
        }

        log.info("shutting down");
        pool.shutdown();
    }

    public void shutdown() throws IOException {
        listener.shutdown();
    }
}
