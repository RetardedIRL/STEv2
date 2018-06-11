package src;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;

import Enums.EncryptionType;
import Enums.ModeType;
import Enums.PaddingType;

public class FileManager {

	/**
	 * Reads a byte array from a given path to then decrypt it using the CryptoManager class.
	 * 
	 * @param path	Path to read the file from.
	 * @param encryption Encryption Method
	 * @param mode Mode
	 * @param padding Padding type
	 * @return Encrypted content from file as String
	 */
	public static String openFromPath(String path) {
		
		try {
			Path filePath = Paths.get(path);
			byte[] byteArray = Files.readAllBytes(filePath);
			
            EncryptionType  encryptIn 	= EncryptionType.valueOf( new String((byte[])Files.getAttribute(filePath, "user:Encryption"), "utf-8"));
            ModeType 		modeIn 		= ModeType.valueOf( new String((byte[])Files.getAttribute(filePath, "user:Mode"), "utf-8"));
            PaddingType 	paddingIn 	= PaddingType.valueOf( new String((byte[])Files.getAttribute(filePath, "user:Padding"), "utf-8"));
			
            
            byte[] IVIn = null;
            
            if(Logic.requiresIV(modeIn) > -1)
            	IVIn = (byte[]) Files.getAttribute(filePath, "user:IV");
			
            byte[] keyIn = null;
            if(encryptIn != EncryptionType.none)
            	keyIn = (byte[]) Files.getAttribute(filePath,  "user:Key");
			System.out.println(encryptIn + ", " + modeIn + ", " + paddingIn + ", " + new String(keyIn, "UTF-8") + ", " + IVIn);
			
			return CryptoManager.decrypt(byteArray, encryptIn, modeIn, paddingIn, keyIn, IVIn);
			
		} catch (FileNotFoundException e) {
			System.err.println("FILE WAS NOT FOUND");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("LINE READING ERROR");
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	public static void saveToPath(File file, String input, EncryptionType encryption, ModeType mode, PaddingType padding) {
		
		try {
			FileOutputStream fileOutput = new FileOutputStream(file);
			
			BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
			
			byte[][] output = CryptoManager.encrypt(input, encryption, mode, padding);
		
			System.out.println(new String(output[0], "UTF-8"));
			System.out.println(new String(output[1], "UTF-8"));
            Files.setAttribute(file.toPath(), "user:Encryption", encryption.toString().getBytes("UTF-8"));
            Files.setAttribute(file.toPath(), "user:Mode", mode.toString().getBytes("UTF-8"));
            Files.setAttribute(file.toPath(), "user:Padding", padding.toString().getBytes("UTF-8"));
            
            // if IV then set that
            
            if(encryption != EncryptionType.none)
            	Files.setAttribute(file.toPath(), "user:Key", output[1]);
            	
            if(Logic.requiresIV(mode) > -1)
            	Files.setAttribute(file.toPath(), "user:IV", output[2]);
            else
            	System.out.println("IV = null");
            
            // write text
         	bufferedOutput.write(output[0]);
         			
			bufferedOutput.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
