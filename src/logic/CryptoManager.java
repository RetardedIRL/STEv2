package logic;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.Operation;
import Enums.PaddingType;
import persistence.MetaData;

public class CryptoManager {
    
	/** template array used to create stream cipher IVs */
	public static byte[] cipherIVTemplate = {0x00, 0x00, 0x00, 0x01};
	
	/** iteration count used for PBE */
	public static int iterationCount = 2048;
	
	/**
	 * Method to encrypt an incoming String based on the metadata handed in via a MetaData object.
	 * Sends the encrypted byte array out via the metadata object to be saved into a file.
	 * 
	 * This method uses randomly generated keys, IVs, as well as hash validation to ensure its security.
	 * 
	 * @param input The String you want encoded.
	 * @param meta The MetaData object carrying all the information about how the String should be encrypted.
	 * 
	 * @throws Exception
	 */
    public static void encrypt(String input, MetaData meta) throws Exception {
		
    	/* transform input to byte array using a save, uniform transformation method.
    	 * 
    	 * Reason for this is it will stay unaffected by different charsets used by your JVM. */
    	byte[] inputBytes = toByteArray(input);
		
    	// setting cleartext here for fast return
    	meta.setText(inputBytes);
    	
    	// if the entered encryption type is none, NONE (heh) of this has to happen, which makes it return fast.
		if(meta.getEncryptionType() == EncryptionType.none)
			
				// all that needs to be done is generate the hash value
				meta.setHashValue(generateHash(meta.getHashFunction(), inputBytes));
		
		else {
			
			/* eventhough a certain level of validity is enforced by the editor's GUI, some special rules
			 * like the incompatibility of DES and GCM are specified and caught here.
			 * 
			 * if the validity check returns as false, the hash is built based on the clear text and the key is set to be nothing.
			 * Afterwards the method ends. */
			if(!isValid(meta)) {
				
				meta.setHashValue(generateHash(meta.getHashFunction(), inputBytes));
				meta.setKey(new byte[] {});
			}
			
			// if validity check returns as true, we're set to start the encryption process.
			else {
				
				// First we set up the variables to use, which we will later instantiate depending on the encryption method.
				Cipher cipher = null;
				Key key = null;
				IvParameterSpec iv = null;
				
				// We iterate over the operation set by the editor's encryption GUI
				switch(meta.getOperation()) {
				
				// in case of the symmetric encryption we operate as follows
				case Symmetric:
					
					/* generate a random key with the strength specified by the encryption GUI.
					 * 
					 * For this I wrote a method that ensures a strong randomness of the key by making use of the KeyGenerator class. */
					key = generateKey(meta.getEncryptionType(), meta.getKeyLength().asInt());
					
					/* Based on the specifics handed in via the GUI, the requested encryption requires an IV. This applies to those
					 * encryption modes I specify as "ivBlock" and "stream", so we filter out the remaining type "block". */
					if(!meta.getEncryptionMode().getType().equals("block")) {
						
						/* we use a method to generate an IvParameterSpec that is both strongly random
						 * and adjusted to the specified encryption mode */
						iv = generateIvParameterSpec(meta.getEncryptionType(), meta.getEncryptionMode());
						
						// we hand the IV over to the metadata object so it can be persisted later
						meta.setIV(iv.getIV());
					}
					
					// once we got all the details set, we initialize the cipher to be used to encrypt the data later on
					cipher = initializeCipher(Cipher.ENCRYPT_MODE, meta, key, iv);
					
					// at last we hand over the key so it can get persisted in the safety file later
					meta.setKey(key.getEncoded());
					break;
					
				// in case of asymmetric encryption this process differs a little
				case Asymmetric:
					
					/* We generate a SecureRandom object to ensure key strength, then we feed that object
					 * into a function - along with the specified key length - to generate a key pair
					 * consisting of a public and a private key. */
					SecureRandom rnd = new SecureRandom();
					KeyPair keyPair = generateKeyPair(meta.getKeyLength().asInt(), rnd);
					
					/*
					 * once we created that we use the public key for encryption and
					 * hand over the private key to use for decryption.
					 * 
					 * A specific of asymmetric encryption is that we don't need the public for decryption,
					 * so we can simply discard it and move on. */
					key = keyPair.getPublic();
					meta.setKey(keyPair.getPrivate().getEncoded());
					
					// then we initialize the cipher
					cipher = initializeCipher(Cipher.ENCRYPT_MODE, key, rnd);
					break;
					
				// lastly we're gonna look at PBE - password based encryption:
				case Password:
					
					/* for PBE we require salt - a byte array as long as the block size given
					 * with the encryption method -, which we 'add in' to prevent offline dictionary attacks.
					 * 
					 * We generate this using a SecureRandom object. */
					SecureRandom rndSalt = new SecureRandom();
					byte[] salt = new byte[meta.getEncryptionType().getBlockSize()];
					
					rndSalt.nextBytes(salt);
					
					// as always we hand over the information needed for decryption to be persisted later
					meta.setSalt(salt);
					
					/* Now we instantiate a SecretKeyFactory object with the specifics passed on by the metadata object
					 * and generate a secret key based on the cleartext password entered by the user. */
					SecretKeyFactory sKeyFactory = SecretKeyFactory.getInstance(meta.getEncryptionType().toString(), "BC");
					//SecretKey sKey = sKeyFactory.generateSecret(new PBEKeySpec(meta.getPassword().toCharArray(), salt, iterationCount));
					SecretKey sKey = sKeyFactory.generateSecret(new PBEKeySpec(meta.getPassword().toCharArray()));
					
					/* again we initialize the cipher with the specifics, throwing in a static iterationCount
					 * dictating how often the mixing function specified should be applied to generate the key.
					 * 
					 * The value is public, it's use being to simply make every calculation take up more resources,
					 * which makes a huge difference for dictionary attacks and the such. */
					//cipher = initializeCipher(Cipher.ENCRYPT_MODE, meta, sKey);
					cipher = initializeCipher(Cipher.ENCRYPT_MODE, meta.getEncryptionType(), sKey, new PBEParameterSpec(salt, iterationCount));
					break;
				}
				
				// once the individualized ciphers are initialized, we apply it to the clear text and return the encrypted ciphertext
				byte[] ciphertext = applyCipher(cipher, inputBytes);
				
				/* with that the encryption process is done, all that's left is to generate the hash value
				 * used for validation when decrypting the file and hand that and the ciphertext over to
				 * the metadata object. */
				meta.setText(ciphertext);
				meta.setHashValue(generateHash(meta.getHashFunction(), ciphertext));
			}
		}
	}
    
