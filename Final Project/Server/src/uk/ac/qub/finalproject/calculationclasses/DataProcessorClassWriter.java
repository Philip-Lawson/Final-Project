/**
 * 
 */
package uk.ac.qub.finalproject.calculationclasses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Phil
 *
 */
public class DataProcessorClassWriter {
	

	private ReadWriteLock lock = new ReentrantReadWriteLock(true);
	private IDataProcessor currentProcessingClass;
	private byte[] classBytes;


	public void setCurrentProcessingClass(IDataProcessor currentProcessingClass) {
		this.currentProcessingClass = currentProcessingClass;
	}

	public byte[] getClassBytes() {
		try {
			lock.readLock().lock();
			if (null == classBytes) {
				lock.readLock().unlock();
				writeClassToBytes();
			}
			return classBytes;
		} finally {
			lock.readLock().unlock();
		}
	}

	public void changeProcessingClass(IDataProcessor newProcessingClass) {
		this.currentProcessingClass = newProcessingClass;
		writeClassToBytes();
	}

	private void writeClassToBytes() {
		lock.writeLock().lock();
		
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
		} finally {
			lock.writeLock().unlock();
		}
	}
}
