# draw_whiteboard
distributed system project
Shared Whiteboard Report
Zhuoya ZHOU 
1.System Architecture
I chose centralized server and client-server architecture.
1.1 Overview
All users (including manager) connect to a central server. The server is responsible for managing all user connections and synchronizing the whiteboard content. It controls client access by managing a user list. And Clients connect to the server to retrieve and update whiteboard content.
1.2 Reasons for Choosing It 
I chose a centralized client-server design over a peer-to-peer architecture because of the huge performance and stability benefits. Firstly, the centralized design uses high-speed server for computing and data processing, which eliminates the instability and performance difficulties associated with individual user PCs. Furthermore, the centralized design allows for cluster deployment and load balancing, which increases system processing capacity by adding server nodes, eliminates single points of failure, and improves concurrent processing and fault tolerance. Centralized administration improves user access control, resource allocation, and system maintenance while assuring system security and stability. However, P2P design has significant drawbacks in terms of performance, stability, scalability, and management. And other architectures were too difficult for this project, but centralized server and peer-to-peer were sufficient, thus I used centralized server.
2.Communication Protocols and Message Formats
2.1 RMI
RMI simplifies the implementation of remote method calls by providing a high-level abstraction that allows clients to call methods on distant objects as if they were local. RMI automatically manages object serialization and deserialization, masking the underlying communication intricacies, making remote service creation easier and more efficient. RMI is also built on Java's robust type system, which allows for type-safe remote calls and reduces errors caused by type mismatches. For a shared whiteboard system, RMI can successfully handle user connections and whiteboard state synchronization, guaranteeing that all users share the same whiteboard material in real time while also enabling efficient user administration and control.
2.2 TCP Socket
Using TCP Socket is because it provides a dependable, connection-oriented communication channel that ensures data integrity and order. It can achieve the goal of real-time drawing and conversation functionalities on a shared whiteboard system. Furthermore, TCP Socket provides fine-grained control over data transmission and receiving, allowing for fast data synchronization and real-time interaction, making it fit for application that requires frequent updates and low latency answers.
2.3 JsonObject
Using the JSON format in the shared whiteboard system makes it easier to transmit drawing instructions and states of users between the client and server. 
3.Design diagrams (class and interaction)

Interaction diagram
Manager create the whiteboard:



User join the whiteboard:


Manager/User operates the whiteboard:
Choose the tool and draw whiteboard


User leaves





Manager leaves




Manager operates operation in file menu


Save and saveAs are also realized.


Manager kickout the user 




3.Implementation details (by features) 
Centralized Client-Server Architecture
4.1.1. Server Implementation
4.1.1.1 Server Initialization
Server Class (ServerApplication):
Starts the server and listens for client connections.
Uses RMI (Remote Method Invocation) to register a remote user service (IRemoteUserService).
For each client connection, it starts a new thread (ClientRequestThread) to handle requests.
4.1.1.2 Handling Client Requests
Client Request Thread Class (ClientRequestThread):
Handles requests from clients, such as creating the whiteboard, drawing operations, etc.
Parses incoming JSON messages and performs appropriate actions based on the request type.
Broadcasts drawing operations and other updates to all connected clients to ensure real-time synchronization.
4.1.2. Client Implementation
4.1.2.1 Creating a Whiteboard
Create Whiteboard Application Class (CreateFrameApplication):
Launched by the manager to create and initialize the whiteboard.
Parses command-line arguments to get the server address, port number, and username.
Starts a new thread (ManagerThread) to connect to the server and sets up the remote user service and socket connection.
Displays the whiteboard interface and starts listening for requests from the server.
4.1.2.2 Joining a Whiteboard
Join Whiteboard Application Class (JoinFrameApplication):
Connects to an existing whiteboard session to receive updates and display them locally.
Parses command-line arguments to get the server address, port number, and username.
Starts a new thread (UserThread) to connect to the server and sets up the remote user service and socket connection.
Waits for manager approval to join the session and displays the whiteboard interface.
Continuously listens for requests from the server, such as drawing updates and notifications.
4.1.3. Network Communication and Synchronization
Client Connection Utilities Class (ClientConnectionUtils):
Manages the network connection and communication between the client and server.
Sends drawing data to the server and receives drawing data updates from the server.
Ensures real-time synchronization of the whiteboard content across all clients using synchronized keyword in every according operate method.

Basic Feature1: Multiple users share the whiteboard:

1.ServerApplication.java:
The main method starts the server, creates a ServerSocket, and waits for client connections.
A main thread is created to listen for incoming client connections. Whenever a new client connects, a new ClientRequestThread object is created to handle communication with that client.
2.ClientRequestThread.java:
In the run method, it continuously receives messages from the client and parses the content.
Depending on the message type (such as drawing instructions, chat messages, etc.) of the sent request, it forwards the message to a UserThread and ManagerThread associated with the user.
Uses ConnectionSocket to manage client connections and data transmission. This can make sure ClientRequestThread and UserThread, ManagerThread connected successfully and forward successfully.
3.DrawPanel.java:
Manages the drawing logic on the board, including tools like brush, eraser, shapes, etc using draw().
Whenever a user performs an action on the drawing board, it generates the corresponding drawing request and sends them to the server.
Receives broadcasted drawing request from the server and replicates the actions on the local drawing board.
4.ConnectionSocket.java:
Encapsulates socket communication for client connections, providing methods for sending and receiving messages.
In the send(), it sends messages to the connected clients.
In the receive(), it receives messages from the clients.

