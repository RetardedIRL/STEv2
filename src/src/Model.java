package src;

import java.io.File;

import Enums.EncryptionType;
import Enums.ModeType;
import Enums.PaddingType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class Model {

	//Instanzvariablen
	private EncryptionType encryptionType;
	private ModeType modeType;
	private PaddingType paddingType;
	
	private TextArea textArea;
	
	private String fileName;
	private String filePath;
	
	/** Constructor */
	public Model(TextArea textArea) {
		
		this.encryptionType = EncryptionType.none;
		this.modeType = ModeType.ECB;
		this.paddingType = PaddingType.NoPadding;
		
		this.textArea = textArea;
	}
	
	/**
	 * Method to open a file. Uses FileChooser and FileManager classes.
	 */
	void open() {
		FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(null);
		
		if(file != null) {
			filePath = file.getAbsolutePath();
			fileName = file.getName();
			
			textArea.setText(FileManager.openFromPath(filePath));
		}
	}
	
	/**
	 * Method to save the current text to a file. Uses the FileManager class.
	 * 
	 * Uses saveAs if this is the first save.
	 */
	void save() {
		
		//File already exists
		if(fileName != null) {
			File file = new File(filePath);
			try {
				FileManager.saveToPath(file, textArea.getText(), encryptionType, modeType, paddingType);
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
	void saveAs() {
		FileChooser fileChooser = new FileChooser();
		
		//Sets the datatype that is displayed in the filechooser
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showSaveDialog(null);
		
		if(file != null) {
			filePath = file.getAbsolutePath();
			fileName = file.getName();
			
			try {
				FileManager.saveToPath(file, textArea.getText(), encryptionType, modeType, paddingType);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/** Setter Method for EncryptionType */
	public void setEncryptionType(EncryptionType encryption) {
		this.encryptionType = encryption;
	}
	
	/** Setter Method for ModeType */
	public void setModeType(ModeType mode) {
		this.modeType = mode;
	}
	
	/** Setter Method for PaddingType */
	public void setPaddingType(PaddingType padding) {
		this.paddingType = padding;
	}
}
