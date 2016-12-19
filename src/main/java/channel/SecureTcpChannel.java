package channel;

import java.io.IOException;
import java.net.Socket;

import util.MyCipher;

public class SecureTcpChannel extends TcpChannel {

	public SecureTcpChannel(Socket socket) {
		super(socket);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	    public void writeLine(String line) {
	    	super.writeLine(MyCipher.encryptRSAtest(line));
	    }

	    @Override
	    public String readLine() throws IOException {
	        return MyCipher.decryptRSAtest(super.readLine());
	    }

}
