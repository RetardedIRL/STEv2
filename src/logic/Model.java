package logic;

import presentation.PasswordDialog;
import persistence.FileManager;

import java.io.File;
import java.util.Optional;

import Enums.Operation;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import persistence.MetaData;

/**
 * Model class for the STE. Handles logic when given in commands from the GUI.
 * @author sam
 */
public class Model {

	// we save the editor's text area so we can manipulate it directly.
	private TextArea textArea;
	
	/* furthermore we store the file name and path after saving a file,
	   mainly so we can distinguish between save and save as. */
	private String fileName;
	private String filePath;
	
	/* this metadata object gets manipulated and filled by the GUI,
	   before it gets handed over to the CryptoManager to provide information. */
	private MetaData currentMeta;
	
	/** Constructor
	 * 
	 * Create an empty metadata object and store the editor's text area.
	 */
	public Model(TextArea textArea) {
		
		currentMeta = MetaData.getInstance();
		this.textArea = textArea;
	}
	
	/**
	 * Method to set the password put in by the user via password dialog.
	 * 
	 * @param input the password to store in the metadata object.
	 */
	public void setPassword(String input) {
		currentMeta.setPassword(input);
	}
	
	/**
	 * Method to 'create a new file', which just means clearing the editor along
	 * with other traces of the old file used.
	 */
	public void newFile() {
		
		this.fileName = null;
		this.filePath = null;
		this.currentMeta = MetaData.getInstance();
		this.textArea.setText("");
	}
	
	/**
	 * Method to read a persisted file into the editor. Gets called by the GUI,
	 * uses the default open dialog window provided by the OS, then uses the
	 * FileManager class to actually read and decrypt the file.
	 */
	public void open() {
		
		try {
			
			// first we get a file chooser and get input from the user
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(null);
			
			/* the following can only apply when the user input was valid,
			 * otherwise actions like closing the opening dialog incorrectly
			 * would lead to a major error. */
			if(file != null) {
				
				// we save the information about the file
				filePath = file.getAbsolutePath();
				fileName = file.getName();
				
				// then we pull the metadata out of it using a function from the FileManager class
				currentMeta = FileManager.readMetaData(file);
				
				/* in order to open a password encrypted file we first need to ask
				 * the user to enter it. */
				if(currentMeta.getOperation() == Operation.Password) {
					
					/* we open a password dialog window and wait for the user to finish their input.
					 * Once we got that we save it in the metadata object before handing it over to
					 * the FileManager to decrypt. */
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
				
				/* now we hand it over to the FileManager class to decrypt and return the String
				 * so we can place it in the text area. */
				textArea.setText(FileManager.openFromPath(file, currentMeta));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to save the STE's text area. Gets called by the GUI, uses the FileManager class to
	 * actually persist the file.
	 */
	public void save() {
		
		/* if we haven't saved anything so far the fileName variable is null, which means
		 * we are now trying to save a new file. */
		if(fileName == null)
			// use the saveAs() function instead.
			saveAs();
		
		// if not the file already exists and we have to overwrite it.
		else {
			
			// get the existing file
			File file = new File(filePath);
			try {
				
				// ask for password (explained above)
				if(currentMeta.getOperation() == Operation.Password) {
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
				
				// use FileManager class to encrypt and save the file.
				FileManager.saveToPath(file, textArea.getText(), currentMeta);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method used to save to a new file. Can be called via GUI, substitute method called
	 * when saving a new file using the regular 'save' expression.
	 */
	public void saveAs() {
		
		// get a file chooser and set the data type displayed
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		// open the default save dialog window provided by the OS
		File file = fileChooser.showSaveDialog(null);
		
		// if input is valid get to saving
		if(file != null) {
			
			// save file information
			filePath = file.getAbsolutePath();
			fileName = file.getName();
			
			try {
				
				// ask for password if needed
				if(currentMeta.getOperation() == Operation.Password) {
					PasswordDialog pd = new PasswordDialog();
				    Optional<String> result = pd.showAndWait();
				    result.ifPresent(password -> currentMeta.setPassword(password));
				}
				
				// use FileManager class to encrypt and persist the data
				FileManager.saveToPath(file, textArea.getText(), currentMeta);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Getter-method for our metadata object. Used by GUI to
	 * manipulate and fill the object with information.
	 *
	 * @return the metadata object used by this model instance.
	 */
	public MetaData getCurrentMeta() {
		return this.currentMeta;
	}
}
