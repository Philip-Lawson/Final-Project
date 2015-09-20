/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;

/**
 * This command loads additional work packets to the system.
 * 
 * @author Phil
 *
 */
public class LoadAdditionalWorkPacketsCommand implements Command {

	private AbstractWorkPacketLoader workPacketLoader;

	public LoadAdditionalWorkPacketsCommand(
			AbstractWorkPacketLoader workPacketLoader) {
		this.workPacketLoader = workPacketLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		workPacketLoader.loadAdditionalWorkPackets();
	}

}
