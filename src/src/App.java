package src;

import Enums.EncryptionType;
import Enums.ModeType;
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
import javafx.scene.control.ToggleGroup;
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
			
			gridPane.getRowConstraints().add(new RowConstraints(50));
			
			Text encryptionText = new Text("Encryption Method");
			Text modeText = new Text("Mode");
			Text paddingText = new Text("Padding Method");
			
			GridPane.setHalignment(encryptionText, HPos.CENTER);
			GridPane.setHalignment(modeText, HPos.CENTER);
			GridPane.setHalignment(paddingText, HPos.CENTER);
			
			gridPane.add(encryptionText, 0, 0);
			gridPane.add(modeText, 1, 0);
			gridPane.add(paddingText, 2, 0);
			
			ComboBox<EncryptionType> encryptionBox = new ComboBox<EncryptionType>();
			encryptionBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	model.setEncryptionType(encryptionBox.getValue());
	            }
			});
			
			encryptionBox.setValue(EncryptionType.none);
			encryptionBox.getItems().addAll(EncryptionType.values());
			
			ComboBox<ModeType> modeBox = new ComboBox<ModeType>();
			modeBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	model.setModeType(modeBox.getValue());
	            }
			});
			
			modeBox.setValue(ModeType.ECB);
			modeBox.getItems().addAll(ModeType.values());
			
			
			ComboBox<PaddingType> paddingBox = new ComboBox<PaddingType>();
			paddingBox.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	
	            	//TODO change other dropdown options depending on the encryption
	            	model.setPaddingType(paddingBox.getValue());
	            }
			});
			
			paddingBox.setValue(PaddingType.NoPadding);
			paddingBox.getItems().addAll(PaddingType.values());
			
			GridPane.setHalignment(encryptionBox, HPos.CENTER);
			GridPane.setHalignment(modeBox, HPos.CENTER);
			GridPane.setHalignment(paddingBox, HPos.CENTER);
			
			encryptionBox.setMinWidth(150);
			modeBox.setMinWidth(150);
			paddingBox.setMinWidth(150);
			
			gridPane.add(encryptionBox, 0, 1);
			gridPane.add(modeBox, 1, 1);
			gridPane.add(paddingBox, 2, 1);
			
			Button doneButton = new Button("Done");
			doneButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	encryptionStage.close();
	            }
			});
			
			GridPane.setHalignment(doneButton, HPos.RIGHT);
			GridPane.setValignment(doneButton, VPos.BOTTOM);
			
			GridPane.setMargin(doneButton, new Insets(10, 10, 10, 10));
			
			gridPane.add(doneButton, 2, 2);
			
			encryptionStage.setScene(new Scene(gridPane, 600, 125));
			
			encryptionStage.setResizable(false);
			
		}
		
		encryptionStage.show();
	}

}
