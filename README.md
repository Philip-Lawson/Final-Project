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
