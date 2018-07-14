package Test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.PaddingType;
import logic.CryptoManager;
import persistence.MetaData;

public class TempTest {

	@Test
	public void testShit() {
		
		String input = "test";
		MetaData test = MetaData.getInstance();
		
		test.setOperation(Operation.Symmetric);
		test.setEncryptionType(EncryptionType.AES);
		test.setKeyLength(KeyLength.x128);
		test.setEncryptionMode(EncryptionMode.ECB);
		test.setPaddingType(PaddingType.PKCS7Padding);
		test.setHashFunction(HashFunction.MD5);
		
		try {
			CryptoManager.encrypt(input, test);
			CryptoManager.decrypt(test);
		
		assertEquals(input, new String(test.getText(), "UTF-8"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
