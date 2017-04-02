package application;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class MainWindowController {
	
	@FXML AnchorPane anchorPane;
	@FXML ImageView pdfContainer;
	@FXML HBox pdfContainerHBox;

	@FXML
	private void clickOpen() throws IOException {
		File selectedFile = openFileChoser();
		openPDFFile(selectedFile);
	}

	private void openPDFFile(File selectedFile) {
		PDDocument pdfFile = null;
		
		if (selectedFile == null){
			return;
		}
		try {
			pdfFile = PDDocument.load(selectedFile);
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		PDFRenderer renderer = new PDFRenderer(pdfFile);
		Image image = null;
		try {
			image = SwingFXUtils.toFXImage(renderer.renderImage(0), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pdfContainer.setImage(image);
		
		anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {			
    		pdfContainer.setX(pdfContainer.getX() + newVal.doubleValue() - oldVal.doubleValue());	
    		pdfContainer.setFitWidth(pdfContainer.getFitWidth() + newVal.doubleValue() - oldVal.doubleValue());
			pdfContainer.preserveRatioProperty();
		});
		
		anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
			pdfContainer.setFitHeight(pdfContainer.getFitHeight() + newVal.doubleValue() - oldVal.doubleValue());
			pdfContainer.preserveRatioProperty();
		});
	}

	private File openFileChoser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}
}
