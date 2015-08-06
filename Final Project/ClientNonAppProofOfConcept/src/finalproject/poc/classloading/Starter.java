package finalproject.poc.classloading;

public class Starter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting Client");
		Thread clientThread = new Thread(new Client());
		clientThread.start();

	}


}
