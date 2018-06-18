package Test;

import org.junit.Test;

import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.EncryptionMode;
import Enums.PaddingType;

import persistence.MetaData;

import logic.CryptoManager;


import static org.junit.Assert.assertEquals;

public class CryptoTest {

	@Test
	public void testEncryptDecrypt() {
		
		String input = "test";
		EncryptionType encryption = EncryptionType.AES;
		EncryptionMode mode = EncryptionMode.CFB8;
		PaddingType padding = PaddingType.NoPadding;
		
		MetaData testMeta = new MetaData();
		
		testMeta.setEncryptionType(encryption);
		testMeta.setEncryptionMode(mode);
		testMeta.setPaddingType(padding);
		testMeta.setKeyLength(KeyLength.x128);
		testMeta.setHashFunction(HashFunction.SHA1);

		String cleartext = encryptionDecryption(testMeta, input);
		
		assertEquals(input, cleartext);
	}
	
	@Test
	public void testAllEncryptionTypes() {
		
		String input = "test";
		
		for(EncryptionType encryption : EncryptionType.getValuesByOperation(Operation.Symmetric)) {
			
			for(EncryptionMode mode : EncryptionMode.getModeByOperation(Operation.Symmetric)) {
				
				for(PaddingType padding : PaddingType.getPaddingByMode(mode)) {
					
					for(KeyLength keylength : KeyLength.getKeyLength(encryption)) {
						
						MetaData testMeta = new MetaData();
						
						testMeta.setEncryptionType(encryption);
						testMeta.setEncryptionMode(mode);
						testMeta.setPaddingType(padding);
						testMeta.setKeyLength(keylength);
						testMeta.setHashFunction(HashFunction.SHA1);
						
						String cleartext = encryptionDecryption(testMeta, input);
						
						System.out.println(encryption + ", " + mode + ", " + padding + ", " + keylength);
						assertEquals(input, cleartext);
					}
				}
			}
		}
	}
	
	public String encryptionDecryption(MetaData meta, String input) {
		try {
			byte[] ciphertext = CryptoManager.encrypt(input, meta);
			
			String cleartext = CryptoManager.decrypt(ciphertext, meta);
			
			System.out.println(input + ", " + cleartext + ".");
			
			return cleartext;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
