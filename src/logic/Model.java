package logic;

import presentation.PasswordDialog;
import persistence.FileManager;

import java.io.File;
import java.util.Optional;

import Enums.Operation;
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
	
	public void newFile() {
		
		currentMeta = new MetaData();
		this.textArea.setText("");
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
				
				//load MetaData
				currentMeta = FileManager.loadMetaData(file);
				
				if(currentMeta.getOperation() == Operation.Password) {
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
				
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
				if(currentMeta.getOperation() == Operation.Password) {
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
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
				if(currentMeta.getOperation() == Operation.Password) {
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
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
