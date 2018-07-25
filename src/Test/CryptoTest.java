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
import logic.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CryptoTest {

	@Test
	public void testAllSymmetricAsymmetricEncryptionCombos() {
		
		String input = "test";
		
		for(Operation operation : new Operation[] {Operation.Symmetric, Operation.Asymmetric})

			for(EncryptionType encryption : EncryptionType.getValuesByOperation(operation))

				for(EncryptionMode mode : EncryptionMode.getModeByOperation(operation))

					if(mode != EncryptionMode.CTS)
						for(PaddingType padding : PaddingType.getPaddingByMode(mode))
	
							for(KeyLength keylength : KeyLength.getKeyLength(encryption))
								
								for(HashFunction hashFunction : HashFunction.values()) {
									
									MetaData testMeta = MetaData.getInstance();
									
									testMeta.setOperation(operation);
									testMeta.setEncryptionType(encryption);
									testMeta.setEncryptionMode(mode);
									testMeta.setPaddingType(padding);
									testMeta.setKeyLength(keylength);
									testMeta.setHashFunction(hashFunction);
									
									// now that validity is enforced in GUI I have to include this manually which sucks but oh well what do.
									if(checkValidity(testMeta)) {
										String cleartext = encryptionDecryption(testMeta, input);
										
										System.out.println(encryption + ", " + mode + ", " + padding + ", " + keylength + ", " + hashFunction);
										System.out.println("----------------------------------------\n");
										assertEquals(input, cleartext);
									}
								}
	}
	
	@Test
	public void testCTS() {
		
		String input = "testtesttesttesttesttesttesttesttesttesttesttest";
		
		MetaData testMeta = MetaData.getInstance();
		
		testMeta.setOperation(Operation.Symmetric);
		testMeta.setEncryptionType(EncryptionType.AES);
		testMeta.setEncryptionMode(EncryptionMode.CTS);
		testMeta.setPaddingType(PaddingType.NoPadding);
		testMeta.setKeyLength(KeyLength.x128);
		testMeta.setHashFunction(HashFunction.NONE);
		
		String cleartext = encryptionDecryption(testMeta, input);
		
		assertEquals(input, cleartext);
		
	}
	@Test
	public void testPBE() {
		
		String input = "test";
		String password = "password";
		
		MetaData testMeta = MetaData.getInstance();
		testMeta.setOperation(Operation.Password);
		testMeta.setHashFunction(HashFunction.NONE);
		testMeta.setPassword(password);
		
		for(EncryptionType encryption : EncryptionType.getValuesByOperation(Operation.Password)) {
		
			testMeta.setEncryptionType(encryption);
			
			String cleartext = encryptionDecryption(testMeta, input);
			
			System.out.println(encryption);
			System.out.println("----------------------------------------\n");
			
			assertEquals(input, cleartext);
		}
	}
	
	/**
	 * Test to check whether decryption is only done correctly when using the correct key.
	 * 
	 * Is cleared when decrypted string is unequal to input string.
	 */
	@Test
	public void testWrongKey() {
		
		String input = "test";
		
		MetaData testMeta = MetaData.getInstance();
		
		testMeta.setOperation(Operation.Symmetric);
		testMeta.setEncryptionType(EncryptionType.AES);
		testMeta.setEncryptionMode(EncryptionMode.CFB8);
		testMeta.setPaddingType(PaddingType.NoPadding);
		testMeta.setKeyLength(KeyLength.x128);
		testMeta.setHashFunction(HashFunction.NONE);
		
		try {
			
			CryptoManager.encrypt(input, testMeta);
			
			//Tamper with key
			byte[] key = testMeta.getKey();
			
			key[2] = 0x09;
			testMeta.setKey(key);
			
			CryptoManager.decrypt(testMeta);
			
			assertFalse(input.equals(Utils.toString(testMeta.getText())));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String encryptionDecryption(MetaData meta, String input) {
		
		System.out.println("----------------------------------------");
		
		try {
			
			CryptoManager.encrypt(input, meta);
			
			CryptoManager.decrypt(meta);
		
			System.out.println(input + ", " + Utils.toString(meta.getText()) + ".");
			
			return Utils.toString(meta.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean checkValidity(MetaData currentMeta) {
			
			// since when using PBE all the details are specified in the encryption method, none of this applies, return early
			if(currentMeta.getOperation() != Operation.Password) {
				
				// get the encryption method used
				EncryptionMode mode = currentMeta.getEncryptionMode();
				
				int blockSize = 0;
				
				if(currentMeta.getEncryptionType() != null)
					blockSize = currentMeta.getEncryptionType().getBlockSize();
				
				/* Certain modes - namely ECB and CBC - don't work with NoPadding if the input isn't the same size or multiples of the block size dictated
				 * by the mode, which leads to failure. */
				if((mode == EncryptionMode.ECB || mode == EncryptionMode.CBC) && currentMeta.getPaddingType() == PaddingType.NoPadding) {
					
					// prevent NullpointerException
					if(currentMeta.getText().length > 0 && blockSize != 0) {
						if (currentMeta.getText().length % blockSize != 0) {
							
							return false;
						}
					}
					else {
						return false;
					}
				
				}
				
				if(mode == EncryptionMode.CTS && currentMeta.getText().length < blockSize) {
					return false;
				}
				
				// Here is the rule I talked about towards the beginning, where DES and GCM are incompatible
				if(mode == EncryptionMode.GCM && currentMeta.getEncryptionType() == EncryptionType.DES) {
					
					return false;
				}
			}
			
			// if none of these cases apply, the input metadata is valid and encryption/decryption can proceed.
			return true;
		}
}
