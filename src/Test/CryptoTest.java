package Test;

import org.junit.Test;

import Enums.EncryptionType;
import Enums.ModeType;
import Enums.PaddingType;

import src.CryptoManager;


import static org.junit.Assert.assertEquals;

public class CryptoTest {

	@Test
	public void testEncryptions() {
		
		String input = "test";
		EncryptionType encryption = EncryptionType.AES;
		ModeType mode = ModeType.CFB8;
		PaddingType padding = PaddingType.NoPadding;
		

		String cleartext = "";
		try {
			byte[][] ciphertext = CryptoManager.encrypt(input, encryption, mode, padding);
			
			System.out.println(new String(ciphertext[0], "UTF-8") + ", " + new String(ciphertext[1], "UTF-8") + ", " + new String(ciphertext[2], "UTF-8"));
			
			cleartext = CryptoManager.decrypt(ciphertext[0], encryption, mode, padding, ciphertext[1], ciphertext[2]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(input + ", " + cleartext + ".");
		assertEquals(input, cleartext);
	}
}
