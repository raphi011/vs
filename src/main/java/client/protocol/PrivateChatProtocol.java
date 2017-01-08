package client.protocol;

import channel.IChannel;
import connection.Protocol;
import util.Config;
import util.Keys;
import org.bouncycastle.util.encoders.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class PrivateChatProtocol extends Protocol {
    private final String username;
    private final PrintStream out;
    private Config config=new Config("client");

    public PrivateChatProtocol(String username, PrintStream out, IChannel channel) {
        super("\\|", channel);
        this.username = username;
        this.out = out;
    }

    @Override
    protected boolean isCommand(String input) {
        return false;
    }

    @Override
    protected String selectCommand(String command, String input) {
        String[] params = splitParams(input);
        int index= params[1].indexOf(' ');
        String hmac=params[1].substring(0,index);
        String message=params[1].substring(index+1);
        byte[] genhmac=hmac(message);
        out.println(String.format("%s %s: %s", hmac, params[0], message));
        if(MessageDigest.isEqual(genhmac, Base64.decode(hmac.getBytes()))) {
            return "!ack";
        }
        else
        	return new String(Base64.encode(genhmac))+" !tampered "+message;
    }

    private byte[] hmac(String message){
    	Key secretKey=null;
    	try {
    		secretKey = Keys.readSecretKey(new File(config.getString("hmac.key")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Mac hMac=null;
    	try {
			hMac = Mac.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			hMac.init(secretKey);
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	hMac.update(message.getBytes());
    	byte[] hash = hMac.doFinal();
    	return hash;
    }
}