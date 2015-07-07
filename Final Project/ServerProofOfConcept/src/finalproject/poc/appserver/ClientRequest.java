package finalproject.poc.appserver;

public enum ClientRequest {
	REGISTER(0), CHANGE_EMAIL(1), DEREGISTER_DEVICE(2), DEREGISTER_USER(3), PROCESS_RESULT(
			4), REQUEST_WORK_PACKET(5), REQUEST_CALCULATION_CLASS(6);

	private int requestNum;

	private ClientRequest(int requestNum) {
		this.requestNum = requestNum;
	}

	public int getRequestNum() {
		return requestNum;
	}

}
