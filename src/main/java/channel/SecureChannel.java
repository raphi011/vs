package channel;

import util.AesProvider;

import java.io.IOException;

public class SecureChannel implements IChannel {

	private final AesProvider aesProvider;
	private final IChannel channel;

	public SecureChannel(IChannel channel, AesProvider aesProvider) {
		this.channel = channel;
		this.aesProvider = aesProvider;
	}

	@Override
	public void open() throws IOException {
		channel.open();
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	@Override
	public boolean isOpen() {
		return channel.isOpen();
	}

	@Override
	public void writeLine(String line)  throws IOException {
		String encrypted = aesProvider.encrypt(line);
		channel.writeLine(encrypted);
	}

	@Override
	public String readLine() throws IOException {
		String line = channel.readLine();
		return aesProvider.decrypt(line);
	}
}
