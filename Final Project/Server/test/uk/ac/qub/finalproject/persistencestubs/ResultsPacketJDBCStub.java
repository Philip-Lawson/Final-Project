package uk.ac.qub.finalproject.persistencestubs;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.ResultsPacket;
import uk.ac.qub.finalproject.persistence.ResultsPacketJDBC;

public class ResultsPacketJDBCStub extends ResultsPacketJDBC {

	private boolean resultWritten = false;
	private boolean resultsComplete;
	
	private Collection<IResultsPacket> results;

	@Override
	public Collection<IResultsPacket> getResultsPackets() {
		return results;
	}

	@Override
	public void writeResult(IResultsPacket resultsPacket) {
		resultWritten = true;
	}
	
	@Override
	public boolean allResultsComplete(){
		return resultsComplete;
	}
	
	public void setResultsComplete(boolean resultsComplete){
		this.resultsComplete = resultsComplete;
	}

	public boolean resultWritten() {
		return resultWritten;
	}

	public void addResults(int num) {
		results = new ArrayList<IResultsPacket>();

		for (int count = 0; count < num; count++) {
			ResultsPacket packet = new ResultsPacket();
			packet.setPacketId(count + "");
			packet.setResult(num);
			results.add(packet);
		}
	}

	public int getResultsSize() {
		return results.size();
	}
}
