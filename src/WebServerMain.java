import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
	* Main Server class.
	*
	* @author 180029410
	*/
public class WebServerMain {

				private LogSavingDialog logSavingDialog;
				private final int MAXIMUM_NUMBER_OF_USERS = 50;
				private final int DEFAULT_PORT = 12345;

				/**
					* main method, creates an instance of WebServerMain.
					*
					* @param args [0] for the directory, [1] for the port number.
					*/
				public static void main(String[] args) {
								new WebServerMain(args);
				}

				/**
					* Runs a server, or gives an error message.
					*
					* @param args [0] for the directory, [1] for the port number.
					*/
				public WebServerMain(String[] args) {
								if (args.length == 0) {
												System.out.println("Usage: java WebServerMain <document_root> <port>");
												System.exit(1);
								}
								runAServer(args[0], args[1]);
				}

				/**
					* Get a date in user friendly format.
					*
					* @return current date
					*/
				public static String getDate() {
								DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								Calendar cal = Calendar.getInstance();
								return dateFormat.format(cal.getTime());
				}

				/**
					* Runs a server.
					*
					* @param directory Directory name.
					* @param port Port Number.
					*/
				public void runAServer(String directory, String port) {
								int socketPort = DEFAULT_PORT; //default port number

								//if entered Port is the number
								try {
												socketPort = Integer.parseInt(port);
								} catch (NumberFormatException e) {
												System.out.print("Please enter the number for Port");
												System.exit(1);
								}

								ServerSocket ss = null;
								try {
												ss = new ServerSocket(socketPort);
								} catch (IOException e) {
												System.out.println("Error finding port");
												System.exit(1);
								}

								/* Infinite loop, wait for requests*/
								while (true) {
												Socket soc = null;

												//Save logs in /log.txt file of the directory
												logSavingDialog = new LogSavingDialog(directory + "/log.txt");

												try {
																/*
																	* *****************************************************************
																	* Program will stop here until we get a connection
																	*/
																soc = ss.accept(); // Once read -> start up a thread
																/*
																	* ****************************************************************
																	*/

																System.out.println("\n\n" + getDate() + "  Connection accepted at :" + soc);
																logSavingDialog.addToLog(getDate() + "  Connection accepted at :" + soc);

												} catch (IOException ioe) {
																System.out.println("Server failed to accept");
																System.exit(1);
												}

												//Do not let the user make a request if the number of users is 50 or more
												if (ThreadForUser.getNumberOfUser() < MAXIMUM_NUMBER_OF_USERS) {
																ThreadForUser threadForUser = new ThreadForUser(soc, directory, logSavingDialog);
																threadForUser.start();
												}
								}
				}
}