Basic Feature2: GUI contains lines, circles, ellipses, and rectangles:

1.DrawPanel.java:
The DrawPanel class manages the drawing logic on the board, including handling different shapes like lines, circles, ovals, and rectangles.
It has a method like draw() to handle the drawing of shapes. The idea of draw a line is to mousePressed(MouseEvent e) to monitor the starting position coordinate, mouseReleased(MouseEvent e) to monitor the end coordinate, and then connect to a line. The same is true for other graphs, and draw a graph through some calculation.
2.AbstractTypeButton.java:
This is a template class for different drawing tools.
It defines common properties and methods for all types of drawing buttons, such as selecting and deselecting tools.
3.LineTypeButton.java (and similar classes for other shapes):
Implements specific drawing tools by extending AbstractTypeButton and set their icons or other attributes when initializing.
Each shape tool (like LineTypeButton, CircleTypeButton, OvalTypeButton, RectangleTypeButton) implements the logic for drawing the respective shape.
It includes methods to set and get the current drawing tool in DrawPanel.
4.DrawPosition.java:
Represents drawing positions and shapes.
Stores the start and end coordinates, shape type, and other properties required to render the shape on the board.
This data is used by DrawPanel to draw the shapes.
Example Workflow:
1.Tool Selection:
When a user selects a shape tool (e.g., line, circle), an instance of the corresponding TypeButton class (e.g., LineTypeButton, CircleTypeButton) sets the current drawing tool in DrawPanel.
2.Drawing Shapes:
When the user starts drawing (e.g., pressing the mouse), DrawPanel captures the initial coordinates.
As the user drags the mouse, DrawPanel dynamically updates the shape's outline based on the current mouse position.
Upon releasing the mouse, DrawPanel finalizes the shape's coordinates and updates the board.
3.Shape Rendering:
DrawPanel uses the captured coordinates and shape type to render the shape using the appropriate method in draw().
The shape's data is stored in a DrawPosition object to ensure it can be redrawn if needed (e.g., when synchronizing with other users).

Basic Feature3: Advanced operation of whiteboard:
1.Text Input (TextTypeButton.java):
Implement a method to display a text box at the clicked position.
Capture the entered text and render it at the specified position on the board.
Store the text and its coordinates for redrawing.
Drag in mouseDragged(MouseEvent e) to change the coordinates of the text.
2.Color Selection (ColorPalette.java):
Create a color palette UI with 16 color options.
Implement a method to handle color selection and set the current drawing color.
Apply the selected color to all drawing operations.
3.Freehand Drawing (BrushTypeButton.java):
mousePressed(MouseEvent e) listens to record the position coordinates of each point, uses a vector to store the path, and draws the image.
4.Eraser with Different Sizes (EraserTypeButton.java):
Similar to Brush, except that the brush color is white and the brush thickness is set.

Basic Feature4: User leaves:
1. DrawFrameApplication.java: 
•closeFrame Method: 
oThis method handles the logic when listening the window is closed. 
oIf the user is not being kicked out, it calls clientConnectionUtils.disconnect(isManager, traceId) to initiate the disconnection process. 
oStops the frame and disposes of the window. 
oExits the application. 
2. ClientConnectionUtils.java:
disconnect Method:
oThis method handles the logic for disconnecting a user.
oIt creates and sends a disconnect request (JSONObject) to the ClientRequestThread.
oCalls internal methods to handle the disconnection process.
3. ClientRequestThread.java:
Handling Disconnect Request:
oParses the incoming JSONObject disconnect request.
oRetrieves the user ID (traceId) from the parsed result.
oCalls UserManagerUtils.removeUser(String traceId) to handle the removal of the user.
4. UserManagerUtils.java:
removeUser Method:
oThis method removes the user with the given traceId from the active user list.
6. DrawFrameApplication.java:
Update the user list for other users:
oupdateUserListThread: Updates the userlist of the frame for other users when remote server has changed.  
7. UserListApplication.java:
Handling User Close:
oUpdates the UI of server to reflect the user has closed the whiteboard.

Basic Feature5: Manager leaves:
2. ClientConnectionUtils.java:
disconnect(isManager, traceId):
oConstructs a JSONObject with the close request details, including the action type and the manager's traceId.
oSends the JSONObject to the ClientRequestThread for processing.
oManages the actual disconnection from the server and other clients.
3. ClientRequestThread.java:
Handling Manager Close Request:
oListens for incoming JSONObjects.
oWhen a close request is received, it parses the traceId from the JSONObject.
oCalls UserManagerUtils.removeManager(traceId) to handle the manager removal.
oBroadcasts the manager's close operation using UserManagerUtils.broadcastManagerOperate().
oClears all user data using UserManagerUtils.clear.
4. UserManagerUtils.java:
removeManager(String traceId):
oRemoves the manager identified by traceId from the active user list.
broadcastManagerOperate(String operation):
oBroadcasts the specified operation (in this case, manager close) to the userThread for all connected users.
clear():
oClears all user data and state related to the whiteboard session.
5. userThread.java:
The received request is "MANAGER_CLOSE_WINDOW", calling the closeByManager method
6. DrawFrameApplication.java:
closeByManager Method:
oHandles the logic when the manager closes the whiteboard. 
oA dialog box is displayed to remind the user that the manager is closed.
oUpdates stops the frame, sets isKickedOut to true, and calls ClientConnectionUtils.disconnect.
 7. UserListApplication.java:
Handling Manager Close:
oUpdates the UI of server to reflect the manager has closed the whiteboard.





Advanced Feature1: Only manager can operate the file menu

1.User Role Distinction:
ServerApplication.java: Manages client connections. There is no explicit role assignment in this class, but it manages overall connections.
2.Role Passing in CreateFrameApplication and JoinFrameApplication:
CreateFrameApplication.java: When a manager creates a new whiteboard, it passes true to the DrawFrameApplication to indicate the user is a manager.
JoinFrameApplication.java: When a regular user joins an existing whiteboard, it passes false to the DrawFrameApplication to indicate the user is not a manager.
3.Manager-Specific Operations:
isManager() Method in DrawFrameApplication.java:
This method checks if the current user is a manager. It returns true if the user has administrator privileges, as indicated by the parameter passed during the creation or joining of the whiteboard.
fileMenu Component in DrawPanel.java:
This component includes various menu items that trigger different actions.
DrawPanel checks the result of the isManager() method and decides whether to display the fileMenu.
If isManager() returns true, fileMenu is displayed, allowing the user to create a new whiteboard and perform other administrative actions.
If isManager() returns false, fileMenu is not displayed, thus preventing regular users from accessing administrative functions.
Operation after event listening:
“New”: Remove all elements from the drawTrack vector, which stores the drawing positions and actions. Call syncDrawBoard() to update the state across all connected clients.
“Save”: Display a JFileChooser dialog for the user to specify the save location and file name. If a location is chosen, get the file path.
Use FileOutputStream and ObjectOutputStream to serialize and save the drawing track to the specified file. Handle exceptions gracefully and print error messages if necessary.
“Open”: Display a JFileChooser dialog for the user to select a file to open. If a file is selected, read the file path. Use FileInputStream and ObjectInputStream to read and deserialize the saved drawing track from the file. Handle exceptions gracefully and print error messages if necessary. Call syncDrawBoard() to update the state across all connected clients.
“SaveAs”: Display a JFileChooser dialog for the user to specify the save location and file name. If a location is chosen, get the file path. Use FileOutputStream and ObjectOutputStream to serialize and save the drawing track to the specified file. Handle exceptions gracefully and print error messages if necessary.
“Exit”: Call clientConnectionUtils.disconnect(true, traceId) to disconnect from the server. Remove the user from the server's user list and notify all other clients about the disconnection. Call System.exit(0) to terminate the application.

Advanced Feature2: Allows the administrator to kick the user
1.DrawFrameApplication.java:
Displaying Kickout UI:
Check if the user is the manager using isManager.
If the user is the manager, set the title to indicate they are the manager and display the kickout panel.
Add UI components for kicking out users:
A panel (kickPanel) with a vertical layout.
A label (kickOutLabel) to indicate the kickout functionality.
A text field (kickOutTextField) for entering the user ID to be kicked out.
A button (kickOutButton) that triggers the kickout action.
Kickout Button Action:
When the kickout button is pressed, retrieve the entered user ID from kickOutTextField.
Check if the entered user ID is the manager's own ID by getManagerUsername():
If yes, display an information dialog indicating that the manager cannot kick themselves out using DialogUtils.showInfoDialog().
If no, call clientConnectionUtils.kickOut() with the entered user ID.
Handle remote exceptions by displaying a no server connection dialog.
2.ClientConnectionUtils.java:
kickOut():
This method takes the user ID of the user to be kicked out as a parameter.
It sends a request to the server to kick out the specified user.
Handles the necessary network communication and error handling.
3.IRemoteUserService.java:
getManagerUsername Method:
This method returns the user ID of the manager.
Used to check if the entered user ID is the manager's own ID.
4.DialogUtils.java:
showInfoDialog():
Displays an information dialog with a specified message.
showNoServerConnectionDialog():
Displays a dialog indicating that there is no connection to the server.
5.ClientRequestThread.java:
When receiving a kickout request, retrieve the user ID to be kicked out from the request.
Use userManagerUtils to get the connection socket of the user to be kicked out.
Send a kickout request to the user's connection socket ManagerThread and UserThread.
Remove the user from userManagerUtils.
6.UserThread/ManagerThread.java:
When receiving a kickout notification, call drawFrameApplication.kickedOut() to handle the kickout logic on the client's side. That is, refresh the user list on the screen.

5. New innovations
 advanced features (Implementation details are in point 4)
1. file" button, new, open, save, saveAs, close(can only be used by administrators)
2. Allow the administrator to kick the user