    /**
	 * Method to decrypt a ciphertext byte array handed in via the MetaData object.
	 * Sends the decrypted byte array out via the metadata object to be loaded into the editor.
	 *
	 * @param meta The MetaData object carrying all the information about how the ciphertext
	 * needs to be decrypted -- included the ciphertext itself.
	 * 
	 * @throws Exception
	 */
    public static void decrypt(MetaData meta) throws Exception {
		
    	/* as with the encryption method, none of this has to be done 
    	 * if no encryption was applied in the first place */
    	if(meta.getEncryptionType() != EncryptionType.none) {
    	
    		// firstly, we grab the key, as well as the cipher text from the MetaData object
	    	byte[] key = meta.getKey();
	    	byte[] inputBytes = meta.getText();
			
	    	/* next up comes our hash validation, which operates by hashing our inputBytes and
	    	 * comparing that to the value read from the file's metadata handed in by the metadata object. */
    		if(isHashValid(meta.getHashFunction(), inputBytes, meta.getHashValue()))
				
    			/* once we're clear the file hasn't been altered,
    			 * we move on to test for validity again. */
    			if(isValid(meta)) {
					
    				
    				// the following steps are fairly similar to the encryption process
					Cipher cipher = null;
					
					// we iterate over the operation used in the file read
					switch(meta.getOperation()) {
					
					// in case of symmetric encryption
					case Symmetric:
						IvParameterSpec iv = null;
						
						// read the IV if required
						if(!meta.getEncryptionMode().getType().equals("block"))
							iv = new IvParameterSpec(meta.getIV());
						
						// initialize the cipher to be used for decryption later on
						cipher = initializeCipher(Cipher.DECRYPT_MODE, meta, new SecretKeySpec(key, "BC"), iv);
						break;
						
					// in case of asymmetric encryption
					case Asymmetric:
						
						// instantiate a cipher object
						cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
						
						// use the handed in key bytes to create a EncodedKeySpec object
						PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(key);
						
						// generate the private key by using the KeyFactory.generatePrivate() function
						PrivateKey privKey = KeyFactory.getInstance("RSA", "BC").generatePrivate(privKeySpec);
						
						// initialize the cipher
						cipher.init(Cipher.DECRYPT_MODE, privKey);
						break;
						
					// in case of PBE
					case Password:
						
						// instantiate the SecretKeyFactory object
						SecretKeyFactory sKeyFactory = SecretKeyFactory.getInstance(meta.getEncryptionType().toString(), "BC");
						
						//generate the SecretKey object from the password handed in
						//SecretKey sKey = sKeyFactory.generateSecret(new PBEKeySpec(meta.getPassword().toCharArray(), meta.getSalt(), iterationCount));
						SecretKey sKey = sKeyFactory.generateSecret(new PBEKeySpec(meta.getPassword().toCharArray()));
						
						// initialize the cipher, handing in read salt and static iteration count
						//cipher = initializeCipher(Cipher.DECRYPT_MODE, meta, sKey);
						cipher = initializeCipher(Cipher.DECRYPT_MODE, meta.getEncryptionType(), sKey, new PBEParameterSpec(meta.getSalt(), iterationCount));
						break;
					}
					
					/** After we apply the cipher and decrypt the ciphertext, we apply a method to cut padding beauty marks,
					 *  which weirdly enough only ever get caused by DES and PKCS7Padding.
					 *  
					 *  All it does is use the String.trim() function to cut off any unnecessary white spaces at the start
					 *  (and in our case more importantly) end of the plain text.
					 */
					meta.setText(cutLeftovers(applyCipher(cipher, meta.getText())));
		    	}
			} 	
    }
    
