
public class Calculator {

	public void calculate(){
		for(int count=0; count<100; count++){
			System.out.println(count);	
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
