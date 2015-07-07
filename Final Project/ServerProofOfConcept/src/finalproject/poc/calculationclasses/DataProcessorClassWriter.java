/**
 * 
 */
package finalproject.poc.calculationclasses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Phil
 *
 */
public class DataProcessorClassWriter {
	
	//TODO need to consider synchronization options

	private IDataProcessor currentProcessingClass;
	private byte[] classBytes;

	public void setCurrentProcessingClass(IDataProcessor currentProcessingClass) {
		this.currentProcessingClass = currentProcessingClass;
	}

	public byte[] getClassBytes() {
		if (null == classBytes) {
			writeClassToBytes();
		}
		return classBytes;
	}

	public void changeProcessingClass(IDataProcessor newProcessingClass) {
		this.currentProcessingClass = newProcessingClass;
		writeClassToBytes();
	}

	private void writeClassToBytes() {
		try {
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bytes);

			oos.flush();
			bytes.flush();
			oos.writeObject(currentProcessingClass.getClass());

			classBytes = bytes.toByteArray();
			bytes.close();
			oos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