    /**
     * Method used to generate a random IvParameterSpec object. Operates in a way that block size and required length
     * dictated by encryption mode is accounted for, as well as specified attributes such as the template for
     * stream cipher IVs.
     * 
     * @param encryption the encryption method used, required to receive the block size
     * @param mode the encryption mode used, required to receive the required IV length
     * @return the generated IvParameterSpec object
     */
    private static IvParameterSpec generateIvParameterSpec(EncryptionType encryption, EncryptionMode mode) {
    	
    	//acquire the block size depicted by the encryption method
    	int length = encryption.getBlockSize();

    	IvParameterSpec ivSpec = null;
    	
    	// not sure if I actually need this but I added it to prevent any possible NullPointerExceptions
    	if(length > 0) {
    		
    		//generate a SecureRandom object used for creating the random bytes
    		SecureRandom rnd = new SecureRandom();
    		
    		//instantiate the byte array
    		byte[] ivBytes = new byte[length];
    		
    		/* iterate over the type of the mode to decide what type of IV to generate.
    		 * 
    		 * (since "block" got filtered out we don't have to account for that) */
    		switch(mode.getType()) {
    		
    		// in case of "ivBlock" mode, namely CBC and CTS
    		case "ivBlock":
    			
    			// simply fill the whole array with random bytes
	    		rnd.nextBytes(ivBytes);
	    		break;
	    		
	    	// in case of "stream" mode, like CFB8 or GCM
    		case "stream":
    			
    			/* the stream cipher IV consists of two parts, since
    			 * the last 4 bytes are dictated to be 0x00, 0x00, 0x00, 0x01.
    			 * 
    			 * We create a byte array with a size 4 spots shorter than the complete
    			 * ivBytes array to leave space for the latter part.
    			 * 
    			 * Then we fill those remaining spots with random bytes, usually you'd
    			 * reserve at least some of them for a linearly increasing message number,
    			 * for our case however random is good enough since the chance of us reusing
    			 * the same IV is very slim. */
    			byte[] cipherIVRandom = new byte[length-4];
    			rnd.nextBytes(cipherIVRandom);
    			
    			// once generated we can glue the two parts together into the ivBytes array
    			System.arraycopy(cipherIVRandom, 0, ivBytes, 0, cipherIVRandom.length);
    			System.arraycopy(cipherIVTemplate, 0, ivBytes, length-4, cipherIVTemplate.length);
    			break;
    		}
    		
    		// finally we initialize the IvParameterSpec object
    		ivSpec = new IvParameterSpec(ivBytes);
    	}
    	
    	// and return it.
    	return ivSpec;
    }
    
