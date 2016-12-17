package nameserver;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;

import nameserver.exceptions.AlreadyRegisteredException;
import nameserver.exceptions.InvalidDomainException;
import util.Config;

/**
 * Please note that this class is not needed for Lab 1, but will later be used
 * in Lab 2. Hence, you do not have to implement it for the first submission.
 */
public class Nameserver implements INameserverCli, INameserver, Runnable {

	private String componentName;
	private Config config;
	private InputStream userRequestStream;
	private PrintStream userResponseStream;

	private ConcurrentHashMap<String, INameserver> children;
	private ConcurrentHashMap<String, String> users;

	private Registry registry;
	private boolean isRootServer = false;
	private String rootNameServerID = "";
	private String domain = "";
	private String port;
	private String host;

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
	public Nameserver(String componentName, Config config,
			InputStream userRequestStream, PrintStream userResponseStream) {
		this.componentName = componentName;
		this.config = config;
		this.userRequestStream = userRequestStream;
		this.userResponseStream = userResponseStream;

		// TODO
		children = new ConcurrentHashMap<>();
		users = new ConcurrentHashMap<>();

		// TODO Read config
		rootNameServerID = config.getString("root_id");
		host = config.getString("registry.host");
		port = config.getString("registry.port");
		try {
			domain = config.getString("domain");
		} catch (Exception e) {
			domain = null;
			isRootServer = true;
		}
	}

	@Override
	public void run() {
		try {
            if (isRootServer) {
                registry = LocateRegistry.createRegistry(Integer.parseInt(port));
				INameserver stub1 = (INameserver)UnicastRemoteObject.exportObject(this, 0);
				registry.bind(rootNameServerID, stub1);
            } else {
                registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
				INameserver stub = (INameserver)registry.lookup(rootNameServerID);
				stub.registerNameserver(domain, (INameserver)UnicastRemoteObject.exportObject(this,0), null);
            }
		} catch (Exception e) {
			System.err.println("Client exception: " + e.toString());
			e.printStackTrace();
			try {
				exit();
			} catch(IOException e1) {

			}
			return;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(userRequestStream));
		String s = "";
		try {
			while ((s = br.readLine()) != null) {
				if (s.equals("!addresses")) {
					userResponseStream.print(addresses());
					userResponseStream.flush();
				}

				if (s.equals("!nameservers")) {
					userResponseStream.print(nameservers());
					userResponseStream.flush();
				}

				if (s.equals("!exit")) {
					exit();
					break;
				}

			}
		} catch (IOException e) {
			;
		}
	}

	@Override
	public String nameservers() throws IOException {
		// Read Semaphore for children
		String returnString = "";
		for(String key : children.keySet()) {
			returnString += (key+"\n");
		}
		//Close Semaphore children
		return returnString;
	}

	@Override
	public String addresses() throws IOException {
		String returnString = "";
		//Read Semaphore for users
		for(String key : users.keySet()) {
			returnString += key + " " + users.get(key) + "\n";
		}
		//Close Read Semaphore
		return returnString;
	}

	@Override
	public String exit() throws IOException {
		UnicastRemoteObject.unexportObject(this, true);
		if (isRootServer) {
			try {
				registry.unbind(rootNameServerID);
			} catch (Exception e) {
			}
			UnicastRemoteObject.unexportObject(registry, true);
		}
		return null;
	}

	/**
	 * @param args
	 *            the first argument is the name of the {@link Nameserver}
	 *            component
	 */
	public static void main(String[] args) {
		Nameserver nameserver = new Nameserver(args[0], new Config(args[0]),
				System.in, System.out);
		// TODO: start the nameserver
		nameserver.run();
	}

	@java.lang.Override
	public void registerUser(String username, String address) throws RemoteException, AlreadyRegisteredException, InvalidDomainException {
		int index = username.lastIndexOf('.');
		if(index==-1){
			//insert write semaphore users
			if (users.containsKey(username)){
				//close write semaphore users
				throw new AlreadyRegisteredException("user already registered");
			}
			users.put(username,address);
			System.out.println("RegisterUser success: " + username);
			//close write semaphore
		}else{
			String end=username.substring(index+1);
			String next=username.substring(0,index);

			//insert read semaphore children
			if(children.containsKey(end)==false){
				//close read semaphore children
				throw new InvalidDomainException("No domain with name "+end+" found");
			}

			INameserver userTest = (INameserver)children.get(end);
			//close read semaphore children
			System.out.println("RegisterUser recursion: " + next);
			userTest.registerUser(next, address);
		}
	}

	@java.lang.Override
	public INameserverForChatserver getNameserver(String zone) throws RemoteException {
		System.out.println("getNameserver called");
		//insert read semaphore children
		if(children.containsKey(zone)== false){
			//close read semaphore children
			throw new RemoteException("Domain " + zone + "not found!");
		}
		INameserverForChatserver ret = (INameserverForChatserver)children.get(zone);
		//close read semaphore children
		return ret;
	}

	@java.lang.Override
	public String lookup(String username) throws RemoteException {
		System.out.println("Started lookup");
		//insert read semaphore users
		if(users.containsKey(username)==false){
			//close read semaphore users
			throw new RemoteException();
		}
		String ret = users.get(username);
		//close read semaphore users();

		return ret;
	}

	@java.lang.Override
	public void registerNameserver(String domain, INameserver nameserver,
								   INameserverForChatserver nameserverForChatserver)
			throws RemoteException, AlreadyRegisteredException,
			InvalidDomainException {

		int index = domain.lastIndexOf('.');
		if(index==-1){
			//insert write semaphore domain
			if (children.containsKey(domain)){
				//close write semaphore domain
				throw new AlreadyRegisteredException("domain already registered");
			}
			children.put(domain, nameserver);
			System.out.println("Successfully inserted " + domain);
			//close write semaphore domain /children
		}else{
			String end=domain.substring(index+1);
			String next=domain.substring(0,index);

			//insert read semaphore children
			if(children.containsKey(end)==false){
				//close read semaphore children
				throw new InvalidDomainException("No domain with name "+end+" found");
			}
			INameserver serverTest = (INameserver)children.get(end);
			//close read semaphore children

			System.out.println("registerNameserver recursion: " + next);
			serverTest.registerNameserver(next, nameserver, null);
		}

	}
}
