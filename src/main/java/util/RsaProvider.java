package util;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;



public class RsaProvider {

	private String cipherString = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";
	private Cipher privateCipher;
	private Cipher publicCipher;

	static {
		com.sun.org.apache.xml.internal.security.Init.init();
	}

	public void setPublicKey(File keyFile) {
		try {
            PublicKey publicKey =Keys.readPublicPEM(keyFile);
            publicCipher = Cipher.getInstance(cipherString);
            publicCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void setPrivateKey(File keyFile, String password) {
		Keys.StaticPasswordReader.setPassword(keyFile.getPath(), password);
		System.out.println("Password: " + password);

		try {
			PrivateKey privateKey = Keys.readPrivatePEM(keyFile);
			privateCipher = Cipher.getInstance(cipherString);
			privateCipher.init(Cipher.DECRYPT_MODE, privateKey);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public String decrypt(String input){
		if(input==null) {
			return null;
		}

		System.out.println("attemping to decrypt: "+input.length());

		String ret=null;
		try {
			ret = new String(privateCipher.doFinal(Base64.decode(input.getBytes())));
		} catch (Base64DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;
	}

	public String encrypt(String input){
		if(input==null) {
			return null;
		}

		String ret=null;

		try {
			ret = Base64.encode(publicCipher.doFinal(input.getBytes()));
			ret=ret.replace("\n", "");
			ret=ret.replace("\r", "");
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("encrypt: "+ret.length());

		return ret;
	}
}