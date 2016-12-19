package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import util.Keys.StaticPasswordReader;

public class MyCipher {

	public static String decryptAEStest(String input){
		return decryptAES("12345678123456781234567812345678".getBytes(), "1234567812345678".getBytes(), input);
	}
	public static String decryptAES(byte[] key, byte[] vector, String input){
		System.out.println("to decrypt: "+input);
		if(input==null)
			return null;
		
		com.sun.org.apache.xml.internal.security.Init.init();
		Cipher cipher=null;
		String ret=null;
		try {
			cipher=Cipher.getInstance("AES/CTR/NoPadding");
			try {
				SecretKeySpec secret= new SecretKeySpec(key,"AES");
				cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(vector));
				ret = new String(cipher.doFinal(Base64.decode(input.getBytes())));
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Base64DecodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("from decrypt: "+ret);
		return ret;
	}
	public static String encryptAEStest(String input){
		return encryptAES("12345678123456781234567812345678".getBytes(), "1234567812345678".getBytes(), input);
	}
	public static String encryptAES(byte[] key, byte[] vector, String input){
		System.out.println("to decrypt: "+input);
		if(input==null)
			return null;
		
		com.sun.org.apache.xml.internal.security.Init.init();
		Cipher cipher=null;
		String ret=null;
		try {
			cipher=Cipher.getInstance("AES/CTR/NoPadding");
			try {
				SecretKeySpec secret= new SecretKeySpec(key,"AES");
				cipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(vector));
				ret = Base64.encode(cipher.doFinal(input.getBytes()));
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("from decrypt: "+ret);
		return ret;
	}
	public static String decryptRSAtest(String input){
		return decryptRSA("keys/chatserver/chatserver.pem","12345",input);
	}
	public static String decryptRSA(String privateKeyPath, String password, String input){
		System.out.println("attemping to decrypt: "+input.length());
		if(input==null)
			return null;
		
		com.sun.org.apache.xml.internal.security.Init.init();
		StaticPasswordReader.setPassword(privateKeyPath, password);
		File f=new File(privateKeyPath);
		String ret=null;
		try {
			PrivateKey key=Keys.readPrivatePEM(f);
			final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");
		      // encrypt the plain text using the public key
		      cipher.init(Cipher.DECRYPT_MODE, key);
		      ret=new String(cipher.doFinal(Base64.decode(input.getBytes())));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Base64DecodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static String encryptRSAtest(String input){
		return encryptRSA("keys/chatserver/chatserver.pub.pem",input);
	}
	public static String encryptRSA(String publicKeyPath, String input){
		if(input==null)
			return null;
		com.sun.org.apache.xml.internal.security.Init.init();
		File f=new File(publicKeyPath);
		String ret=null;
		try {
			PublicKey key=Keys.readPublicPEM(f);
			final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA256AndMGF1Padding");
		      // encrypt the plain text using the public key
		      cipher.init(Cipher.ENCRYPT_MODE, key);
		      ret = Base64.encode(cipher.doFinal(input.getBytes()));
				ret=ret.replace("\n", "");
				ret=ret.replace("\r", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
