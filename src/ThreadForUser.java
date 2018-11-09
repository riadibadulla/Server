import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
	* Class runs the thread for requests.
	* @author 180029410
	*/
public class ThreadForUser extends Thread {
				private Socket soc = null;               //client socket
				private boolean isAlive = false;         //if Thread is alive
				private static int numberOfUsers = 0;    //number of running threads
				private String directory;                //directory where all files are stored
				private String requestFile;              //file which is requested by the client
				private InputStream inputStream;         // get data from client on this input stream
				private OutputStream outputStream;       // can send data back to the client on this output stream
				private BufferedReader input;            // use buffered reader to read client data (request data)
				private PrintStream printStream;         // print stream back to the client
				private Boolean get = false;             // true if the request is get
				private Boolean head = false;            // true if the request is head
				private LogSavingDialog logSavingDialog; //Saving dialog, class which saves logs

				/**
					* Constructor.
					*
					* @param soc       client socket.
					* @param directory directory with files
					* @param logSavingDialog object which is responsible for saving logs.
					*/
				public ThreadForUser(Socket soc, String directory, LogSavingDialog logSavingDialog) {
								numberOfUsers++;                            //increment number of users

								this.soc = soc;
								this.directory = directory;
								this.logSavingDialog = logSavingDialog;

								try {
												inputStream = soc.getInputStream();     // get data from client on this input stream
												outputStream = soc.getOutputStream();  // to send data back to the client on this stream
												input = new BufferedReader(new InputStreamReader(inputStream)); // use buffered reader to read client datadirectory

								} catch (IOException ioe) {
												System.out.println("ConnectionHandler: " + ioe.getMessage());
								}
				}

				/**
					* Getter method for number of users.
					*
					* @return number of all threads running now
					*/
				public static int getNumberOfUser() {
								return numberOfUsers;
				}

				/**
					* Runs the thread.
					*/
				public void run() {
								isAlive = true;
								try {
												getRequest();

												printStream = new PrintStream(outputStream);
												if (requestFile.contains("jpg")) {
																respond("image/jpeg");
												} else if (requestFile.contains("png")) {
																respond("image/png");
												} else if (requestFile.contains("gif")) {
																respond("image/gif");
												} else if (requestFile.contains("html")) {
																respond("text/html");
												}

												//Save all logs fo this request
												logSavingDialog.saveTo();
								} catch (Exception e) {
								}

								//The thread is going to be stopped, number of users decremented
								numberOfUsers--;
				}

				/**
					* Get request from the client.
					*
					* @throws Exception Exception.
					*/
				public void getRequest() throws Exception {
								String line;                                      //lines of the request
								boolean initialIteration = true;                  //if the line is the first line of the request

								while ((line = input.readLine()).length() > 0) {
												System.out.println("[" + line + "]");
												logSavingDialog.addToLog(line + "\n");

												if (initialIteration) {
																initialIteration = false;
																if (line.contains("GET")) { //If is get request
																				get = true;
																				head = false;
																} else if (line.contains("HEAD")) { //if it is the head request
																				get = false;
																				head = true;
																}

																int firstSlash = line.indexOf("/");
																requestFile = line.substring(firstSlash + 1, line.length() - " HTTP/1.1".length());
																//System.out.println(requestFile);
																System.out.println("Requested File" + "[" + directory + requestFile + "]");
												}
								}
				}

				/**
					* Responds the client.
					*
					* @param contentType type of the content in response.
					* @throws Exception Exception.
					*/
				public void respond(String contentType) throws Exception {

								logSavingDialog.addToLog("\nResponse: Header ");

								//If the request is nether get nor head
								if (!get && !head) {
												printStream.print("HTTP/1.1 501 Not Implemented\r\n");
												cleanUp();
								} else {

												//Initialise the file
												File file = new File(directory + requestFile);

												//If the file exists
												if (file.exists()) {

																int numberOfBytes = (int) file.length();
																FileInputStream inFile = new FileInputStream(directory + requestFile);
																byte[] fileInBytes = new byte[numberOfBytes];
																inFile.read(fileInBytes);

																printStream.print("HTTP/1.1 200 OK\r\n");
																printStream.print("Content-Type: " + contentType + "\r\n");
																printStream.print("Content-Length: " + numberOfBytes + "\r\n");
																printStream.print("\r\n");

																logSavingDialog.addToLog("HTTP/1.1 200 OK\n");
																logSavingDialog.addToLog("Content-Type: " + contentType + "\n");
																logSavingDialog.addToLog("Content-Length: " + numberOfBytes + "\n");
																logSavingDialog.addToLog("\n\n\n\n\n--------------------------------------\n");

																/* If the response was Get, return body as well*/
																if (get) {
																				printStream.write(fileInBytes, 0, numberOfBytes);
																}
																cleanUp();
												} else {

																//If the file does not exist, 404 NOT FOUND
																printStream.print("HTTP/1.1 404 Not Found\r\n");
																printStream.print("Content-Type: text/html\r\n");
																printStream.print("Content-Length: 128\r\n");
																printStream.print("\r\n");

																logSavingDialog.addToLog("HTTP/1.1 404 Not Found\r\n");
																logSavingDialog.addToLog("Content-Type: text/html\r\n");
																logSavingDialog.addToLog("Content-Length: 128\r\n");
																logSavingDialog.addToLog("\n\n\n\n\n--------------------------------------\n");

																/* If the response was Get, return body as well, which prints 404 NOT FOUND*/
																if (get) {
																				printStream.print("<html><body><center><h1>Oooops, 404 Not found</h1></center></body></html>");
																}
																cleanUp();
												}
								}
				}

				/**
					* Cleans up the printStream and closes the connection.
					*/
				public void cleanUp() {
								printStream.flush();
								printStream.close();
				}

}
