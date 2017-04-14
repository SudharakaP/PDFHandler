package mainWindow;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;

import application.Main;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.Group;

public class MainWindowController {
	
	@FXML AnchorPane anchorPane;
	@FXML ImageView pdfContainer;
	@FXML Button prevButton;
	@FXML Button nextButton;
	@FXML TextField pageNumber;
	@FXML Button zoomInButton;
	@FXML Button zoomOutButton;
	@FXML ScrollPane scrollPane;
	@FXML HBox pdfContainerHBox;
	@FXML Group groupContainer;
	
	private int pageNo = 0;
	private PDDocument pdfFile;
	private int endOfPageScrollCount;
		
	@FXML
	public void initialize(){
		Context.getContext().setMainWindow(this);
		scrollPane.setContent(groupContainer);
	}

	/**
	 * Action method for File -> Open menu item.
	 * @throws IOException
	 */
	@FXML
	private void clickOpen() throws IOException {
		File selectedFile = openFileChoser();
		openPDFFile(selectedFile);
	}
	
	/**
	 *  Action method for File -> Exit menu item.
	 */
	@FXML
	private void clickExit() {
		Platform.exit();
	}
	
	/**
	 *  Action method for File -> Save menu item.
	 */
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
	
	/**
	 *  Action method for File -> Print menu item.
	 */
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

	/**
	 * Open selected PDF file.
	 * @param selectedFile
	 */
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
		zoomListeners();
	}
	
	/**
	 * Action method for Edit -> Remove Current Page menu item.
	 */
	@FXML
	private void clickRemovePage(){
		if (pdfFile != null){
			pdfFile.removePage(pageNo);
			openPDFPage(pageNo);
		}
	}
	
	/**
	 * Action method for Edit -> Rotate Page menu item.
	 */
	@FXML
	private void clickRotatePage(){
		if (pdfFile != null){
			DialogPane rotationDialogPane = null;
			try {
				rotationDialogPane = (DialogPane) FXMLLoader.load(Main.class.getResource("../mainWindow/RotationDialog.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Stage stage = new Stage(StageStyle.UNDECORATED);
			Scene scene = new Scene(rotationDialogPane);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPane.getScene().getWindow());
            stage.show();
		}
	}

	private void zoomListeners() {
		zoomInButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				pdfContainer.setScaleX(1.5 * pdfContainer.getScaleX());	
				pdfContainer.setScaleY(1.5 * pdfContainer.getScaleY());	
			}		
		});
		
		zoomOutButton.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				pdfContainer.setScaleX(0.5 * pdfContainer.getScaleX());
				pdfContainer.setScaleY(0.5 * pdfContainer.getScaleY());
			}		
		});
	}
	
	/**
	 * Listens to mouse scrolls
	 */
	private void scrollListeners(){
		pdfContainer.setOnScroll(new EventHandler<ScrollEvent>(){
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0 && pageNo != 0 && scrollPane.vvalueProperty().get() == scrollPane.getVmin()){
					if (endOfPageScrollCount == 3){
						openPDFPage(--pageNo);
						endOfPageScrollCount = 0;
					}else{
						endOfPageScrollCount++;
					}
				} else if (event.getDeltaY() < 0 && pageNo != pdfFile.getNumberOfPages() - 1 
						&& scrollPane.vvalueProperty().get() == scrollPane.getVmax()) {
					if (endOfPageScrollCount == 3){
						openPDFPage(++pageNo);
						endOfPageScrollCount = 0;
					}else{
						endOfPageScrollCount++;
					}
				}
			}			
		});
	}

	/**
	 * Listens to next (nextButton) and previous (nextButton) button clicks
	 */
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

	/**
	 * Opens/Displays the PDF page by converting it into a BufferedImage object
	 * @param pageNo
	 */
	protected void openPDFPage(int pageNo){
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
        scrollPane.setVvalue(scrollPane.getVmin());
	}

	/**
	 * Listeners for the AnchorPane which embeds the main window. 
	 */
	private void anchorPaneListeners() {
		anchorPane.widthProperty().addListener((obs, oldVal, newVal) -> {			
    		pdfContainer.setX(pdfContainer.getX() + newVal.doubleValue() - oldVal.doubleValue());	
    		pdfContainer.setFitWidth(pdfContainer.getFitWidth() + newVal.doubleValue() - oldVal.doubleValue());			
		});
		
		anchorPane.heightProperty().addListener((obs, oldVal, newVal) -> {
			pdfContainer.setFitHeight(pdfContainer.getFitHeight() + newVal.doubleValue() - oldVal.doubleValue());
		});
	}

	/**
	 * Opens the file chooser dialog.
	 * @return the chosen file path
	 */
	private File openFileChoser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}
	
	
	/**
	 * Opens the file saving dialog.
	 * @return the selected filepath
	 */
	private File savePDFFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save PDF File");
		fileChooser.getExtensionFilters().add(new ExtensionFilter("PDF Files", "*.pdf"));
		File selectedFile = fileChooser.showSaveDialog(anchorPane.getScene().getWindow());
		return selectedFile;
	}

	/**
	 * Handles the rotation of pages.
	 * @param angle
	 */
	public void rotatePage(int angle) {				
		pdfFile.getPage(pageNo).setRotation(angle + pdfFile.getPage(pageNo).getRotation());
		openPDFPage(pageNo);
	}
}
