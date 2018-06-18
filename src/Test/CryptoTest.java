package Test;

import org.junit.Test;

import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.EncryptionMode;
import Enums.PaddingType;

import persistence.MetaData;

import src.CryptoManager;


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

		String cleartext = "";
		try {
			byte[][] ciphertext = CryptoManager.encrypt(input, testMeta);
			
			cleartext = CryptoManager.decrypt(ciphertext[0], testMeta, ciphertext[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(input + ", " + cleartext + ".");
		assertEquals(input, cleartext);
	}
}
