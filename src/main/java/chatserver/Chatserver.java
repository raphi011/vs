package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import channel.SecureChannelListener;
import channel.UdpListener;
import chatserver.protocol.ChatProtocolFactory;
import chatserver.protocol.InfoProtocolFactory;
import cli.Command;
import cli.Shell;
import connection.ConnectionAgent;
import nameserver.INameserverForChatserver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import util.Config;

public class Chatserver implements IChatserverCli, Runnable {
	private Log log = LogFactory.getLog(Chatserver.class);

	private final UserStore userStore;
	private final String componentName;
	private final Config config;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;

	private INameserverForChatserver nameServer;
	private Shell shell;
	private ConnectionAgent tcpListener;
	private ConnectionAgent udpListener;
	private AddressStore addressStore;

	/**
	 * @param componentName
	 *            the name of the component - represented in the prompt
	 * @param config
	 *            the configuration to use
	 * @param userRequestStream
	 *            the input stream to read user input from
	 * @param userResponseStream
	 *            the output stream to write the console output to
	 */
	public Chatserver(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
		this.userStore = new UserStore();
	}

	@Override
	public void run() {
		int tcpPort = config.getInt("tcp.port");
		int udpPort = config.getInt("udp.port");
		String rootId = config.getString("root_id");
		String registryHost = config.getString("registry.host");
		int registryPort = config.getInt("registry.port");
		String publicKeyDir = config.getString("keys.dir");
		String privateKeyPath = config.getString("key");

		try {
			Registry registry = LocateRegistry.getRegistry(registryHost, registryPort);
			nameServer =  (INameserverForChatserver)registry.lookup(rootId);
			addressStore = new AddressStore(nameServer);
		} catch (RemoteException | NotBoundException ex) {
			log.error("Error contacting nameserver", ex);
            System.exit(-1);
		}

		userStore.load();

        try {
            tcpListener = new ConnectionAgent("tcpListener",
									   new SecureChannelListener(new ServerSocket(tcpPort),publicKeyDir, privateKeyPath),
									   new ChatProtocolFactory(userStore, addressStore));
			udpListener = new ConnectionAgent("udpListener",
									   new UdpListener(new DatagramSocket(udpPort)),
									   new InfoProtocolFactory(userStore));
        } catch (IOException ex) {
            log.error("unable to open server socket", ex);
            System.exit(-1);
        }

        tcpListener.start();
		udpListener.start();

        shell = new Shell(componentName, userRequestStream, userResponseStream);
        shell.register(this);
        shell.run();
	}

	@Override
    @Command
	public String users() throws IOException {
		String usersString = "";
		int index = 1;

		for (User user : userStore.getUsersSorted()) {
			usersString += String.format("%d. %s %s%s",
										 index++,
										 user.getName(),
										 user.isOnline() ? "online" : "offline",
										 System.lineSeparator());
		}

		return usersString;
	}

	@Override
	@Command
	public String exit() throws IOException {
        try {
			tcpListener.shutdown();
			udpListener.shutdown();
			tcpListener.join();
			udpListener.join();
		} catch (InterruptedException ex) {
			// dont care ..
		}
        shell.close();

		return null;
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Chatserver}
	 *            component
	 */
	public static void main(String[] args) {
		Chatserver chatserver = new Chatserver(args[0],
				new Config("chatserver"), System.in, System.out);
		chatserver.run();
	}
}
