package src;

import Enums.EncryptionType;
import Enums.KeyLength;
import Enums.Operation;
import Enums.EncryptionMode;
import Enums.PaddingType;

import src.Model;

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
	private Stage passwordStage;
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
            	//Model new
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
			
			gridPane.getRowConstraints().add(new RowConstraints(50));
			
			Text operationText = new Text("Operation");
			Text encryptionText = new Text("Encryption Method");
			Text keyLengthText = new Text("Key Length");
			Text modeText = new Text("Encryption Mode");
			Text paddingText = new Text("Padding");
			
			GridPane.setHalignment(operationText, 	HPos.CENTER);
			GridPane.setHalignment(encryptionText, 	HPos.CENTER);
			GridPane.setHalignment(keyLengthText, 	HPos.CENTER);
			GridPane.setHalignment(modeText, 		HPos.CENTER);
			GridPane.setHalignment(paddingText, 	HPos.CENTER);
			
			gridPane.add(operationText,	0, 0);
			gridPane.add(encryptionText,1, 0);
			gridPane.add(keyLengthText, 2, 0);
			gridPane.add(modeText, 		3, 0);
			gridPane.add(paddingText, 	4, 0);
			
			ComboBox<PaddingType> paddingBox = new ComboBox<PaddingType>();
			paddingBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	model.getCurrentMeta().setPaddingType(paddingBox.getValue());
	            }
			});
			
			ComboBox<EncryptionMode> modeBox = new ComboBox<EncryptionMode>();
			modeBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	paddingBox.getItems().setAll(PaddingType.getPaddingByMode(modeBox.getValue()));
	            	paddingBox.setDisable(paddingBox.getItems().toString() == "[]");
	            	model.getCurrentMeta().setEncryptionMode(modeBox.getValue());
	            }
			});
			
			ComboBox<KeyLength> keyLengthBox = new ComboBox<KeyLength>();
			keyLengthBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//HANDLE DOES NOT TRIGGER WHEN COMBOBOX IS EMPTY
	            	model.getCurrentMeta().setKeyLength(keyLengthBox.getValue());
	            }
			});
			
			ComboBox<EncryptionType> encryptionBox = new ComboBox<EncryptionType>();
			encryptionBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	keyLengthBox.getItems().setAll(KeyLength.getKeyLength(encryptionBox.getValue()));
	            	keyLengthBox.setDisable(keyLengthBox.getItems().toString() == "[]");
	            	
	            	model.getCurrentMeta().setEncryptionType(encryptionBox.getValue());
	            	
	            }
			});
			
			ComboBox<Operation> operationBox = new ComboBox<Operation>();
			operationBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	encryptionBox.getItems().setAll(EncryptionType.getValuesByOperation(operationBox.getValue()));
	            	modeBox.getItems().setAll(EncryptionMode.getModeByOperation(operationBox.getValue()));
	            }
			});
			
			operationBox.setValue(Operation.Symmetric);
			operationBox.getItems().addAll(Operation.values());
			
			GridPane.setHalignment(operationBox, HPos.CENTER);
			GridPane.setHalignment(encryptionBox, HPos.CENTER);
			GridPane.setHalignment(keyLengthBox, HPos.CENTER);
			GridPane.setHalignment(modeBox, HPos.CENTER);
			GridPane.setHalignment(paddingBox, HPos.CENTER);
			
			operationBox.setMinWidth(150);
			encryptionBox.setMinWidth(150);
			keyLengthBox.setMinWidth(150);
			modeBox.setMinWidth(150);
			paddingBox.setMinWidth(150);
			
			gridPane.add(operationBox, 0, 1);
			gridPane.add(encryptionBox, 1, 1);
			gridPane.add(keyLengthBox, 2, 1);
			gridPane.add(modeBox, 3, 1);
			gridPane.add(paddingBox, 4, 1);
			
			Button doneButton = new Button("Done");
			doneButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	boolean temp = operationBox.getValue() == Operation.Password;
	            	
	            	encryptionStage.close();
	            	
	            	if(temp)
	            		openPasswordWindow();
	            }
			});
			
			GridPane.setHalignment(doneButton, HPos.RIGHT);
			GridPane.setValignment(doneButton, VPos.BOTTOM);
			
			GridPane.setMargin(doneButton, new Insets(10, 10, 10, 10));
			
			gridPane.add(doneButton, 4, 2);
			
			encryptionStage.setScene(new Scene(gridPane, 1000, 125));
			
			encryptionStage.setResizable(false);
			
		}
		
		encryptionStage.show();
	}
	
	public void openPasswordWindow() {
		
		if(passwordStage == null) {
			passwordStage = new Stage();
			passwordStage.setTitle("Enter password");
		
			GridPane gridPane = new GridPane();
			
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			
			TextField passwordText = new TextField();
			
			passwordText.setPromptText("Enter password");
			passwordText.setMinWidth(150);
			
			Button passwordButton = new Button("Done");
			passwordButton.setMinWidth(50);
			
			passwordButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	model.setPassword(passwordText.getText());
	            	
	            	passwordStage.close();
	            }
			});
			
			gridPane.add(passwordText, 0, 0);
			gridPane.add(passwordButton, 1, 0);
			
			gridPane.setHalignment(passwordText, HPos.CENTER);
			gridPane.setHalignment(passwordButton, HPos.LEFT);
			
			gridPane.setMargin(passwordText, new Insets(10));
			
			passwordStage.setScene(new Scene(gridPane, 270, 50));
		}
		
		passwordStage.show();
	}

}
