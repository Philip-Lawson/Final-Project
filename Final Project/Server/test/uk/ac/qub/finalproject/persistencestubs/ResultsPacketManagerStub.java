/**
 * 
 */
package uk.ac.qub.finalproject.persistencestubs;

import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.persistence.ResultsPacketManager;

/**
 * @author Phil
 *
 */
public class ResultsPacketManagerStub extends ResultsPacketManager {
	
	private boolean allResultsComplete;
	private boolean resultWritten;
	private boolean resultIsSaved;
	private IResultsPacket resultsPacket;
	
	@Override
	public boolean allResultsComplete(){
		return allResultsComplete;
	}
	
	@Override
	public void writeResult(IResultsPacket resultsPacket){
		resultWritten = true;
		this.resultsPacket = resultsPacket;
	}
	
	@Override
	public void writeResultsList(Collection<IResultsPacket> packetList){
		resultWritten = true;
		if(packetList.size() > 0)
		this.resultsPacket = packetList.iterator().next();
	}
	
	@Override
	public IResultsPacket getResultForComparison(String packetID){
		return resultsPacket;
	}
	
	@Override
	public boolean resultIsSaved(String packtID){
		return resultIsSaved;
	}
	
	public void setResultSaved(boolean resultIsSaved){
		this.resultIsSaved = resultIsSaved; 
	}

	public boolean isAllResultsComplete() {
		return allResultsComplete;
	}

	public void setAllResultsComplete(boolean allResultsComplete) {
		this.allResultsComplete = allResultsComplete;
	}

	public boolean isResultWritten() {
		return resultWritten;
	}

	public void setResultWritten(boolean resultWritten) {
		this.resultWritten = resultWritten;
	}

	public IResultsPacket getResultsPacket() {
		return resultsPacket;
	}

	public void setResultsPacket(IResultsPacket resultsPacket) {
		this.resultsPacket = resultsPacket;
	}

}
