package persistence;

import logic.CryptoManager;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

import Enums.EncryptionType;
import persistence.MetaData;

public class FileManager {

	private static String safetyPath = "C:\\Users\\gpota\\Desktop\\safety";
	/**
	 * Reads a byte array from a given path to then decrypt it using the CryptoManager class.
	 * 
	 * @param path	Path to read the file from.
	 * @param encryption Encryption Method
	 * @param mode Mode
	 * @param padding Padding type
	 * @return Encrypted content from file as String
	 */
	public static String openFromPath(File file, MetaData meta) throws Exception{

		byte[] byteArray = null;
		byte[] keyBytes = null;
		
		try {
			byteArray = Files.readAllBytes(file.toPath());
			System.out.println(meta);
			
			if(meta.getEncryptionType() != EncryptionType.none) {
				keyBytes = Files.readAllBytes(new File(safetyPath + file.getName()).toPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return CryptoManager.decrypt(byteArray, meta);

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
			
			byte[] output = CryptoManager.encrypt(input, meta);
		
            Files.setAttribute(file.toPath(), "user:Encryption", meta.getEncryptionType().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:Mode", meta.getEncryptionMode().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:Padding", meta.getPaddingType().toString().getBytes());
            
            // Write key to safety file
            if(meta.getEncryptionType() != EncryptionType.none) {
            	
            	String safetyPath = "C:\\Users\\gpota\\Desktop\\safety\\" + file.getName();
            	
            	FileOutputStream safetyFileOutput = new FileOutputStream(new File(safetyPath));
            	
            	BufferedOutputStream safetyBufferedOutput = new BufferedOutputStream(safetyFileOutput);
            	
            	safetyBufferedOutput.write(meta.getKey());
            	
            	safetyBufferedOutput.close();
            }
            
            // if IV then set that
            if(meta.getIV() != null)
            	Files.setAttribute(file.toPath(), "user:IV", meta.getIV());
            else
            	System.out.println("IV = null");
            
            // write text
         	bufferedOutput.write(output);
         			
			bufferedOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
