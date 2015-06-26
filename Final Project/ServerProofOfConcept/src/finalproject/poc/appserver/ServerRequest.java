package finalproject.poc.appserver;

public enum ServerRequest {
	NEW_CALCULATION(0), LOAD_CALCULATOR_CLASS(1), LOAD_WORK_PACKET_CLASS(2), 
		LOAD_RESULT_PACKET_CLASS(3);

	private int requestNum;

	private ServerRequest(int requestNum) {
		this.requestNum = requestNum;
	}

	public int getRequestNum() {
		return requestNum;
	}

}
