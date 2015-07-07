package com.example.appproofofconcept;

public enum ClientRequest {
	REGISTER(0), CHANGE_EMAIL(1), UNREGISTER(2), PROCESS_RESULT(3), REQUEST_WORK_PACKET(
			4), REQUEST_CALCULATION_CLASS(5);

	private int requestNum;

	private ClientRequest(int requestNum) {
		this.requestNum = requestNum;
	}

	public int getRequestNum() {
		return requestNum;
	}

}
