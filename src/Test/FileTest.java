package Test;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.EncryptionMode;
import Enums.PaddingType;

import persistence.MetaData;
import persistence.FileManager;;

public class FileTest {

	@Test
	public void testSaveOpen() {
		
		String testString = "test";
		MetaData testMeta = MetaData.getInstance();
		
		testMeta.setOperation(Operation.Symmetric);
		testMeta.setEncryptionType(EncryptionType.AES);
		testMeta.setEncryptionMode(EncryptionMode.CBC);
		testMeta.setPaddingType(PaddingType.PKCS7Padding);
		testMeta.setKeyLength(KeyLength.x256);
		testMeta.setHashFunction(HashFunction.SHA1);
		
		File testFile = new File("C:\\Users\\gpota\\Desktop\\test\\test.txt");
		
		FileManager.saveToPath(testFile, testString, testMeta);
		
		String result = FileManager.openFromPath(testFile, testMeta);
		
		assertEquals(testString, result);
	}
}
