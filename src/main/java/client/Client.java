package client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.*;

import channel.*;
import chatserver.UserStore;
import client.protocol.ClientProtocol;
import client.protocol.PrivateChatProtocolFactory;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import connection.ConnectionAgent;
import cli.Command;
import cli.Shell;
import connection.Connection;
import connection.ReadProtocolFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import util.*;

public class Client implements IClientCli, Runnable {
	private Log log = LogFactory.getLog(Client.class);

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;
    private IChannel tcpChannel;
	private IChannel secureChannel;
	private Thread tcpListenerThread;
	private ClientProtocol clientProtocol;
    private ConnectionAgent udpListener;
	private ConnectionAgent tcpListener;
    private Shell shell;
	private DatagramSocket udpSocket;
    private UdpChannel udpChannel;

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
			// tcp server - moved to !authenticate
			//tcpChannel = new TcpChannel(new Socket(host, tcpPort));
			//clientProtocol = new ClientProtocol(tcpChannel);
			//Connection tcpConnection = new Connection(tcpChannel, clientProtocol);
			//tcpConnection.overrideOut(userResponseStream);
			//tcpListenerThread = new Thread(tcpConnection, "clientprotocol");
			//tcpListenerThread.start();

			// udp
			udpSocket = new DatagramSocket();
            udpListener = new ConnectionAgent("udpListener",
											  new UdpListener(udpSocket),
											  new ReadProtocolFactory());
            udpListener.overrideOut(userResponseStream);
			udpChannel = new UdpChannel(
					udpSocket,
					InetAddress.getByName(host),
					udpPort);
			udpListener.start();

			// shell
			shell = new Shell(componentName, userRequestStream, userResponseStream);
			shell.register(this);
			shell.run();
		} catch(ConnectException ex) {
			userResponseStream.println("Could not connect to the server.");
		} catch (IOException ex) {
			log.error("error occured while starting up", ex);
            userResponseStream.println("error occured while starting up.");
		}
	}

	@Override
	@Command
	public String login(String username, String password) throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
		if (username == null || username.isEmpty()) {
			return "Please enter a username";
		}
		//if (password == null || password.isEmpty()) {
		//	return "Please enter a password";
		//}

		tcpChannel.writeLine(String.format("login %s %s", username, password));

		return null;
	}

	@Override
	@Command
	public String logout() throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
        tcpChannel.writeLine("logout");

		return null;
	}

	@Override
	@Command
	public String send(String message) throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
		if (message == null || message.isEmpty()) {
			return "Please enter a message.";
		}

		//tcpChannel.writeLine(String.format("send %s", message));
		tcpChannel.writeLine(String.format("send %s", message));

		return null;
	}

	@Override
	@Command
	public String list() throws IOException {
        udpChannel.writeLine("list");

		return null;
	}

	@Override
	@Command
	public String msg(String username, String message) throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
		clientProtocol.addPrivateMessage(username, message);
        tcpChannel.writeLine(String.format("$lookup %s", username));

		return null;
	}

	@Override
	@Command
	public String lookup(String username) throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
        if (username == null || username.isEmpty()) {
			return "Please enter a username.";
		}

		tcpChannel.writeLine(String.format("lookup %s", username));

		return null;
	}

	@Override
	@Command
	public String register(String privateAddress) throws IOException {
		if (tcpChannel == null) {
			return "Authenticate before executing other commands.";
		}
		if (tcpListener != null) {
			return "Already registered.";
		}

		String username = clientProtocol.getUsername();
		if (username == null || username.isEmpty()) {
			return "Not logged in.";
		}

		if (privateAddress == null || privateAddress.isEmpty()) {
			return "Please enter an address.";
		}

		String[] address = privateAddress.split(":");

		if (address.length != 2) {
			return "Please enter the address in the following format: 'host:port'";
		}

		try {
			int port = Integer.parseInt(address[1]);
			tcpListener = new ConnectionAgent("tcpListener",
											  new TcpListener(new ServerSocket(port)),
											  new PrivateChatProtocolFactory(username, userResponseStream));
            tcpListener.start();
			tcpChannel.writeLine(String.format("register %s", privateAddress));
		} catch (IOException ex) {
            log.error("error starting private chat listener", ex);
		} catch (NumberFormatException ex) {
			return "Please enter a valid port.";
		}

		return null;
	}
	
	@Override
	@Command
	public String lastMsg() throws IOException {
		if (clientProtocol == null) {
			return "Authenticate before executing other commands.";
		}
        String message = clientProtocol.getLastMessage();

		if (message == null || message.isEmpty()) {
			return "No message received!";
		}

		return message;
	}

	@Override
	@Command
	public String exit() throws IOException {
		try {
            udpSocket.close();
			tcpChannel.close();
            if (tcpListener != null) {
				tcpListener.shutdown();
				tcpListener.join();
			}
			tcpListenerThread.join();
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
		client.run();		System.out.println("273");
	}

	// --- Commands needed for Lab 2. Please note that you do not have to
	// implement them for the first submission. ---

	@Override
	@Command
	public String authenticate(String username) throws IOException {

		String host = config.getString("chatserver.host");
		int tcpPort = config.getInt("chatserver.tcp.port");
		tcpChannel = new TcpChannel(new Socket(host, tcpPort));
		tcpChannel.open();

		if (username == null || username.isEmpty()) {
			return "Please enter a username";
		}

		File privateKeyfile = new File(config.getString("keys.dir"), username + ".pem");

		if (!privateKeyfile.exists()) {
			return "This user doesn't exist";
		}

		File publicKeyfile = new File(config.getString("chatserver.key"));

		if (!publicKeyfile.exists()) {
			return "Missing server keyfile.";
		}

		RsaProvider rsaProvider = new RsaProvider();
		rsaProvider.setPublicKey(publicKeyfile);
		/* Pwd is discarded currently. */
		Keys.PasswordReader pwdReader  = new Keys.PasswordReader(username);
		char pwd[] = pwdReader.getPassword();
		rsaProvider.setPrivateKey(privateKeyfile, new String(pwd));
		String challenge=Base64.encode(SecurityUtils.getRandomBytes(32));
		String request = String.format("!authenticate %s %s", username, challenge);
		tcpChannel.writeLine(rsaProvider.encrypt(request));

		String response = rsaProvider.decrypt(tcpChannel.readLine());

		String[] params = response.split(" ");

		if (!(challenge.equals(params[1]))) {
			return "Wrong challenge";
		}

		try {
			System.out.println("Challenge: " + challenge + " | Vector: " + params[4] + " | " + Base64.decode(params[4]).length);
		} catch (Base64DecodingException e1) {}

		AesProvider aesProvider = null;
		try {
			aesProvider = new AesProvider(Base64.decode(params[3]), Base64.decode(params[4]));
		} catch (Base64DecodingException e1) {}

		secureChannel = new SecureChannel(tcpChannel, aesProvider);

		secureChannel.writeLine(params[2]);

		clientProtocol = new ClientProtocol(secureChannel);
		clientProtocol.setUserName(username);
		Connection tcpConnection = new Connection(secureChannel, clientProtocol);
		tcpConnection.overrideOut(userResponseStream);
		tcpChannel = secureChannel;
		tcpListenerThread = new Thread(tcpConnection, "clientprotocol");
		tcpListenerThread.start();

		return null;
	}
}