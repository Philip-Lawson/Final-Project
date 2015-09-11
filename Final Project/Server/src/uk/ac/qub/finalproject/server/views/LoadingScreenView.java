/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
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

	public void setTitle(String projectTitle) {
		Platform.runLater(new Runnable() {

			public void run() {
				title.setText(projectTitle);
			}

		});
	}

	public void updateProgress(String message, int percentComplete) {
		Platform.runLater(new Runnable() {
			public void run() {
				progressMessage.setText(message);
				percentCompleteLabel.setText(percentComplete + "%");
				loadingProgressBar.setProgress(percentComplete / 100.0);
			}
		});
	}
}
