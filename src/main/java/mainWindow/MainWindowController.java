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

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.rendering.PDFRenderer;

import application.Main;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	@FXML TextField zoomLevel;
	
	private int pageNo = 0;
	private PDDocument pdfFile;
	private float pdfPageSize;
	private double formattedZoomLevel;
	private File selectedFile;
	
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
		selectedFile = openFileChoser();
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
		} else {
			showInfoDialog("Information", "Open a PDF File First!", "Chose a PDF file to open using the File -> Open menu");
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
		} else {
			showInfoDialog("Information", "Open a PDF File First!", "Chose a PDF file to open using the File -> Open menu");
		}
	}

	private void showInfoDialog(String title, String header, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(contentText);
		alert.showAndWait();
	}

	/**
	 * Open selected PDF file.
	 * @param selectedFile
	 */
	protected void openPDFFile(File selectedFile) {		
		if (selectedFile == null){
			return;
		}
		if (pdfFile != null){
			try {
				pdfFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		try {
			pdfFile = PDDocument.load(selectedFile);
		} catch (InvalidPasswordException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		openPDFPage(pageNo = 0);		
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
			if (pageNo != pdfFile.getNumberOfPages())
				openPDFPage(pageNo);
			else 
				openPDFPage(--pageNo);
		} else {
			showInfoDialog("Information", "Open a PDF File First!", "Chose a PDF file to open using the File -> Open menu");
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
		} else {
			showInfoDialog("Information", "Open a PDF File First!", "Chose a PDF file to open using the File -> Open menu");
		}
	}

	private void zoomListeners() {
		EventHandler<MouseEvent> zoomInButtonListener = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				pdfContainer.setScaleX(1.5 * pdfContainer.getScaleX());	
				pdfContainer.setScaleY(1.5 * pdfContainer.getScaleY());	
				double zoom = 100 * pdfContainer.getFitHeight() * pdfContainer.getScaleY() / pdfPageSize;
				setFormatedZoomLevel(zoom);
				zoomLevel.setText(formattedZoomLevel + "%");
			}		
		};
		zoomInButton.setOnMouseClicked(null);
		zoomInButton.setOnMouseClicked(zoomInButtonListener);
		
		EventHandler<MouseEvent> zoomOutButtonListener = new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				pdfContainer.setScaleX(2.0 * pdfContainer.getScaleX() / 3.0);
				pdfContainer.setScaleY(2.0 * pdfContainer.getScaleY() / 3.0);
				double zoom = 100 * pdfContainer.getFitHeight() * pdfContainer.getScaleY() / pdfPageSize;
				setFormatedZoomLevel(zoom);
				zoomLevel.setText(formattedZoomLevel + "%");
			}		
		};
		zoomOutButton.setOnMouseClicked(null);
		zoomOutButton.setOnMouseClicked(zoomOutButtonListener);
		
		EventHandler<ActionEvent> zoomLevelListener = new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				try {
					String zoom = zoomLevel.getText();
					if (zoom.contains("%")){
						zoom = zoom.replace("%", "");
					}
					pdfContainer.setFitHeight(pdfPageSize * Double.parseDouble(zoom) / 100);
					zoomLevel.setText(zoom + "%");
				} catch (NumberFormatException exception){
					// Do nothing on invalid input to the textbox
				}
			}		
		};
		zoomLevel.setOnAction(null);
		zoomLevel.setOnAction(zoomLevelListener);
	}

	
	/**
	 * Formats the zoom level that is displayed in the textbox
	 * 
	 * @param zoomLevel
	 */
	private void setFormatedZoomLevel(double zoomLevel){
		BigDecimal bigDecimal = new BigDecimal(zoomLevel);
        bigDecimal = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP);
        formattedZoomLevel = bigDecimal.doubleValue();
	}
	
	/**
	 * Listens to mouse scrolls
	 */
	private void scrollListeners(){
		EventHandler<ScrollEvent> scrollListener = new EventHandler<ScrollEvent>(){
			@Override
			public void handle(ScrollEvent event) {
				if (event.getDeltaY() > 0 && pageNo != 0 && scrollPane.vvalueProperty().get() == scrollPane.getVmin()){			
						openPDFPage(--pageNo);
				} else if (event.getDeltaY() < 0 && pageNo != pdfFile.getNumberOfPages() - 1 
						&& scrollPane.vvalueProperty().get() == scrollPane.getVmax()) {				
						openPDFPage(++pageNo);
				}
			}			
		};
		scrollPane.setOnScroll(null);
		scrollPane.setOnScroll(scrollListener);
	}

	/**
	 * Listens to next (nextButton) and previous (nextButton) button clicks
	 */
	private void navButtonListeners() {
		EventHandler<ActionEvent> prevButtonListener = new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if (pageNo != 0){
		    		openPDFPage(--pageNo);
		    	}
		    }
		};
		prevButton.setOnAction(null);
		prevButton.setOnAction(prevButtonListener);
		
		EventHandler<ActionEvent> nextButtonListener = new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if (pageNo != pdfFile.getNumberOfPages() - 1){
		        	openPDFPage(++pageNo);
		    	}
		    }
		};
		nextButton.setOnAction(null);
		nextButton.setOnAction(nextButtonListener);
		
		EventHandler<ActionEvent> pageNumberListener = new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent e){
				try {
					pageNo = Integer.parseInt(pageNumber.getText()) - 1;
				} catch (NumberFormatException exception){
					// Do nothing on invalid input to the textbox
				}
				if (pageNo >= 0 && pageNo < pdfFile.getNumberOfPages())
					openPDFPage(pageNo);
			}
		};
		pageNumber.setOnAction(null);
		pageNumber.setOnAction(pageNumberListener);
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
			image = SwingFXUtils.toFXImage(renderer.renderImageWithDPI(pageNo, 500), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
        pdfContainer.setImage(image);
        pageNumber.setText("" + displayNo);
        scrollPane.setVvalue(scrollPane.getVmin());
        pdfPageSize = pdfFile.getPage(pageNo).getMediaBox().getHeight();
        
        if (formattedZoomLevel == 0)
        	setFormatedZoomLevel(100 * pdfContainer.getFitHeight() / pdfPageSize);
        zoomLevel.setText(formattedZoomLevel + "%");
	}

	/**
	 * Listeners for the AnchorPane which embeds the main window. 
	 */
	private void anchorPaneListeners() {
		ChangeListener<Number> anchorPaneWidth = new ChangeListener<Number>(){
	        @Override 
	        public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
	        	pdfContainer.setTranslateX(pdfContainer.getTranslateX() + 0.5 * (newVal.doubleValue() - oldVal.doubleValue()));	
	        }
		};
		
		ChangeListener<Number> anchorPaneHeight = new ChangeListener<Number>(){
	        @Override 
	        public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
	        	pdfContainer.setFitHeight(pdfContainer.getFitHeight() + newVal.doubleValue() - oldVal.doubleValue());
				if (formattedZoomLevel != 0){
		        	setFormatedZoomLevel(100 * pdfContainer.getFitHeight() / pdfPageSize);
		        	zoomLevel.setText(formattedZoomLevel + "%");
				}
	        }
		};
		anchorPane.widthProperty().removeListener(anchorPaneWidth);
		anchorPane.widthProperty().removeListener(anchorPaneHeight);
		anchorPane.widthProperty().addListener(anchorPaneWidth);	
		anchorPane.heightProperty().addListener(anchorPaneHeight);
	}

	/**
	 * Opens the file chooser dialog.
	 * @return the chosen file path
	 */
	protected File openFileChoser() {
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

	@FXML 
	private void clickMergePDF() {
		if (pdfFile != null){
			DialogPane mergeDialogPane = null;
			try {
				mergeDialogPane = (DialogPane) FXMLLoader.load(Main.class.getResource("../mainWindow/MergePDFDialog.fxml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Stage stage = new Stage(StageStyle.UNDECORATED);
			Scene scene = new Scene(mergeDialogPane);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(anchorPane.getScene().getWindow());
            stage.show();
		} else {
			showInfoDialog("Information", "Open a PDF File First!", "Chose a PDF file to open using the File -> Open menu");
		}
	}

	public void mergePDFFiles(String pdfFile) {
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		try {
			pdfMerger.addSource(selectedFile);
			pdfMerger.addSource(new File(pdfFile));		
			pdfMerger.setDestinationFileName(selectedFile.getAbsolutePath());
			pdfMerger.mergeDocuments(null);
 			showInfoDialog("Done", "PDF Merge Successful!", "The pdf files, " + 
			selectedFile.getName() + " and " + pdfFile + " has been merged sucessfully.");
 			openPDFFile(selectedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
