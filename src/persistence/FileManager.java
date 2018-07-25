package persistence;

import logic.CryptoManager;
import logic.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.PaddingType;
import persistence.MetaData;

/**
 * Class called by the model to persist data handed in by the CryptoManager class.
 * 
 * @author sam
 */
public class FileManager {

	
	/** static path used to write keys to, namely a USB drive or similar external mediums. */
	private static String safetyPath = "D:\\safety\\";
	

	/**
	 * Method used to persist a file handed in by the model, which uses the standard OS
	 * save dialog to set the file to write to.
	 * 
	 * The input handed in gets encrypted via the CryptoManager class using the information
	 * provided by the MetaData object (which previously got filled by the encryption GUI).
	 * 
	 * Once encrypted an OutputStream is used to write the bytes, furthermore the metadata that
	 * can stay public will be written to be exactly that, metadata to the file.
	 * 
	 * The private metadata, namely the key used, will be written to the safety file located
	 * at the path dictated by the static safetyPath variable.
	 * 
	 * @param file the file (handed in by the model) to which the data should be persisted.
	 * @param input the String read from the interface to be encrypted and persisted.
	 * @param meta the MetaData object describing how the input should be encrypted.
	 */
	public static void saveToPath(File file, String input, MetaData meta) {
		
		try {
			
			// We create the stream objects necessary to writing the bytes
			FileOutputStream fileOutput = new FileOutputStream(file);
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			
			/* Now we encrypt the data using the CryptoManager class, which in return saves
			 * information like key used, IV used, salt values as well as the ciphertext itself */
			CryptoManager.encrypt(input, meta);
			
			/* Once that is said and done, we begin writing the metadata:
			 * 
			 * Some of these are uniform for every operation, so they can safely be written
			 * for each of them. */
			Files.setAttribute(file.toPath(), "user:Operation", Utils.toByteArray(meta.getOperation().toString()));
			Files.setAttribute(file.toPath(), "user:Encryption", Utils.toByteArray(meta.getEncryptionType().toString()));
			Files.setAttribute(file.toPath(), "user:HashFunction", Utils.toByteArray(meta.getHashFunction().toString()));
			Files.setAttribute(file.toPath(), "user:Hash", Utils.toByteArray(meta.getHashValue().toString()));
			
			// From there on we iterate over the operation method used.
			switch(meta.getOperation()) {
			
			/* in case of symmetric encryption
			 * we have to add information about the encryption mode used, padding techniques,
			 * the key length dictated by the user and the IV generated if needed. */
			case Symmetric:
	            Files.setAttribute(file.toPath(), "user:Mode", Utils.toByteArray(meta.getEncryptionMode().toString()));
	            Files.setAttribute(file.toPath(), "user:Padding", Utils.toByteArray(meta.getPaddingType().toString()));
	            Files.setAttribute(file.toPath(), "user:KeyLength", Utils.toByteArray(meta.getKeyLength().toString()));
	            Files.setAttribute(file.toPath(), "user:IV", meta.getIV());
	            break;
	           
	        /* in case of asymmetric encryption we just need to hand over the key length */
			case Asymmetric:
				Files.setAttribute(file.toPath(), "user:KeyLength", Utils.toByteArray(meta.getKeyLength().toString()));
				break;
			
			/* for password based encryption the same goes but for the salt */
			case Password:
	            Files.setAttribute(file.toPath(), "user:Salt", meta.getSalt());
	            break;
			}
            
            /* in the following we will work on persisting the key used for encryption,
             * which obviously only applies when encryption was used at all. */
            if(meta.getEncryptionType() != EncryptionType.none) {
            	
            	/* create the necessary buffers targeting the safety path.
            	 *  We create a new file with the same name as the name entered by the user. */
            	FileOutputStream safetyFileOutput = new FileOutputStream(new File(safetyPath + file.getName()));
            	BufferedOutputStream safetyBufferedOutput = new BufferedOutputStream(safetyFileOutput);
            	
            	// Now we write the key and close the stream.
            	safetyBufferedOutput.write(meta.getKey());
            	safetyBufferedOutput.close();
            }
            
            // Finally we can save the encrypted data to the actual file and close the stream.
         	bufferedOutput.write(meta.getText());
			bufferedOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method used to open a persisted file based on a path handed in by the model.
	 * 
	 * To decrypt the file using CryptoManager, we read the information needed from the file's metadata,
	 * as well as the key value from the safety file.
	 * 
	 * 
	 * @param file the file (handed in by the model) from which the data should be loaded.
	 * @param meta the MetaData object describing how the input should be encrypted.
	 */
	public static String openFromPath(File file, MetaData meta) {
		
		try {
			
			// We read the metadata persisted along with the file.
			meta = readMetaData(file);
			
			// Then we set the text we want decrypted.
			meta.setText(Files.readAllBytes(file.toPath()));
			
			/* if the now read metadata contains an encryption method,
			 * we read the key from the safetypath. */
			if(meta.getEncryptionType() != EncryptionType.none) {
				meta.setKey(Files.readAllBytes(new File(safetyPath + file.getName()).toPath()));
			}
			
			// Now we decrypt the data and return it as a String.
			CryptoManager.decrypt(meta);
			return Utils.toString(meta.getText());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	/**
	 * Method to read metadata from a given file. Returns a filled metadata object.
	 * 
	 * @param file the file you want to read the metadata from.
	 * 
	 * @return a filled metadata object.
	 */
	public static MetaData readMetaData(File file) {
		try {
			
			// create a dummy metadata object
			MetaData tempMeta = MetaData.getInstance();
			
			// read the uniform metadata
			tempMeta.setOperation(Operation.valueOf(asString(file, "user:Operation")));
			tempMeta.setEncryptionType(EncryptionType.valueOf(asString(file, "user:Encryption")));
			tempMeta.setHashFunction(HashFunction.valueOf(asString(file, "user:HashFunction")));
			tempMeta.setHashValue(Utils.toString((byte[])Files.getAttribute(file.toPath(), "user:Hash")));
			
			// iterate over the operation specific metadata
			switch(tempMeta.getOperation()) {
			
			case Symmetric:
				
				tempMeta.setEncryptionMode(EncryptionMode.valueOf(asString(file, "user:Mode")));
				tempMeta.setPaddingType(PaddingType.valueOf(asString(file, "user:Padding")));
				tempMeta.setKeyLength(KeyLength.valueOf(asString(file, "user:KeyLength")));
				tempMeta.setIV(Utils.toByteArray(asString(file, "user:IV")));
				break;
				
			case Asymmetric:
				
				tempMeta.setKeyLength(KeyLength.valueOf(asString(file, "user:KeyLength")));
				break;
				
			case Password:
					
				tempMeta.setSalt(Utils.toByteArray(asString(file, "user:Salt")));
				break;
			}

			// return the filled metadata object.
			return tempMeta;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Method used to make reading metadata more humanly readable.
	 * 
	 * @param file the file to read the specific metadata from.
	 * @param value the name of the metadata, e.g. "user:Encryption"
	 * 
	 * @return a string value from what's read from the file.
	 * 
	 * @throws Exception
	 */
	private static String asString(File file, String value) throws Exception {
		return Utils.toString((byte[])Files.getAttribute(file.toPath(), value));
	}
}
