package src;

import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Enums.EncryptionType;
import Enums.HashFunction;
import persistence.MetaData;

public class CryptoManager {
    
    public static byte[][] encrypt(String input, MetaData meta) throws Exception {
		
    	byte[] inputBytes = input.getBytes();
		
		if(meta.getEncryptionType() == EncryptionType.none) {
			meta.setHashValue(generateHash(meta.getHashFunction(), inputBytes));
			return new byte[][] {inputBytes, null};
		}
		
		Key key = generateKey(meta);
		
		IvParameterSpec iv = getMatchingIV(meta);
		
		meta.setIV(iv.getIV());
		
		Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE, meta, key, iv);
		
		byte[] ciphertext = applyCipher(cipher, input.getBytes());
		
		meta.setHashValue(generateHash(meta.getHashFunction(), ciphertext));
		
		return new byte[][] {ciphertext, key.getEncoded()};
	}
    
    public static String decrypt(byte[] input, MetaData meta, byte[] key) throws Exception {
		
    	byte[] inputBytes = input;
		
    	validateHash(meta.getHashFunction(), inputBytes, meta.getHashValue());
    	
		if(meta.getEncryptionType() == EncryptionType.none) {
			return new String(inputBytes, "UTF-8");
		}
		
		IvParameterSpec iv = new IvParameterSpec(meta.getIV());
		
		Cipher cipher = generateCipher(Cipher.DECRYPT_MODE, meta, new SecretKeySpec(key, "BC"), iv);
		
		return new String(applyCipher(cipher, input), "UTF-8");
	}

    private static IvParameterSpec getMatchingIV(MetaData meta) {
    	
    	switch(meta.getEncryptionMode().requiresIV()) {
    	
    	//no IV
    	case -1: 
    		return null;
    		
    	//inline IV
    	case 0:
    		return generateIvParameterSpec(meta.getEncryptionType().getIVSize());
    
    	//stream cipher IV
    	case 1:
    		
    		//TEMP
    		return new IvParameterSpec(new byte[] {0x13 , 0x12 ,0x11 ,0x10 ,0x09 ,0x08 ,0x07 ,0x06 ,0x05, 0x04, 0x03, 0x02, 0x01, 0x00, 0x00, 0x01});
    	
    	default:
    		return null;
    	}
    }
    
    private static IvParameterSpec generateIvParameterSpec(int length) {
    	
    	IvParameterSpec ivSpec = null;
    	
    	if(length > 0) {
    		SecureRandom rnd = new SecureRandom();
    		
    		byte[] ivBytes = new byte[length];
    		
    		rnd.nextBytes(ivBytes);
    		
    		ivSpec = new IvParameterSpec(ivBytes);
    	}
    	return ivSpec;
    }
    
    private static Cipher generateCipher(int mode, MetaData meta, Key key, IvParameterSpec iv) throws Exception {
    	Cipher cipher = Cipher.getInstance(meta.getEncryptionType().toString() + "/" + meta.getEncryptionMode().toString() + "/" + meta.getPaddingType().toString(), "BC");
    
    	if(iv != null)
    		cipher.init(mode, key, iv);
    	else
    		cipher.init(mode,  key);
    	
    	return cipher;
    }
    
    private static byte[] applyCipher(Cipher cipher, byte[] input) throws Exception
	{
		byte[] output = new byte[cipher.getOutputSize(input.length)];
		
		int ctLength = cipher.update(input, 0, input.length, output, 0);
		
		ctLength += cipher.doFinal(output, ctLength);
		
		return output;
	}
	
	/**
	 * Method to generate a random key using the KeyGenerator class.
	 * 
	 * @param encryption Encryption Method
	 * @return generated Key
	 * @throws Exception
	 */
	private static Key generateKey(MetaData meta) throws Exception {
		
		KeyGenerator generator = KeyGenerator.getInstance(meta.getEncryptionType().toString(), "BC");
		
		generator.init(meta.getKeyLength().asInt());
		return generator.generateKey();
	}

	public static String generateHash(HashFunction hashFunction, byte[] input) throws Exception
	{
		if(hashFunction != HashFunction.NONE)
		{
			MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
			hash.update(input);
		
			return new String(hash.digest(), "UTF-8");
		}
		return null;
	}

	public static void validateHash(HashFunction hashFunction, byte[] input, String read) throws Exception
	{
		if(hashFunction != HashFunction.NONE)
		{
			MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
			hash.update(input);
			
			if(!MessageDigest.isEqual(read.getBytes() , hash.digest()))
			{
				throw new Exception("File has been corrupted/altered");
			}
		}
	}
}
