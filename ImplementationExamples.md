## Loading Work Packets

To load work packets into the system, you need to extend the AbstractWorkPacketLoader and implement the two abstract methods.

```Java
   /**
	 * This method encapsulates the logic to return the initial collection of
	 * work packets when starting the server.
	 * 
	 * 
	 * @return a collection of work packets.
	 */
	protected Collection<IWorkPacket> retrieveInitialWorkPackets(){
	
	   // load your data from an external source
	   Map <ItemID, Item> items = loadItems(); // example method
	   
	   // create the work packet collection you will return to the caller
	   Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(items.size());
	   
	   for (ItemID itemID : items.keySet() ){
	      // the work packet constructor takes a String as the packet ID
	      // and the data as a Serializable Object
	      workPackets.add(new WorkPacket(itemID.toString(), items.get(itemID)));
	   }
	
	}
	
	/**
	 * The method encapsulates the logic for returning an additional collection
	 * of work packets should the user to decide to add more work to the system.
	 * 
	 * @return
	 */
	protected abstract Collection<IWorkPacket> retrieveAdditionalWorkPackets(){
	
		 // load your data from an external source
	   Map <ItemID, Item> items = loadExtraItems(); // example method
	   
	   // create the work packet collection you will return to the caller
	   Collection<IWorkPacket> workPackets = new ArrayList<IWorkPacket>(items.size());
	   
	   for (ItemID itemID : items.keySet() ){
	      // the work packet constructor takes a String as the packet ID
	      // and the data as a Serializable Object
	      workPackets.add(new WorkPacket(itemID.toString(), items.get(itemID)));
	   }
	   
	}


```

## Transferring Work Packets

To transfer work packets you will need to implement a method to convert the Results Packets into data that you want to store. This can be done 
```Java
        private Connection connection; 

	/**
	 * Abstract method to convert a collection of results packets to a
	 * collection of the desired type.
	 * 
	 * @param resultsPackets
	 *            the collection of results packets.
	 * @return a collection of the desired object e.g. Numbers, Files etc.
	 */
	protected Collection<?> convertResults(
			Collection<IResultsPacket> resultsPackets){
	   Collection<DataBean> data = new ArrayList<DataBean>(resultsPackets.size());
	   
	   for (IResultsPacket packet: resultsPackets) {
	   	data.add(new DataBean(packet.getPacketId(), packet.getResult()));
	   }
	   
	   return data;
	}

	/**
	 * Abstract method to connect to the user's database.
	 */
	protected abstract void connectToDatabase();

	/**
	 * Abstract method to write the converted results to the user's database.
	 * 
	 * @param convertedResults
	 */
	protected void writeResults(Collection<?> convertedResults){
		for (Object data: convertedResults){
		   // cast the data to the appropriate type
		   // exception handling omitted for brevity
		   Integer dataToAdd = (Integer) data; 
		   writeResult(connection, dataToAdd);
		}
	}
	
	// example method
	private void writeResult(Connection con, DataBean data){
	   // write to the database
	}

	/**
	 * Close the connection to the database.
	 */
	protected void closeConnection(){
	   // close your connection
	}

```

## Validating Packets

You will need to validate the results that are returned by the client. 

```Java
     /**
	 * Checks that a results packet is valid. All implementations should return
	 * true if the results packet is valid.
	 * 
	 * @param workPacket
	 *            the initial work packet that was processed
	 * @param resultsPacket
	 *            the result of the processed data
	 * @return true if the result is valid
	 */
	public boolean validateNewResult(IWorkPacket workPacket,
			IResultsPacket resultsPacket) {
	   Integer data = (Integer) workPacket.getInitialData();
	   Integer result = (Integer) resultsPacket.getResult();
	   
	   return result.equals(data * 2);
	}
	
	/**
	 * Checks that a results packet is identical to a previously processed
	 * duplicate. Since the system only needs one valid result from each work
	 * packet, this method is used to verify that a client is sending valid
	 * results. <br></br>The simplest implementation of this method is a bitwise
	 * comparison.
	 * 
	 * @param resultsPacket
	 * @param savedResult
	 * @return
	 */
	public boolean compareWithSavedResult(IResultsPacket resultsPacket,
			IResultsPacket savedResult) {
	   Integer newResult = (Integer) resultsPacket.getResult();		
	   Integer oldResult = (Integer) savedResult.getResult();
	   
	   return newResult.equals(oldResult);
	}

```
## Group Validation
