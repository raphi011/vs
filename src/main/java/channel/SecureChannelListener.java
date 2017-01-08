package channel;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import util.AesProvider;
import util.RsaProvider;
import util.SecurityUtils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Observable;

public class SecureChannelListener extends TcpListener {

    private final String publicKeyDir;
    private final String privateKeyPath;

    private String username;

    public SecureChannelListener(
            ServerSocket serverSocket,
            String publicKeyDir,
            String privateKeyPath
            ) {
        super(serverSocket);
        this.publicKeyDir = publicKeyDir;
        this.privateKeyPath = privateKeyPath;
    }

    @Override
    public IChannel accept() throws IOException {
        IChannel channel = super.accept();

        return handshake(channel);
    }

    @Override
    public IChannel accept(int timeout) throws IOException {
        TcpChannel channel = (TcpChannel)super.accept(0);

        IChannel secureChannel = handshake(channel);

        channel.setTimeout(timeout);

        return secureChannel;
    }

    public File getPublicKey(String username) {
        File file = new File(publicKeyDir, username + ".pub.pem");

        if (!file.exists()) {
            return null;
        }

        return file;
    }
    public String getUser() {
        return username;
    }
    private IChannel handshake(IChannel channel) throws IOException {
        RsaProvider rsaProvider = new RsaProvider();
        rsaProvider.setPrivateKey(new File(privateKeyPath), "12345");

        //  1st message
        String request = rsaProvider.decrypt(channel.readLine());
        String[] params = request.split(" ");
        username = params[1];
        String clientChallenge = params[2];
        String serverChallenge = Base64.encode(SecurityUtils.getRandomBytes(32));
        byte[] secretKey = SecurityUtils.getRandomBytes(32);
        byte[] vector = SecurityUtils.getRandomBytes(16);

        File userPublicKey = getPublicKey(username);

        if (userPublicKey == null) {
            channel.close();
            throw new IOException("User doesn't exist");
        }
        //System.out.println("clientChallenge on Server: " + clientChallenge + " | Vector: " + vector);
        // 2nd message
        rsaProvider.setPublicKey(userPublicKey);
        String response = String.format(
                "!ok %s %s %s %s",
                clientChallenge,
                serverChallenge,
                Base64.encode(secretKey),
                Base64.encode(vector));

        String encryptedResponse = rsaProvider.encrypt(response);
        channel.writeLine(encryptedResponse);

        // 3d message
        AesProvider aesProvider = new AesProvider(secretKey, vector);

        request = aesProvider.decrypt(channel.readLine());

        if (!request.equals(serverChallenge)) {
            channel.close();
            throw new IOException("Handshake failed");
        }

        return new SecureChannel(channel, aesProvider);
    }
}
