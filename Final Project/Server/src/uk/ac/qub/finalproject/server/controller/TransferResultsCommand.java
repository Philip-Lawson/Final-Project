/**
 * 
 */
package uk.ac.qub.finalproject.server.controller;

import uk.ac.qub.finalproject.persistence.AbstractResultsTransferManager;

/**
 * This command transfers results to the implementers database using the results
 * transfer manager.
 * 
 * @author Phil
 *
 */
public class TransferResultsCommand implements Command {

	private AbstractResultsTransferManager resultsTransferManager;

	public TransferResultsCommand(
			AbstractResultsTransferManager resultsTransferManager) {
		this.resultsTransferManager = resultsTransferManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.qub.finalproject.server.controller.Command#execute()
	 */
	@Override
	public void execute() {
		resultsTransferManager.transferResults();
	}

}
