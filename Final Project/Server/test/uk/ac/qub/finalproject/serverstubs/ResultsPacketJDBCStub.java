package uk.ac.qub.finalproject.serverstubs;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.qub.finalproject.calculationclasses.IResultsPacket;
import uk.ac.qub.finalproject.calculationclasses.ResultsPacket;
import uk.ac.qub.finalproject.persistence.ResultsPacketJDBC;

public class ResultsPacketJDBCStub extends ResultsPacketJDBC {

	private boolean resultWritten = false;
	private Collection<IResultsPacket> results;

	@Override
	public Collection<IResultsPacket> getResultsPackets() {
		return results;
	}

	public void writeResult(IResultsPacket resultsPacket) {
		resultWritten = true;
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
