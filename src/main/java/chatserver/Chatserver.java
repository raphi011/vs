package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;

import channel.TcpChannel;
import cli.Command;
import cli.Shell;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import protocol.ChatProtocol;
import util.Config;

public class Chatserver implements IChatserverCli, Runnable {
	private Log log = LogFactory.getLog(Chatserver.class);

	private final UserStore userStore;
	private final String componentName;
	private final Config config;
	private final InputStream userRequestStream;
	private final PrintStream userResponseStream;

	private Shell shell;
	private Listener tcpListener;

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
		userStore.load();

        try {
            tcpListener = new Listener("tcpListener",
									   new TcpChannel(new ServerSocket(tcpPort)),
									   new ChatProtocol(userStore));
        } catch (IOException ex) {
            log.error("unable to open server socket", ex);
            System.exit(-1);
        }

        tcpListener.start();

        shell = new Shell(componentName, userRequestStream, userResponseStream);
        shell.register(this);
        shell.run();
	}

	@Override
    @Command
	public String users() throws IOException {
		return "users command";
	}

	@Override
	@Command
	public String exit() throws IOException {
        try {
			tcpListener.shutdown();
			tcpListener.join();
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
