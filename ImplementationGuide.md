## Work Packets

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

## Validating Packets

## Group Validation
