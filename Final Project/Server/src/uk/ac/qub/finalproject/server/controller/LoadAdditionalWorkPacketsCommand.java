/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.AbstractWorkPacketLoader;

/**
 * @author Phil
 *
 */
public class LoadAdditionalWorkPacketsCommand implements Command {

	private AbstractWorkPacketLoader workPacketLoader;

	public LoadAdditionalWorkPacketsCommand() {

	}

	public LoadAdditionalWorkPacketsCommand(AbstractWorkPacketLoader workPacketLoader) {
		setWorkPacketLoader(workPacketLoader);
	}

	public void setWorkPacketLoader(AbstractWorkPacketLoader workPacketLoader){
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