    /**
     * Method to generate and initialize a cipher for symmetric encryption.
     * 
     * @param mode the cipher mode used, namely ENCRYPT and DECRYPT
     * @param meta the metadata handed in by the system
     * @param key  the key object
     * @param iv the IV if required, otherwise null
     * @return the readied cipher object.
     * 
     * @throws Exception
     */
    private static Cipher initializeCipher(int mode, MetaData meta, Key key, IvParameterSpec iv) throws Exception {
    	
    	// get an instance of the Cipher class with the encryption specifics and Bouncy Castle as the provider
    	Cipher cipher = Cipher.getInstance(meta.getEncryptionType().toString() + "/" + meta.getEncryptionMode().toString() + "/" + meta.getPaddingType().toString(), "BC");
    
    	// if an IV is required, initialize cipher along with it
    	if(iv != null)
    		cipher.init(mode, key, iv);
    	else
    		cipher.init(mode,  key);
    	
    	// return the readied cipher object
    	return cipher;
    }
    
    /**
     * Method to generate and initialize a cipher for asymmetric encryption.
     * 
     * @param mode the cipher mode used, namely ENCRYPT and DECRYPT
     * @param key the key object, namely private or public key
     * @param rnd the SecureRandom object used to generate the keypair
     * @return the readied cipher object.
     * 
     * @throws Exception
     */
    private static Cipher initializeCipher(int mode, Key key, SecureRandom rnd) throws Exception {
    	
    	// get an instance of the Cipher class for asymmetric encryption and Bouncy Castle as the provider
    	Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
    	
    	// initialize the cipher
    	cipher.init(mode, key, rnd);
    	
    	// returnt the readied cipher object
    	return cipher;
    }
    
    /**
     * Method to generate and initialize a cipher for password based encryption.
     * 
     * @param mode the cipher mode used, namely ENCRYPT and DECRYPT
     * @param encryption the encryption method to use, for PBE that includes
     * the mixing function used when generating the key
     * 
     * @param key the key object generated from clear text password, salt and iteration count
     * @param parameterSpec salt and iteration count
     * @return the readied cipher object.
     * 
     * @throws Exception
     */
    private static Cipher initializeCipher(int mode, EncryptionType encryption, Key key, PBEParameterSpec parameterSpec) throws Exception {
    
    	// get an instance of the Cipher class for PBE, with its specifics described in the encryption method.
    	Cipher cipher = Cipher.getInstance(encryption.toString(), "BC");
    	
    	// initialize the cipher with the PBEParameterSpec containing salt and iteration count.
    	cipher.init(mode, key, parameterSpec);
    	
    	// return the readied cipher object
    	return cipher;
    }
    
    /**
     * Method to apply a cipher to an input array to generate a ciphertext / cleartext.
     * 
     * @param cipher the intialized cipher object.
     * @param input the responding input array.
     * @return the transformed byte array, namely ciphertext or cleartext.
     * 
     * @throws Exception
     */
    private static byte[] applyCipher(Cipher cipher, byte[] input) throws Exception
	{
    	// initialize an output array the size responding to the length of our input
		byte[] output = new byte[cipher.getOutputSize(input.length)];
		
		/* run the update cipher update function over the input 
		 * and save it's specifics for offset in the following step. */
		int ctLength = cipher.update(input, 0, input.length, output, 0);
		
		// run doFinal to complete the transformation
		ctLength += cipher.doFinal(output, ctLength);
		
		// return the transformed byte array
		return output;
	}
	
    /**
     * Method to generate keys for symmetric encryption. Uses the KeyGenerator class
     * to secure key strength.
     * 
     * @param encryption the encryption method used by the applicant
     * @param keylength the key length specified
     * 
     * @return a strongly randomized key of the specified length
     * 
     * @throws Exception
     */
	private static Key generateKey(EncryptionType encryption, int keylength) throws Exception {
		
		// instantiate a KeyGenerator object with the encryption method and Bouncy Castle provider
		KeyGenerator generator = KeyGenerator.getInstance(encryption.toString(), "BC");
	
		// initialize the generator based on the key length
		generator.init(keylength);
		
		// return a generated key with the wanted length
		return generator.generateKey();
	}

	/**
	 * Method to generate KeyPair objects for asymmetric encryption. Provides strongly
	 * randomized keys by using the KeyPairGenerator class.
	 * 
	 * @param keylength the specified key length
	 * @param rnd the SecureRandom object later to be used to initialize the cipher
	 * @return a KeyPair object of the wanted strength
	 * 
	 * @throws Exception
	 */
	private static KeyPair generateKeyPair(int keylength, SecureRandom rnd) throws Exception {
		
		// instantiate the KeyPairGenerator class with RSA and Bouncy Castle
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		
		// initialize the generator with the specified key length and SecureRandom object
		generator.initialize(keylength, rnd);
		
		// return a generated KeyPair object
		return generator.generateKeyPair();
		
	}
	
