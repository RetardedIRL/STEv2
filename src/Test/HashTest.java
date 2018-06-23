package Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import Enums.HashFunction;
import logic.CryptoManager;

public class HashTest {

	@Test
	public void testHashValidateHash() {
		
		for(HashFunction hashFunction : HashFunction.values()) {
			byte[] input = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,0x07};
	
			String hash = null;
			
			try {
				hash = CryptoManager.generateHash(hashFunction, input);
				
				assertTrue(CryptoManager.isHashValid(hashFunction, input, hash));
			} catch(Exception e) {
				fail();
			}
		}
	}
}
