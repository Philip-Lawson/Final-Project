/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

/**
 * This is the view that appears to the user when the application first opens.
 * It is used to inform them of progress when setting up the application and
 * loading data from the database.
 * 
 * @author Phil
 *
 */
public class LoadingScreenView {

	@FXML
	private Label title;

	@FXML
	private Label progressMessage;

	@FXML
	private Label percentCompleteLabel;

	@FXML
	private ProgressBar loadingProgressBar;

	@FXML
	private ProgressIndicator progressIndicator;

	/**
	 * Sets the title of the page.
	 * 
	 * @param projectTitle
	 */
	public void setTitle(String projectTitle) {
		Platform.runLater(new Runnable() {

			public void run() {
				title.setText(projectTitle);
			}

		});
	}

	/**
	 * Updates the progress bar with the current percent and an appropriate
	 * message.
	 * 
	 * @param message
	 * @param percentComplete
	 */
	public void updateProgress(String message, int percentComplete) {
		Platform.runLater(new Runnable() {
			public void run() {
				/*
				 * Avoids a bug with JavaFX where the progress indicator doesn't
				 * animate on the first showing.
				 */
				progressIndicator.setDisable(true);
				progressIndicator.setDisable(false);

				progressMessage.setText(message);
				percentCompleteLabel.setText(percentComplete + "%");
				loadingProgressBar.setProgress(percentComplete / 100.0);
			}
		});
	}
}
