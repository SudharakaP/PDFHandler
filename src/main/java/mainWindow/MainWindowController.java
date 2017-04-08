package mainWindow;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
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
	
	@FXML
	private void clickExit() {
		Platform.exit();
	}
	
	@FXML
	private void clickSave() {
		if (pdfFile != null){
			File fileName = savePDFFile();
			if (fileName != null){
				try {
					pdfFile.save(fileName);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	private void clickPrint(){
		if (pdfFile != null){
			PrinterJob job = PrinterJob.getPrinterJob();
			if (job.printDialog()) {
		        try {
		        	job.print();
		        } catch (PrinterException e) {
		        	e.printStackTrace();
		         }
		     }
		}
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
		
		pdfContainer.preserveRatioProperty();
		anchorPaneListeners();
		navButtonListeners();
		scrollListeners();
	}
	
	@FXML
	private void clickRemovePage(){
		if (pdfFile != null){
			pdfFile.removePage(pageNo);
			openPDFPage(pageNo);
		}
	}
	
	@FXML
	private void clickRotatePage(){
		if (pdfFile != null){
			
			//pdfFile.getPage(pageNo).setRotation(degree);
			openPDFPage(pageNo);
		}
	}

	private void zoomListeners() {
		pdfContainer.setOnZoomStarted(new EventHandler<ZoomEvent>(){
			@Override
			public void handle(ZoomEvent event) {
				pdfContainer.setScaleX(event.getZoomFactor() * pdfContainer.getScaleX());
			}
		});	
	}
	
	private void scrollListeners(){
		pdfContainer.setOnScroll(new EventHandler<ScrollEvent>(){
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0 && pageNo != 0)
					openPDFPage(--pageNo);
				else if (event.getDeltaY() < 0 && pageNo != pdfFile.getNumberOfPages() - 1)
					openPDFPage(++pageNo);				
			}			
		});
	}

	private void navButtonListeners() {
		prevButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if (pageNo != 0){
		    		openPDFPage(--pageNo);
		    	}
		    }
		});
		
		nextButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if (pageNo != pdfFile.getNumberOfPages() - 1){
		        	openPDFPage(++pageNo);
		    	}
		    }
		});
		
		pageNumber.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				try {
					pageNo = Integer.parseInt(pageNumber.getText()) - 1;
				} catch (NumberFormatException exception){
					
				}
				if (pageNo >= 0 && pageNo < pdfFile.getNumberOfPages())
					openPDFPage(pageNo);
			}
		});
	}
	
	private void openPDFPage(int pageNo){
		int displayNo = pageNo + 1;
		PDFRenderer renderer = new PDFRenderer(pdfFile);
        Image image = null;
        try {
			image = SwingFXUtils.toFXImage(renderer.renderImage(pageNo), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        pdfContainer.setImage(image);
        pageNumber.setText("" + displayNo);
	}

	private void anchorPaneListeners() {
		anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {			
    		pdfContainer.setX(pdfContainer.getX() + newVal.doubleValue() - oldVal.doubleValue());	
    		pdfContainer.setFitWidth(pdfContainer.getFitWidth() + newVal.doubleValue() - oldVal.doubleValue());			
		});
		
		anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
			pdfContainer.setFitHeight(pdfContainer.getFitHeight() + newVal.doubleValue() - oldVal.doubleValue());
		});
	}

	private File openFileChoser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}
	
	
	private File savePDFFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}
}
