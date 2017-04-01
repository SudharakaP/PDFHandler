package application;

import java.io.File;
import java.io.IOException;

import org.jpedal.examples.viewer.Commands;
import org.jpedal.examples.viewer.OpenViewerFX;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

public class MainWindowController {
	
	@FXML AnchorPane anchorPane;

	@FXML
	private void clickOpen() throws IOException {
		File selectedFile = openFileChoser();
		openPDFFile(selectedFile);
	}

	private void openPDFFile(File selectedFile) {
		Stage pdfStage = new Stage();
		OpenViewerFX pdfViewer = new OpenViewerFX(pdfStage, null);
		pdfViewer.setupViewer();
		Object[] input =  {selectedFile};
		pdfViewer.executeCommand(Commands.OPENFILE, input);
	}

	private File openFileChoser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}
}
