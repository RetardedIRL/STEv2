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
	public void testAllEncryptionCombos() {
		
		String input = "test";
		
		for(Operation operation : new Operation[] {Operation.Symmetric, Operation.Asymmetric})

			for(EncryptionType encryption : EncryptionType.getValuesByOperation(operation))

				for(EncryptionMode mode : EncryptionMode.getModeByOperation(operation))

					for(PaddingType padding : PaddingType.getPaddingByMode(mode))

						for(KeyLength keylength : KeyLength.getKeyLength(encryption))
							
							for(HashFunction hashFunction : HashFunction.values()) {
								MetaData testMeta = new MetaData();
								
								testMeta.setOperation(operation);
								testMeta.setEncryptionType(encryption);
								testMeta.setEncryptionMode(mode);
								testMeta.setPaddingType(padding);
								testMeta.setKeyLength(keylength);
								testMeta.setHashFunction(hashFunction);
								
								String cleartext = encryptionDecryption(testMeta, input);
								
								System.out.println(encryption + ", " + mode + ", " + padding + ", " + keylength + ", " + hashFunction);
								System.out.println("----------------------------------------\n");
								assertEquals(input, cleartext);
							}
	}
	
	public String encryptionDecryption(MetaData meta, String input) {
		
		System.out.println("----------------------------------------");
		
		try {
			
			CryptoManager.encrypt(input, meta);
			
			CryptoManager.decrypt(meta);
		
			System.out.println(input + ", " + new String(meta.getText(), "UTF-8") + ".");
			
			return new String(meta.getText(), "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
