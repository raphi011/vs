package util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.SecureRandom;
import java.security.Security;

/**
 * Please note that this class is not needed for Lab 1, but can later be
 * used in Lab 2.
 * 
 * Provides security provider related utility methods.
 */
public final class SecurityUtils {

	private SecurityUtils() {
	}

	/**
	 * Registers the {@link BouncyCastleProvider} as the primary security
	 * provider if necessary.
	 */
	public static synchronized void registerBouncyCastle() {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.insertProviderAt(new BouncyCastleProvider(), 0);
		}
	}

	public static byte[] getRandomBytes(int size){
		SecureRandom secureRandom = new SecureRandom();
		final byte[] number = new byte[size];
		secureRandom.nextBytes(number);
		return number;
	}
}
