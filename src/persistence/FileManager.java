package persistence;

import logic.CryptoManager;
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

public class FileManager {

	private static String safetyPath = "C:\\Users\\gpota\\Desktop\\safety\\";
	/**
	 * Reads a byte array from a given path to then decrypt it using the CryptoManager class.
	 * 
	 * @param path	Path to read the file from.
	 * @param encryption Encryption Method
	 * @param mode Mode
	 * @param padding Padding type
	 * @return Encrypted content from file as String
	 */
	public static String openFromPath(File file, MetaData meta) {
		
		try {
			meta.setText(Files.readAllBytes(file.toPath()));
			
			if(meta.getEncryptionType() != EncryptionType.none) {
				meta.setKey(Files.readAllBytes(new File(safetyPath + file.getName()).toPath()));
			}
			
			CryptoManager.decrypt(meta);
			return new String(meta.getText(), "UTF-8");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	/**
	 * Encrypts an input String using the CryptoManager class to then write it to a file.
	 * 
	 * @param file	The output file
	 * @param input The input String
	 * @param encryption Encryption Method
	 * @param mode Mode
	 * @param padding Padding type
	 */
	public static void saveToPath(File file, String input, MetaData meta) {
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(file);
			
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			
			CryptoManager.encrypt(input, meta);
			
			Files.setAttribute(file.toPath(), "user:Operation", meta.getOperation().toString().getBytes());
			Files.setAttribute(file.toPath(), "user:Encryption", meta.getEncryptionType().toString().getBytes());
			Files.setAttribute(file.toPath(), "user:HashFunction", meta.getHashFunction().toString().getBytes());
			Files.setAttribute(file.toPath(), "user:Hash", meta.getHashValue().toString().getBytes());
			
			switch(meta.getOperation()) {
			
			case Symmetric:
	            Files.setAttribute(file.toPath(), "user:Mode", meta.getEncryptionMode().toString().getBytes());
	            Files.setAttribute(file.toPath(), "user:Padding", meta.getPaddingType().toString().getBytes());
	            Files.setAttribute(file.toPath(), "user:KeyLength", meta.getKeyLength().toString().getBytes());
	            Files.setAttribute(file.toPath(), "user:IV", meta.getIV());
	            break;
	            
			case Asymmetric:
				Files.setAttribute(file.toPath(), "user:KeyLength", meta.getKeyLength().toString().getBytes());
				break;
				
			case Password:
	            Files.setAttribute(file.toPath(), "user:Salt", meta.getSalt());
	            break;
			}
            
            // Write key to safety file
            if(meta.getEncryptionType() != EncryptionType.none) {
            	
            	FileOutputStream safetyFileOutput = new FileOutputStream(new File(safetyPath + file.getName()));
            	
            	BufferedOutputStream safetyBufferedOutput = new BufferedOutputStream(safetyFileOutput);
            	
            	//System.out.println(meta.toString());
            	safetyBufferedOutput.write(meta.getKey());
            	
            	safetyBufferedOutput.close();
            }
            
            // write text
         	bufferedOutput.write(meta.getText());
         			
			bufferedOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static MetaData loadMetaData(File file) {
		try {
			MetaData tempMeta = new MetaData();
			
			tempMeta.setOperation(Operation.valueOf(asString(file, "user:Operation")));
			tempMeta.setEncryptionType(EncryptionType.valueOf(asString(file, "user:Encryption")));
			tempMeta.setHashFunction(HashFunction.valueOf(asString(file, "user:HashFunction")));
			tempMeta.setHashValue(new String((byte[])Files.getAttribute(file.toPath(), "user:Hash")));
			
			switch(tempMeta.getOperation()) {
			
			case Symmetric:
				
				tempMeta.setEncryptionMode(EncryptionMode.valueOf(asString(file, "user:Mode")));
				tempMeta.setPaddingType(PaddingType.valueOf(asString(file, "user:Padding")));
				tempMeta.setKeyLength(KeyLength.valueOf(asString(file, "user:KeyLength")));
				tempMeta.setIV(asString(file, "user:IV").getBytes());
				break;
				
			case Asymmetric:
				
				tempMeta.setKeyLength(KeyLength.valueOf(asString(file, "user:KeyLength")));
				break;
				
			case Password:
					
				tempMeta.setSalt(asString(file, "user:Salt").getBytes());
				break;
			}

			return tempMeta;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static String asString(File file, String value) throws Exception {
		return new String((byte[])Files.getAttribute(file.toPath(), value));
	}
}
