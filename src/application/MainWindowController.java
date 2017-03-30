package application;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainWindowController {
	
	@FXML
	private void clickOpen() throws IOException {
		AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("MainWindow.fxml"));
		Scene scene = new Scene(page);
		Stage primaryStage = new Stage();
		primaryStage.setScene(scene);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(primaryStage);
	}
}
