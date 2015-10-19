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

For some problems it may be necessary to use some form of fuzzy analysis.
If this is the case, you'll need to implement a method to get the most accurate result and a method to get device statistics after a group computation has finished.

```Java
	/**
	 * Compares a list of pending results and returns the most accurate result.
	 * 
	 * @param pendingResults
	 * @param initalData
	 * @return
	 */
	public IResultsPacket compareGroupResults(
			Map<String, IResultsPacket> pendingResults, IWorkPacket initalData){
	   // using a square root problem as an example
	   Double originalNum = (Double) initialData.getInitialData();
	   
	   Integer closestResult = 0;
	   for (String packetID : pendingResults.keySet()) {
	      Double nextResult = (Double) pendingResults.get(packetID);
	      
	      if (originalNum - (nextResult*nextResult) < originalNum - (closestResult*closestResult){
	         closestResult = nextResult;
	      }
	   }
	   
	   return closestResult;
	}

	/**
	 * Called straight after compareGroupResults.
	 * Returns a key value pair of device ID and a boolean representing whether
	 * a device's result was valid or not.
	 * 
	 * @param pendingResults
	 * @param exemplar
	 * @return
	 */
	public Map<String, Boolean> getDeviceStatistics(
			Map<String, IResultsPacket> pendingResults, IResultsPacket exemplar) {
	   Map<String, Boolean> stats = new HashMap<String, Boolean>();
	   
	   for (String deviceID: pendingResults.keySet()) {
	   
	      // check to see if the result from this device is good enough
	      // if it's not, they will receive a strike against them
	      // if they get enough strikes against them they will be blacklisted
	      boolean goodEnough = isGoodEnough(pendingResults.get(deviceID, exemplar); 
	      stats.put(deviceID, goodEnough); 
	   }
	   
	   return stats;
	}
	
	private boolean isGoodEnough(IResultsPacket pending, IResultsPacket exemplar) {
	   // see if the result is acceptably close to the exemplar
	}

```
