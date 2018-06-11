package src;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Enums.EncryptionType;
import Enums.ModeType;
import Enums.PaddingType;

public class CryptoManager {
    
    //Temporary byte array for pseudorandom IV generation
    static byte[] msgNumber = new byte[] {
    	0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };
    
    /**
     * Encrypts a String to a byte array using various encryption methods, modes and padding types
     * by making use of the BouncyCastle - provider.
     * 
     * @param input To encryptable string.
     * @param encryption Encryption Method
     * @param mode Encryption Mode
     * @param padding Padding
     * @return encrypted byte array
     */
    public static byte[][] encrypt(String input, EncryptionType encryption, ModeType mode, PaddingType padding) throws Exception {
		
    	byte[] inputBytes = input.getBytes();
    	Key key;
		
		if(encryption == EncryptionType.none) {
			return new byte[][] {inputBytes, null, null};
		}
		
		Cipher cipher = Cipher.getInstance(encryption.toString() + "/" + mode.toString() + "/" + padding.toString());
		
		key = generateKey(encryption);

		// Complete check for validity of parameters chosen -- TODO: Stream cipher mode block size
		if(Logic.isValid(encryption, mode, padding, inputBytes, cipher.getBlockSize())) {
			
			byte[] iv = null;
			//If mode requires IV
			switch(Logic.requiresIV(mode)) {
			
			//no IV
			case -1:
				
				System.out.println("no IV");
				
				cipher.init(Cipher.ENCRYPT_MODE, key);
				break;
				
			//normal inline IV
			case 0:
				
				System.out.println("Normal inline IV");
				
				iv = new byte[cipher.getBlockSize()];
				IvParameterSpec zeroIV = new IvParameterSpec(iv);
				
				cipher.init(Cipher.ENCRYPT_MODE, key, zeroIV);
				
				IvParameterSpec encryptionIV = new IvParameterSpec(cipher.doFinal(msgNumber), 0, 8);
				
				cipher.init(Cipher.ENCRYPT_MODE, key, encryptionIV);
				break;
				
			//Stream cipher IV
			case 1:
				
				System.out.println("Stream cipher IV");
				
				iv = new byte[] {0x07, 0x06, 0x05, 0x04, 0x00, 0x00, 0x00, 0x01};
				
				IvParameterSpec tempStreamIVSpec = new IvParameterSpec(iv);
				
				cipher.init(Cipher.ENCRYPT_MODE, key, tempStreamIVSpec);
				break;
				
			default:
				break;
			}
			
			byte[] cipherText = new byte[cipher.getOutputSize(inputBytes.length)];
			
			int ctLength = cipher.update(inputBytes, 0, inputBytes.length, cipherText, 0);
			
			ctLength += cipher.doFinal(cipherText, ctLength);
			
			return new byte[][] {cipherText, key.getEncoded(), iv};
    	}
		else
			throw new Exception("input not valid");
	}

    /**
     * Decrypts a byte array to a String using various encryption methods, modes and padding types
     * by making use of the BouncyCastle - provider.
     * @param input to decryptable byte array
     * @param encryption Encryption Method
     * @param mode Mode
     * @param padding Padding
     * @return Encrypted String.
     * @throws Exception
     */
	public static String decrypt(byte[] input, EncryptionType encryption, ModeType mode, PaddingType padding, byte[] keyBytes, byte[] iv) throws Exception {
		
		byte[] inputBytes = input;

		SecretKeySpec key = new SecretKeySpec(keyBytes, "BC");
		
		if(encryption == EncryptionType.none) {
			return new String(inputBytes, "UTF-8");
		}
		
		Cipher cipher = Cipher.getInstance(encryption.toString() + "/" + mode.toString() + "/" + padding.toString());
		
		//TODO: Generate IV by reading from metadata
		//If mode requires IV
		switch(Logic.requiresIV(mode)) {
			
		//no IV
		case -1:
			cipher.init(Cipher.DECRYPT_MODE, key);
			break;
		
		//normal IV
		case 0:
			IvParameterSpec zeroIV = new IvParameterSpec(iv);
			
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIV);
			
			IvParameterSpec encryptionIV = new IvParameterSpec(cipher.doFinal(msgNumber), 0, 8);
			
			cipher.init(Cipher.DECRYPT_MODE, key, encryptionIV);
			break;
		
		// Stream cipher IV
		case 1:
					
			IvParameterSpec tempStreamIVSpec = new IvParameterSpec(iv);
			
			cipher.init(Cipher.DECRYPT_MODE, key, tempStreamIVSpec);
			
			break;
			
		default:
			break;
		}
		
		byte[] cipherText = new byte[cipher.getOutputSize(inputBytes.length)];
		
		int ctLength = cipher.update(inputBytes, 0, inputBytes.length, cipherText, 0);
		
		ctLength += cipher.doFinal(cipherText, ctLength);
		
		return new String(cipherText, "UTF-8");
	}

	
	/**
	 * Method to generate a random key using the KeyGenerator class.
	 * 
	 * @param encryption Encryption Method
	 * @return generated Key
	 * @throws Exception
	 */
	static Key generateKey(EncryptionType encryption) throws Exception {
		int strength;
		
		switch(encryption) {
		
			case DES:
				strength = 64;
				break;
			case AES:
				strength = 192;
				break;
			default:
				throw new Exception("Unknown Encryption Strength");
		}
		
		KeyGenerator generator = KeyGenerator.getInstance(encryption.toString(), "BC");
		
		generator.init(strength);
		return generator.generateKey();
	}
}
