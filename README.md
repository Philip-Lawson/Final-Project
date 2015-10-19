# Citizen Science Coordinator

Citizen Science is a software layer for a distributed computing system using a pool of android devices. It uses a coordinator/server and a pool of android devices running the [Citizen Science app](https://github.com/Philip-Lawson/Client-App) to do the actual computation. It is designed for computation that is linearly scalable/embarrassingly parallel.

The Coordinator
* Sends data packets to devices
* Validates packets retrieved from devices
* Protects against fraudulent devices
* Visualises the progress of the computation
* Stores the processed data
* Dynamically reloads any data that hasn't been processed
* Allows the user to change paramaters during the computation, including  
 * The level of redundancy in the computation (duplicate packets)
 * The amount of data sent to each device (number of packets sent)
 * The percentage of reliability required to keep a device in the computation
 * The time-limit before a device is considered inactive
* Manages client registration/deregistration requests
* Sends congratulatory emails to users that have registered with their email address

The Client App
* Processes data in the background while the device is operational
* Requests data to be processed, and returns processed data to the coordinator
* Allows the user to start and stop processing at will
* Stops processing when the battery drops below a certain level
* Restarts processing when the battery is charging/above a certain level
* Respects the user's network preferences
* Enters a dormant state when there is no more data to be processed
* Allows a user to register/unregister their device from the computation
* Allows the user to donate to the project from an external web page
* Allows the user to change/delete their email address in the system


## Implementing the Coordinator
To implement the Coordinator you need access to a MySQL database.  

Before you use the Coordinator you must  
* Extend the AbstractWorkPacketLoader and implement the retrieveInitialWorkPackets() and retrieveAdditionalWorkPackets() methods.  
* Extend the AbstractResultsTransferManager and implement the methods to open and close a connection to your database, convert the results to whatever format you need and write the results to your database.  
* Implement or extend the Validation Strategy you will use to validate results. There are two methods used - one to validate a new results and one to compare duplicate results. You must implement a method to validate a new result, the system will default to using bytewise comparison if you don't override the compareWithSavedResult method. 
* Add these implementations to the retrieval methods in the Implementations class.
* Add the URL, user name and password for your MySQL database into the Implementations class.
* Change the key of the encryption algorithm in the Implementations class.
* If you're using group processing, implement the GroupValidationStrategy and set the group validation setting to true in the Implementations class.

The Implementations class is found in /src/uk/ac/qub/finalproject/server/implementations folder.
See the [implementation examples](https://github.com/Philip-Lawson/Final-Project/blob/master/ImplementationExamples.md) for more information on implementing the classes needed.

Build the project and you're ready to go!

