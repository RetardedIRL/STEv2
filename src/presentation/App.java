package presentation;

import logic.Model;

import Enums.EncryptionType;
import Enums.HashFunction;
import Enums.KeyLength;
import Enums.Operation;

import java.io.UnsupportedEncodingException;

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
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Main class of the STE application, builds GUIs and runs the program.
 * 
 * Enforces validity in the encryption GUI!
 * 
 * @author Sam
 */
public final class App extends Application {
	
	// we save the encryption stage so it only has to be built once
	private Stage encryptionStage;
	private MenuItem menuItemSave;
	private TextArea textArea;
	
	// we also save the model used in this STE
	private Model model;
	
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Create the STE GUI and show the stage, by that starting the program.
	 */
	@Override
	public void start(Stage stage) throws Exception {
		
		textArea = new TextArea();

		model = new Model(textArea);
		
		
		BorderPane borderPane = new BorderPane();
		MenuBar menuBar = new MenuBar();
		
		// ------------------------ File Menu ------------------------
		Menu fileMenu = new Menu("");

		
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
		menuItemSave = new MenuItem("Save");
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
            	model.exit();
            }
        });
		
		Label menuLabel = new Label("File");
		
		menuLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent t) {
				menuItemSave.setDisable(!model.valid);
				menuItemSaveAs.setDisable(!model.valid);
			}
		});
		
		fileMenu.setGraphic(menuLabel);
		
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
	 * To explain the GUI further, almost all the dropboxes call a function that checks for validty, sadly because of the
	 * nature of the GUI this is kind of wacky. The done button gets grayed out if the GUI wacks out and the current
	 * encryption combo is invalid.
	 * On first call it creates the stage, any calls later simply display the scene.
	 * 
	 */
	public void openEncryptionWindow() {

		if(encryptionStage == null) {
			encryptionStage = new Stage();
			encryptionStage.setTitle("Encryption");
		
			VBox vBox = new VBox();
			vBox.setPadding(new Insets(10.0));
			
			GridPane gridPane = new GridPane();
			
			//Constraints
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			gridPane.getColumnConstraints().add(new ColumnConstraints(200));
			
			gridPane.getRowConstraints().add(new RowConstraints(50));
			
			//Texts
			Text operationText = new Text("Operation");
			Text encryptionText = new Text("Encryption Method");
			Text keyLengthText = new Text("Key Length");
			Text modeText = new Text("Encryption Mode");
			Text paddingText = new Text("Padding");
			Text hashFText = new Text("Hash Function");
			Text warningText = new Text(model.checkValidity());
			
			//Alignments
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
			
			// ------------------------ Comboboxes ------------------------
			ComboBox<PaddingType> paddingBox = new ComboBox<PaddingType>();
			paddingBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	/* on action this fills the option currently
	            	 * chosen into the metadata object used. */
	            	model.getCurrentMeta().setPaddingType(paddingBox.getValue());
	            	
	            	model.checkSpecs();
	            	warningText.setText(model.checkValidity());
	            	doneButton.setDisable(!model.valid);
	            	
	            }
			});
			
			// set a dummy value to dodge annoying NullPointerException
			paddingBox.setValue(PaddingType.NoPadding);
			model.getCurrentMeta().setPaddingType(PaddingType.NoPadding);
			
			ComboBox<EncryptionMode> modeBox = new ComboBox<EncryptionMode>();
			modeBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	/* adjust the compatible padding types based on what
	            	 * encryption method selected by the user. */
	            	paddingBox.getItems().setAll(PaddingType.getPaddingByMode(modeBox.getValue()));
	            	
	            	// if padding box has no options, disable it
	            	paddingBox.setDisable(paddingBox.getItems().toString() == "[]");
	            	model.getCurrentMeta().setEncryptionMode(modeBox.getValue());
	            	
	            	model.checkSpecs();
	            	warningText.setText(model.checkValidity());
	            	doneButton.setDisable(!model.valid);
	            	
	            }
			});
			
			// dummy value
			modeBox.setValue(EncryptionMode.ECB);
			model.getCurrentMeta().setEncryptionMode(EncryptionMode.ECB);
			
			ComboBox<KeyLength> keyLengthBox = new ComboBox<KeyLength>();
			keyLengthBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//HANDLE DOES NOT TRIGGER WHEN COMBOBOX IS EMPTY
	            	model.getCurrentMeta().setKeyLength(keyLengthBox.getValue());
	            	
	            	model.checkSpecs();
	            	doneButton.setDisable(!model.valid);
	            }
			});
			
			// dummy value
			keyLengthBox.setValue(KeyLength.x64);
			model.getCurrentMeta().setKeyLength(KeyLength.x64);
			
			ComboBox<EncryptionType> encryptionBox = new ComboBox<EncryptionType>();
			encryptionBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	// set keylengths based on encryption method
	            	keyLengthBox.getItems().setAll(KeyLength.getKeyLength(encryptionBox.getValue()));
	            	keyLengthBox.setDisable(keyLengthBox.getItems().toString() == "[]");
	            	
	            	model.getCurrentMeta().setEncryptionType(encryptionBox.getValue());
	            	
	            	model.checkSpecs();
	            	warningText.setText(model.checkValidity());
	            	doneButton.setDisable(!model.valid);
	            }
			});
			
			// dummy value
			encryptionBox.setValue(EncryptionType.DES);
			model.getCurrentMeta().setEncryptionType(EncryptionType.DES);
			
			ComboBox<HashFunction> hashFBox = new ComboBox<HashFunction>();
			hashFBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	model.getCurrentMeta().setHashFunction(hashFBox.getValue());
	            	
	            	model.checkSpecs();
	            	doneButton.setDisable(!model.valid);
	            }
			});
			
			// dummy value
			hashFBox.setValue(HashFunction.NONE);
			model.getCurrentMeta().setHashFunction(HashFunction.NONE);
			hashFBox.getItems().setAll(HashFunction.values());
			
			ComboBox<Operation> operationBox = new ComboBox<Operation>();
			operationBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	/* With the operation box we enforce most of the input validity,
	            	 * simply by changing all options to only show compatible ones,
	            	 * using the compatibility methods provided in the Enums. */
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
	            	}

	            	model.getCurrentMeta().setOperation(operationBox.getValue());
	            	
	            	model.checkSpecs();
	            	warningText.setText(model.checkValidity());
	            	doneButton.setDisable(!model.valid);
	            }
			});
			
			// dummy value
			operationBox.setValue(Operation.Symmetric);
			model.getCurrentMeta().setOperation(Operation.Symmetric);
			operationBox.getItems().addAll(Operation.values());
			
			// alignments
			GridPane.setHalignment(operationBox, HPos.CENTER);
			GridPane.setHalignment(encryptionBox, HPos.CENTER);
			GridPane.setHalignment(keyLengthBox, HPos.CENTER);
			GridPane.setHalignment(modeBox, HPos.CENTER);
			GridPane.setHalignment(paddingBox, HPos.CENTER);
			GridPane.setHalignment(hashFBox, HPos.CENTER);
			
			// more constraints
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

			
			vBox.getChildren().add(gridPane);
			vBox.getChildren().add(warningText);
			
			encryptionStage.setScene(new Scene(vBox, 1200, 150));
			
			encryptionStage.setResizable(false);
			
		}
		
		try {
			model.getCurrentMeta().setText(textArea.getText().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		encryptionStage.show();
	}
}
