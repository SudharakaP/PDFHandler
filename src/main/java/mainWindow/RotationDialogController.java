/*******************************************************************************
 * Copyright 2017 Sudharaka Palamakumbura
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package mainWindow;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class RotationDialogController {
	@FXML
	private ChoiceBox<String> rotationAngle;
	
	@FXML
	private DialogPane dialogPane;
	
	private Node applyButton;
	private Node cancelButton;
	
	private MainWindowController mainWindowController;
	
	@FXML 
	public void initialize(){
		mainWindowController = Context.getContext().getMainWindow();
		ObservableList<String> observableList = FXCollections.observableArrayList();
		observableList.add("90 Degrees Clockwise");
		observableList.add("180 Degrees");
		observableList.add("90 Degrees Counter Clockwise");
		rotationAngle.setItems(observableList);
		
		applyButton = dialogPane.lookupButton(ButtonType.APPLY);
		cancelButton = dialogPane.lookupButton(ButtonType.CANCEL);
		keyListeners();
	}
	
	/**
	 * Listens to Apply button click in the Edit -> Rotate Page dialog box.
	 */
	private void keyListeners(){
		applyButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				String value = rotationAngle.getValue();
				switch (value){
					case "90 Degrees Clockwise":
						mainWindowController.rotatePage(90);
						closeDialog();
						break;
					case "180 Degrees":
						mainWindowController.rotatePage(180);
						closeDialog();
						break;
					case "90 Degrees Counter Clockwise":
						mainWindowController.rotatePage(270);
						closeDialog();
						break;
				}			
			}

			private void closeDialog() {
				Stage dialogStage = (Stage)dialogPane.getScene().getWindow();
				dialogStage.close();
			}			
		});
		
		cancelButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				Stage dialogStage = (Stage)dialogPane.getScene().getWindow();
				dialogStage.close();
			}
		});
	}
}
