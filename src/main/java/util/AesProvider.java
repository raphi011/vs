package util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.util.encoders.Base64;

public class AesProvider {

    private Cipher decryptCipher;
    private Cipher encryptCipher;

    public AesProvider(byte[] key, byte[] vector) {

        try {
            decryptCipher = Cipher.getInstance("AES/CTR/NoPadding");
            encryptCipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecretKeySpec secret = new SecretKeySpec(key, "AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(vector));
            encryptCipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(vector));
        }catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String decrypt(String input){
        if(input==null)
            return null;
        //System.out.println("to decrypt: "+input);

        String ret=null;

        try {
            ret = new String(decryptCipher.doFinal(Base64.decode(input.getBytes())));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        //System.out.println("from decrypt: "+ret);
        return ret;
    }
    public String encrypt(String input){
        if(input==null) {
            return null;
        }

        byte[] ret = null;

        try {
            ret = Base64.encode(encryptCipher.doFinal(input.getBytes()));
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        //System.out.println("from encrypt: "+ret);
        return new String(ret);
    }
}
