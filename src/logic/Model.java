package logic;

import persistence.FileManager;
import java.io.File;
import java.nio.file.Files;

import Enums.EncryptionMode;
import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.PaddingType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import persistence.MetaData;

public class Model {

	private TextArea textArea;
	
	private String fileName;
	private String filePath;
	
	private MetaData currentMeta;
	
	/** Constructor */
	public Model(TextArea textArea) {
		
		currentMeta = new MetaData();
		this.textArea = textArea;
	}
	
	public void setPassword(String input) {
		currentMeta.setPassword(input);
	}
	
	private void loadMetaData(File file) {
		try {
			MetaData openedData = new MetaData();
			openedData.setOperation(Operation.valueOf(asString(file, "user:Operation")));
			openedData.setEncryptionType(EncryptionType.valueOf(asString(file, "user:Encryption")));
			openedData.setEncryptionMode(EncryptionMode.valueOf(asString(file, "user:Mode")));
			openedData.setPaddingType(PaddingType.valueOf(asString(file, "user:Padding")));
			openedData.setKeyLength(KeyLength.valueOf(asString(file, "user:KeyLength")));
			openedData.setHashFunction(HashFunction.valueOf(asString(file, "user:HashFunction")));
			openedData.setHashValue(new String((byte[])Files.getAttribute(file.toPath(), "user:Hash")));
			openedData.setIV(asString(file, "user:IV").getBytes());
			currentMeta = openedData;
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String asString(File file, String value) throws Exception {
		return new String((byte[])Files.getAttribute(file.toPath(), value));
	}
	
	/**
	 * Method to open a file. Uses FileChooser and FileManager classes.
	 */
	public void open() {
		try {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			
			if(file != null) {
				filePath = file.getAbsolutePath();
				fileName = file.getName();
				
				textArea.setText(FileManager.openFromPath(file, currentMeta));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to save the current text to a file. Uses the FileManager class.
	 * 
	 * Uses saveAs if this is the first save.
	 */
	public void save() {
		
		//File already exists
		if(fileName != null) {
			File file = new File(filePath);
			try {
				FileManager.saveToPath(file, textArea.getText(), currentMeta);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//File doesn't exist yet
		else {
			saveAs();
		}
	}
	
	/**
	 * Method to save to a new file. Uses FileChooser and FileManager to write to file
	 */
	public void saveAs() {
		FileChooser fileChooser = new FileChooser();
		
		//Sets the datatype that is displayed in the filechooser
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showSaveDialog(null);
		
		if(file != null) {
			filePath = file.getAbsolutePath();
			fileName = file.getName();
			
			try {
				FileManager.saveToPath(file, textArea.getText(), currentMeta);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public MetaData getCurrentMeta() {
		return this.currentMeta;
	}
}
