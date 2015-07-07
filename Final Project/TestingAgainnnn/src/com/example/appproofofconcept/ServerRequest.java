package com.example.appproofofconcept;

public enum ServerRequest {
	NEW_CALCULATION(100), LOAD_CALCULATOR_CLASS(101), BECOME_DORMANT(102), REGISTER_REQUEST(
			103);

	private int requestNum;

	private ServerRequest(int requestNum) {
		this.requestNum = requestNum;
	}

	public int getRequestNum() {
		return requestNum;
	}

}
