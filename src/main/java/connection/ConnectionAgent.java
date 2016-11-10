package connection;

import channel.IListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionAgent extends Thread {
    private final Log log = LogFactory.getLog(ConnectionAgent.class);

    private final String name;
    private final IProtocolFactory protocolFactory;
    private final IListener listener;
    private PrintStream overrideOut;

    public ConnectionAgent(String name, IListener listener, IProtocolFactory protocolFactory) {
        this.name = name;
        this.listener = listener;
        this.protocolFactory = protocolFactory;
    }

    public void overrideOut(PrintStream out) {
        overrideOut = out;
    }

    @Override
    public void run() {
        log.info(String.format("%s starting up", name));

        ExecutorService pool = Executors.newFixedThreadPool(512);

        while (true) {
            try {
                Connection connection = new Connection(listener.accept(), protocolFactory.newProtocol());
                log.info(String.format("%s connected", name));
                if (this.overrideOut != null){
                    connection.overrideOut(this.overrideOut);
                }
                pool.execute(connection);
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
