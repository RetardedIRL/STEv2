package Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import Enums.HashFunction;
import logic.CryptoManager;

public class HashTest {

	@Test
	public void testHashValidateHash() {
		
		for(HashFunction hashFunction : HashFunction.values()) {
			byte[] input = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
	
			String hash = null;
			
			try {
				hash = CryptoManager.generateHash(hashFunction, input);
				
				assertTrue(CryptoManager.isHashValid(hashFunction, input, hash));
			} catch(Exception e) {
				fail();
			}
		}
	}
	
	@Test
	public void testTampering() {
		byte[] input = new byte[] {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07};
		
		String hash = null;
		
		try {
			hash = CryptoManager.generateHash(HashFunction.SHA1, input);
			
			//System.out.println(new String(input, "UTF-8"));
			input[4] = 0x09;
			
			//System.out.println(new String(input, "UTF-8"));
			assertFalse(CryptoManager.isHashValid(HashFunction.SHA1, input, hash));
		} catch(Exception e) {
		}
	}
}
