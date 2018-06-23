package persistence;

import logic.CryptoManager;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

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
            Files.setAttribute(file.toPath(), "user:Mode", meta.getEncryptionMode().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:Padding", meta.getPaddingType().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:KeyLength", meta.getKeyLength().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:HashFunction", meta.getHashFunction().toString().getBytes());
            Files.setAttribute(file.toPath(), "user:Hash", meta.getHashValue().toString().getBytes());
            
            // Write key to safety file
            if(meta.getEncryptionType() != EncryptionType.none) {
            	
            	FileOutputStream safetyFileOutput = new FileOutputStream(new File(safetyPath + file.getName()));
            	
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
         	bufferedOutput.write(meta.getText());
         			
			bufferedOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
