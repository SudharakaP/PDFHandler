package mainWindow;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MainWindowController {
	
	@FXML AnchorPane anchorPane;
	@FXML ImageView pdfContainer;
	@FXML Button prevButton;
	@FXML Button nextButton;
	@FXML TextField pageNumber;
	
	private int pageNo = 0;
	private PDDocument pdfFile;

	@FXML
	private void clickOpen() throws IOException {
		File selectedFile = openFileChoser();
		openPDFFile(selectedFile);
	}

	private void openPDFFile(File selectedFile) {
		
		
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
		openPDFPage(pageNo);
		
		anchorPaneListeners();
		navButtonListeners();
		
	}

	private void navButtonListeners() {
		prevButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        pageNo--;
		        openPDFPage(pageNo);
		    }
		});
		
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        pageNo++;
		        openPDFPage(pageNo);
		    }
		});
		
		pageNumber.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				pageNo = Integer.parseInt(pageNumber.getText());
				openPDFPage(pageNo);
			}
		});
	}
	
	private void openPDFPage(int pageNo){
		PDFRenderer renderer = new PDFRenderer(pdfFile);
        Image image = null;
        try {
			image = SwingFXUtils.toFXImage(renderer.renderImage(pageNo), null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        pdfContainer.setImage(image);
	}

	private void anchorPaneListeners() {
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
