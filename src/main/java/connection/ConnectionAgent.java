package connection;

import channel.IChannel;
import channel.IListener;
import channel.SecureChannelListener;
import chatserver.protocol.ChatProtocolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionAgent extends Thread {
    private final Log log = LogFactory.getLog(ConnectionAgent.class);

    private final String name;
    private final IProtocolFactory protocolFactory;
    private final IListener listener;
    private PrintStream overrideOut;
    private ExecutorService pool;

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

        pool = Executors.newFixedThreadPool(512);

        while (true) {
            try {
                IChannel channel = listener.accept(500);
                String userName = "";
                Connection connection;
                if(listener instanceof  SecureChannelListener) {
                    userName = ((SecureChannelListener) listener).getUser();
                    connection = new Connection(channel, ((ChatProtocolFactory)protocolFactory).newProtocol(channel, userName));
                } else {
                    connection = new Connection(channel, protocolFactory.newProtocol(channel));
                }

                log.info(String.format("%s connected", name));
                if (this.overrideOut != null){
                    connection.overrideOut(this.overrideOut);
                }
                pool.execute(connection);
            } catch (SocketException ex) {
                // shutdown
                break;
            } catch (IOException ex) {
                log.error(ex);
            }
        }

        log.info("shutting down");
        pool.shutdownNow();
    }

    public void shutdown() throws IOException {
        listener.shutdown();
    }
}
