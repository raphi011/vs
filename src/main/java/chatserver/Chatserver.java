package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import channel.TcpListener;
import channel.UdpListener;
import chatserver.protocol.ChatProtocolFactory;
import chatserver.protocol.InfoProtocolFactory;
import cli.Command;
import cli.Shell;
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

	private Shell shell;
	private ConnectionAgent tcpListener;
	private ConnectionAgent udpListener;

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

		userStore.load();

        try {
            tcpListener = new ConnectionAgent("tcpListener",
									   new TcpListener(new ServerSocket(tcpPort)),
									   new ChatProtocolFactory(userStore));
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
