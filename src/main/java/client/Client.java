package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;

import channel.TcpChannel;
import cli.Command;
import cli.Shell;
import connection.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import util.Config;

public class Client implements IClientCli, Runnable {
	private Log log = LogFactory.getLog(Client.class);

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;
    private TcpChannel tcpChannel;
    private Listener listener;
	private ClientProtocol clientProtocol;
	private Thread listenerThread;
    private Shell shell;

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
	public Client(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;
	}

	@Override
	public void run() {
		String host = config.getString("chatserver.host");
		int tcpPort = config.getInt("chatserver.tcp.port");
		int udpPort = config.getInt("chatserver.udp.port");

		try {
            tcpChannel = new TcpChannel(new Socket(host, tcpPort));
			clientProtocol = new ClientProtocol();
			Connection connection = new Connection(tcpChannel, clientProtocol);
			connection.overrideOut(userResponseStream);
            listenerThread = new Thread(connection);
            listenerThread.start();

			shell = new Shell(componentName, userRequestStream, userResponseStream);
			shell.register(this);
			shell.run();
		} catch (IOException ex) {
			log.error("Unable to connect to server");
		}
	}

	@Override
	@Command
	public String login(String username, String password) throws IOException {
		if (username == null || username.isEmpty()) {
			return "Please enter a username";
		}
		if (password == null || password.isEmpty()) {
			return "Please enter a password";
		}

		tcpChannel.writeLine(String.format("login %s %s", username, password));

		return null;
	}

	@Override
	@Command
	public String logout() throws IOException {
        tcpChannel.writeLine("logout");

		return null;
	}

	@Override
	@Command
	public String send(String message) throws IOException {
		if (message == null || message.isEmpty()) {
			return "Please enter a message.";
		}

		tcpChannel.writeLine(String.format("send %s", message));

		return null;
	}

	@Override
	@Command
	public String list() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Command
	public String msg(String username, String message) throws IOException {
		clientProtocol.addPrivateMessage(username, message);
        tcpChannel.writeLine(String.format("$lookup %s", username));

		return null;
	}

	@Override
	@Command
	public String lookup(String username) throws IOException {
        if (username == null || username.isEmpty()) {
			return "Please enter a username.";
		}

		tcpChannel.writeLine(String.format("lookup %s", username));

		return null;
	}

	@Override
	@Command
	public String register(String privateAddress) throws IOException {
		if (privateAddress == null || privateAddress.isEmpty()){
			return "Please enter an address.";
		}

        tcpChannel.writeLine(String.format("register %s", privateAddress));

		return null;
	}
	
	@Override
	@Command
	public String lastMsg() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Command
	public String exit() throws IOException {
		try {
			tcpChannel.close();
			listenerThread.join();
			shell.close();
		} catch (IOException ex) {
			log.error("error while exiting", ex);
		} catch (InterruptedException ex) { }

		return null;
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Client} component
	 */
	public static void main(String[] args) {
		Client client = new Client(args[0], new Config("client"), System.in,
				System.out);
		client.run();
	}

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	@Override
	public String authenticate(String username) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}