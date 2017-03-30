package application;

import javafx.fxml.FXML;

import javafx.scene.control.MenuBar;

import javafx.scene.layout.AnchorPane;

import javafx.scene.input.MouseEvent;

public class MainWindowController {
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private MenuBar menuBar;

	// Event Listener on AnchorPane[#anchorPane].onDragDetected
	@FXML
	public void onDrag(MouseEvent event) {
		menuBar.setPrefWidth(anchorPane.getWidth());
	}
}
