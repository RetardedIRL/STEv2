package Test;

import org.junit.Test;

import Enums.HashFunction;
import logic.CryptoManager;

public class HashTest {

	@Test
	public void testHashValidateHash() {
		
		byte[] input = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,0x07};

		String hash = null;
		
		try {
			hash = CryptoManager.generateHash(HashFunction.SHA1, input);
			
			CryptoManager.validateHash(HashFunction.SHA1, input, hash);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
