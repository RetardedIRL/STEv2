package presentation;

import logic.Model;

import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;
import Enums.EncryptionMode;
import Enums.PaddingType;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class App extends Application {

	private Stage encryptionStage;
	private Model model;
	
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Create GUI and start it
	 */
	@Override
	public void start(Stage stage) throws Exception {
		
		TextArea textArea = new TextArea();

		model = new Model(textArea);
		
		
		BorderPane borderPane = new BorderPane();
		MenuBar menuBar = new MenuBar();
		
		// ------------------------ File Menu ------------------------
		Menu fileMenu = new Menu("File");
		
		MenuItem menuItemNew = new MenuItem("New");
		menuItemNew.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	model.newFile();
            }
        });
		
		MenuItem menuItemOpen = new MenuItem("Open");
		menuItemOpen.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	model.open();
            }
        });
		MenuItem menuItemSave = new MenuItem("Save");
		menuItemSave.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	model.save();
            }
        });
		MenuItem menuItemSaveAs = new MenuItem("Save as..");
		menuItemSaveAs.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	model.saveAs();
            }
        });
		MenuItem menuItemExit = new MenuItem("Exit");
		menuItemExit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	//Model exit
            }
        });
		
		fileMenu.getItems().addAll(menuItemNew, menuItemOpen, menuItemSave, menuItemSaveAs, menuItemExit);
		
		// ------------------------ Encryption Menu ------------------------
		
		Menu encryptionMenu = new Menu("Encryption");
		
		MenuItem menuItemEncryption = new MenuItem("Choose Encryption");
		menuItemEncryption.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	openEncryptionWindow();
            }
        });
		
		encryptionMenu.getItems().addAll(menuItemEncryption);
		
		menuBar.getMenus().addAll(fileMenu, encryptionMenu);
		
		//Set key shortcuts
		menuItemNew.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		menuItemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		menuItemSave.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		menuItemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		
		borderPane.setTop(menuBar);
		borderPane.setCenter(textArea);
		stage.setScene(new Scene(borderPane, 600, 400));
		stage.show();
		
	}
	
	/**
	 * Manages encryption window GUI.
	 * 
	 * On first call it creates the stage, any calls later simply display the scene.
	 */
	public void openEncryptionWindow() {

		if(encryptionStage == null) {
			encryptionStage = new Stage();
			encryptionStage.setTitle("Encryption");
		
			GridPane gridPane = new GridPane();
			
			//Constraints
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			
			gridPane.getRowConstraints().add(new RowConstraints(50));
			
			Text operationText = new Text("Operation");
			Text encryptionText = new Text("Encryption Method");
			Text keyLengthText = new Text("Key Length");
			Text modeText = new Text("Encryption Mode");
			Text paddingText = new Text("Padding");
			Text hashFText = new Text("Hash Function");
			
			GridPane.setHalignment(operationText, 	HPos.CENTER);
			GridPane.setHalignment(encryptionText, 	HPos.CENTER);
			GridPane.setHalignment(keyLengthText, 	HPos.CENTER);
			GridPane.setHalignment(modeText, 		HPos.CENTER);
			GridPane.setHalignment(paddingText, 	HPos.CENTER);
			GridPane.setHalignment(hashFText, 		HPos.CENTER);
			
			gridPane.add(operationText,	0, 0);
			gridPane.add(encryptionText,1, 0);
			gridPane.add(keyLengthText, 2, 0);
			gridPane.add(modeText, 		3, 0);
			gridPane.add(paddingText, 	4, 0);
			gridPane.add(hashFText, 	5, 0);
			
			ComboBox<PaddingType> paddingBox = new ComboBox<PaddingType>();
			paddingBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	model.getCurrentMeta().setPaddingType(paddingBox.getValue());
	            }
			});
			
			paddingBox.setValue(PaddingType.NoPadding);
			model.getCurrentMeta().setPaddingType(PaddingType.NoPadding);
			
			ComboBox<EncryptionMode> modeBox = new ComboBox<EncryptionMode>();
			modeBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	paddingBox.getItems().setAll(PaddingType.getPaddingByMode(modeBox.getValue()));
	            	paddingBox.setDisable(paddingBox.getItems().toString() == "[]");
	            	model.getCurrentMeta().setEncryptionMode(modeBox.getValue());
	            }
			});
			
			modeBox.setValue(EncryptionMode.ECB);
			model.getCurrentMeta().setEncryptionMode(EncryptionMode.ECB);
			
			ComboBox<KeyLength> keyLengthBox = new ComboBox<KeyLength>();
			keyLengthBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//HANDLE DOES NOT TRIGGER WHEN COMBOBOX IS EMPTY
	            	model.getCurrentMeta().setKeyLength(keyLengthBox.getValue());
	            }
			});
			
			keyLengthBox.setValue(KeyLength.x64);
			model.getCurrentMeta().setKeyLength(KeyLength.x64);
			
			ComboBox<EncryptionType> encryptionBox = new ComboBox<EncryptionType>();
			encryptionBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	keyLengthBox.getItems().setAll(KeyLength.getKeyLength(encryptionBox.getValue()));
	            	keyLengthBox.setDisable(keyLengthBox.getItems().toString() == "[]");
	            	
	            	model.getCurrentMeta().setEncryptionType(encryptionBox.getValue());
	            	
	            }
			});
			
			encryptionBox.setValue(EncryptionType.DES);
			model.getCurrentMeta().setEncryptionType(EncryptionType.DES);
			
			ComboBox<HashFunction> hashFBox = new ComboBox<HashFunction>();
			hashFBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	model.getCurrentMeta().setHashFunction(hashFBox.getValue());
	            }
			});
			
			hashFBox.setValue(HashFunction.NONE);
			model.getCurrentMeta().setHashFunction(HashFunction.NONE);
			hashFBox.getItems().addAll(HashFunction.values());
			
			ComboBox<Operation> operationBox = new ComboBox<Operation>();
			operationBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	Operation operation = operationBox.getValue();
	            	
	            	switch(operation) {
	            	
	            	case Symmetric:
	            		
            			encryptionBox.getItems().setAll(EncryptionType.getValuesByOperation(Operation.Symmetric));
            			modeBox.getItems().setAll(EncryptionMode.getModeByOperation(Operation.Symmetric));
            			
            			encryptionBox.setValue(EncryptionType.DES);
            			encryptionBox.setDisable(false);
            			
            			modeBox.setValue(EncryptionMode.ECB);
            			modeBox.setDisable(false);
            			
            			paddingBox.setValue(PaddingType.NoPadding);
            			paddingBox.setDisable(false);
            			
            			keyLengthBox.setValue(KeyLength.x64);
            			paddingBox.setDisable(false);
	            		break;
	            		
	            	case Asymmetric:
	            		
            			encryptionBox.setValue(EncryptionType.RSA);
            			encryptionBox.setDisable(true);
            			
            			modeBox.setValue(EncryptionMode.None);
            			modeBox.setDisable(true);
            			
            			paddingBox.setValue(PaddingType.NoPadding);
            			paddingBox.setDisable(true);
            			
            			keyLengthBox.setValue(KeyLength.x1024);
            			keyLengthBox.setDisable(false);
            			break;
            			
	            	case Password:

	            		encryptionBox.getItems().setAll(EncryptionType.getValuesByOperation(Operation.Password));
	            		encryptionBox.setValue(EncryptionType.PBEWithMD5AndDES);
	            		encryptionBox.setDisable(false);
	            		
	            		modeBox.setDisable(true);
	            		paddingBox.setDisable(true);
	            		keyLengthBox.setDisable(true);
	            		break;
	            		
	            	default:
	            		break;
	            	}

	            	model.getCurrentMeta().setOperation(operationBox.getValue());
	            }
			});
			
			operationBox.setValue(Operation.Symmetric);
			model.getCurrentMeta().setOperation(Operation.Symmetric);
			operationBox.getItems().addAll(Operation.values());
			
			
			GridPane.setHalignment(operationBox, HPos.CENTER);
			GridPane.setHalignment(encryptionBox, HPos.CENTER);
			GridPane.setHalignment(keyLengthBox, HPos.CENTER);
			GridPane.setHalignment(modeBox, HPos.CENTER);
			GridPane.setHalignment(paddingBox, HPos.CENTER);
			GridPane.setHalignment(hashFBox, HPos.CENTER);
			
			operationBox.setMinWidth(150);
			encryptionBox.setMinWidth(150);
			keyLengthBox.setMinWidth(150);
			modeBox.setMinWidth(150);
			paddingBox.setMinWidth(150);
			hashFBox.setMinWidth(150);
			
			gridPane.add(operationBox, 0, 1);
			gridPane.add(encryptionBox, 1, 1);
			gridPane.add(keyLengthBox, 2, 1);
			gridPane.add(modeBox, 3, 1);
			gridPane.add(paddingBox, 4, 1);
			gridPane.add(hashFBox, 5, 1);
			
			Button doneButton = new Button("Done");
			doneButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	encryptionStage.close();

	            }
			});
			
			GridPane.setHalignment(doneButton, HPos.RIGHT);
			GridPane.setValignment(doneButton, VPos.BOTTOM);
			
			GridPane.setMargin(doneButton, new Insets(10, 10, 10, 10));
			
			gridPane.add(doneButton, 5, 2);
			
			encryptionStage.setScene(new Scene(gridPane, 1150, 125));
			
			encryptionStage.setResizable(false);
			
		}
		encryptionStage.show();
	}
}
