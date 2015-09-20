/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

/**
 * All UI command objects must implement this interface. Each command must
 * execute one action in the underlying system.
 * 
 * @author Phil
 *
 */
public interface Command {
	public void execute();

}
