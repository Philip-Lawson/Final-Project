/**
 * 
 */
package uk.ac.qub.finalproject.server.views;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;

/**
 * The controller class for the About Window. This provides a method to set the
 * project name text, set the close button listener and a method to initialise
 * the hyperlink listener.
 * 
 * @author Phil
 *
 */
public class AboutWindowView {

	@FXML
	private Button closeButton;

	@FXML
	private Text aboutProjectNameText;

	@FXML
	private Hyperlink repoLink;

	/**
	 * Add an event handler to the close button.
	 * 
	 * @param eventHandler
	 */
	public void addCloseButtonListener(EventHandler<ActionEvent> eventHandler) {
		closeButton.setOnAction(eventHandler);
	}

	/**
	 * Sets the project name at the start of the text.
	 * 
	 * @param projectName
	 */
	public void setProjectNameText(String projectName) {
		aboutProjectNameText.setText(projectName);
	}

	/**
	 * Sets the hyper link listener. This listener will open the link on the
	 * user's default browser.
	 */
	public void initialiseHyperLinkListener() {
		repoLink.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(
								URI.create(repoLink.getText()));
					} catch (IOException e) {

					}
				}
			}

		});

	}

}
