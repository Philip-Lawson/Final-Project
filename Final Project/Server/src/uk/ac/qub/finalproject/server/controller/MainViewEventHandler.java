/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Phil
 *
 */
public class MainViewEventHandler implements EventHandler<ActionEvent> {
	
	private Command command;
	
	public MainViewEventHandler(Command command){
		this.command = command;
	}

	@Override
	public void handle(ActionEvent event) {
		command.execute();		
	}

}