	/**
	 * Method to generate a hash value from an input array via a specified hashing function.
	 * 
	 * @param hashFunction the hashing function to be used.
	 * @param input the byte array we base our hashing on.
	 * @return a generated hash value encoded to String by Base64 or "" if the hashing function is NONE.
	 * 
	 * @throws Exception
	 */
	public static String generateHash(HashFunction hashFunction, byte[] input) throws Exception
	{
		//Return fast if no hashing function specified
		if(hashFunction == HashFunction.NONE) {
			return "";
		}
		
		// instantiate a MessageDigest object with the hashing function and Bouncy Castle as a provider.
		MessageDigest hash = MessageDigest.getInstance(hashFunction.toString(), "BC");
		
		// run the update function over the input array
		hash.update(input);
		
		// return a String encoded by Base64 to guarantee unaffectedness by different char sets etc.
		return Base64.getEncoder().encodeToString(hash.digest());
	}

	//TODO: private static
	
	/**
	 * Method to validate a read hash value by comparing it with the result of hashing the current input.
	 * 
	 * @param hashFunction the hashing function used.
	 * @param input the currently available input.
	 * @param read the read hash value to be validated.
	 * @return true, if the read hash is exactly the same as the result of hashing the given input
	 * 
	 * @throws Exception
	 */
	public static boolean isHashValid(HashFunction hashFunction, byte[] input, String read) throws Exception
	{
		// return fast, if no hashing function was specified
		if(hashFunction == HashFunction.NONE) {
			
			// if the hash value isn't "" anymore the file has been altered
			if(!read.equals(""))
				return false;
			
			// otherwise return true
			return true;
		}

		Decoder decoder = Base64.getDecoder();
		/* using the MessageDigest compare function we check whether the read hash value is exactly
		 * the same as the result of hashing our input with the hash function specified, using Base64
		 * to decode our previously encoded hashes.
		 * 
		 * If true the file is still in it's original state (or the hash value has been tampered with the same
		 * way the file got tampered with),
		 * 
		 * if not tampering/corruption is the case.
		 */
		return MessageDigest.isEqual(decoder.decode(read) , decoder.decode(generateHash(hashFunction, input)));

	}

	/**
	 * Method to check for remaining validity issues that can't be enforced by the GUI alone.
	 * 
	 * @param meta
	 * @return
	 */
	private static boolean isValid(MetaData meta) {
		
		// since when using PBE all the details are specified in the encryption method, none of this applies, return early
		if(meta.getOperation() != Operation.Password) {
			
			// get the encryption method used
			EncryptionMode mode = meta.getEncryptionMode();
			
			/* Certain modes - namely ECB, CBC and CTS - don't work with NoPadding if the input isn't the same size or multiples of the block size dictated
			 * by the mode, which leads to failure. */
			if(mode == EncryptionMode.ECB || mode == EncryptionMode.CBC || mode == EncryptionMode.CTS)
				if(meta.getPaddingType() == PaddingType.NoPadding && (meta.getText().length % meta.getEncryptionType().getBlockSize()) != 0) {
					System.out.println("Input bytes not compatible with block size.");
					return false;
				}
			
			// Here is the rule I talked about towards the beginning, where DES and GCM are incompatible
			if(meta.getEncryptionMode() == EncryptionMode.GCM && meta.getEncryptionType() == EncryptionType.DES) {
				System.out.println("GCM and DES are incompatible");
				return false;
			}
		}
		
		// if none of these cases apply, the input metadata is valid and encryption/decryption can proceed.
		return true;
	}
	
	/**
	 * Method using String.trim() to cut decryption beauty marks caused by DES and PKCS7.
	 * 
	 * @param inputBytes the faulty cleartext.
	 * 
	 * @return cleaned up cleartext.
	 */
	private static byte[] cutLeftovers(byte[] inputBytes) {
		
		
		try {
			
			// returns a trimmed version of the faulty input text.
			return toByteArray(new String(inputBytes, "UTF-8").trim());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Method to safely convert Strings to byte arrays.
	 * 
	 * As mentioned above this is recommended so the system can operate unaffected
	 * by the char set used by your JVM.
	 * 
	 * @param string the String to be converted.
	 * @return the safely converted byte array.
	 */
	private static byte[] toByteArray(String string) {
		byte[] bytes = new byte[string.length()];
		char[] chars = string.toCharArray();
		
		for(int i = 0; i != chars.length; i++) {
			
			bytes[i] = (byte) chars[i];
		}
		
		return bytes;
	}
}
