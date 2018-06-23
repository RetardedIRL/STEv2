package logic;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.PaddingType;
import persistence.MetaData;

public class CryptoManager {
    
	
	public static byte[] cipherIVTemplate = {0x00, 0x00, 0x00, 0x01};
	
    public static void encrypt(String input, MetaData meta) throws Exception {
		
    	byte[] inputBytes = input.getBytes();
		
    	meta.setText(inputBytes);
    	
		if(meta.getEncryptionType() != EncryptionType.none) {	

			if(!isValid(meta)) {
				
				meta.setHashValue(generateHash(meta.getHashFunction(), inputBytes));
				meta.setKey(null);
			}
			else {
				
				Key key = generateKey(meta);
				
				IvParameterSpec iv = generateIvParameterSpec(meta);
				
				if(!meta.getEncryptionMode().getType().equals("block"))
					meta.setIV(iv.getIV());
				
				Cipher cipher = generateCipher(Cipher.ENCRYPT_MODE, meta, key, iv);
					
				byte[] ciphertext = applyCipher(cipher, input.getBytes());
				
				meta.setHashValue(generateHash(meta.getHashFunction(), ciphertext));
				meta.setKey(key.getEncoded());
				
				meta.setText(ciphertext);
			}
		}
		else {
			meta.setHashValue(generateHash(meta.getHashFunction(), inputBytes));
		}
	}
    
    public static void decrypt(MetaData meta) throws Exception {
		
    	if(meta.getEncryptionType() != EncryptionType.none) {
    	
	    	byte[] key = meta.getKey();
	    	byte[] inputBytes = meta.getText();
			
	    	if(meta.getHashValue() != null)
	    		if(isHashValid(meta.getHashFunction(), inputBytes, meta.getHashValue()))
					if(isValid(meta)) {
						IvParameterSpec iv = null;
						
						if(!meta.getEncryptionMode().getType().equals("block"))
							iv = new IvParameterSpec(meta.getIV());
						
						Cipher cipher = generateCipher(Cipher.DECRYPT_MODE, meta, new SecretKeySpec(key, "BC"), iv);
						
						meta.setText(cutLeftovers(applyCipher(cipher, meta.getText())));
			    	}
    			}
	    	else
	    		System.out.println("Hash empty, hash function should be NONE"); 	
}
    
    private static IvParameterSpec generateIvParameterSpec(MetaData meta) {
    	
    	int length = meta.getEncryptionType().getBlockSize();

    	IvParameterSpec ivSpec = null;
    	
    	if(length > 0) {
    		
    		SecureRandom rnd = new SecureRandom();
    		
    		byte[] ivBytes = new byte[length];
    		
    		switch(meta.getEncryptionMode().getType()) {
    		
    		case "block":
    			return null;
    			
    		case "ivBlock":
	    		rnd.nextBytes(ivBytes);
	    		break;
	    		
    		case "stream":
    			byte[] cipherIVRandom = new byte[length-4];
    			rnd.nextBytes(cipherIVRandom);
    			
    			System.arraycopy(cipherIVRandom, 0, ivBytes, 0, cipherIVRandom.length);
    			System.arraycopy(cipherIVTemplate, 0, ivBytes, length-4, cipherIVTemplate.length);
    			break;
    			
    		default:
    			break;
    		}
    		
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
		if(hashFunction == HashFunction.NONE) {
			return "";
		}
		MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
		hash.update(input);
		
		return Base64.getEncoder().encodeToString(hash.digest());
	}

	//TODO: private static
	public static boolean isHashValid(HashFunction hashFunction, byte[] input, String read) throws Exception
	{
		if(hashFunction == HashFunction.NONE) {
			if(!read.equals(""))
				return false;
			
			return true;
		}

		//Compare the two hashes using a message digest helper function
		if(!MessageDigest.isEqual(Base64.getDecoder().decode(read) , Base64.getDecoder().decode(generateHash(hashFunction, input))))
		{
			throw new Exception("File has been altered!");
		}
		
		return true;
	}

	private static boolean isValid(MetaData meta) {
		
		if(meta.getEncryptionMode() == EncryptionMode.ECB || meta.getEncryptionMode() == EncryptionMode.CBC || meta.getEncryptionMode() == EncryptionMode.CTS)
			if(meta.getPaddingType() == PaddingType.NoPadding && (meta.getText().length % meta.getEncryptionType().getBlockSize()) != 0) {
				System.out.println("Input bytes not compatible with block size.");
				return false;
			}
		
		return true;
	}
	
	private static byte[] cutLeftovers(byte[] inputBytes) {
		
		
		try {
			return new String(inputBytes, "UTF-8").trim().getBytes();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}