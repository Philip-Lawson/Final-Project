package finalproject.poc.classloading;

public enum ClientRequest {
	REGISTER(0), PROCESS_RESULT(1);

	private int requestNum;

	private ClientRequest(int requestNum) {
		this.requestNum = requestNum;
	}

	public int getRequestNum() {
		return requestNum;
	}

}
