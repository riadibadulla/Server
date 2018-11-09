import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
	* Responsible for saving logs.
	* @author 180029410
	*
	*/
public class LogSavingDialog {

				private String log;        //log per each connection
				private String fileName;   //file Where the logs are saved

				/**
					* Constructor.
					*
					* @param fileName link to the file name.
					*/
				public LogSavingDialog(String fileName) {
								log = "--------------------------------------\n\n";
								this.fileName = fileName;
				}

				/**
					* Adds new lines to the log.
					*
					* @param line new line to be appended to the log
					*/
				public void addToLog(String line) {
								log += line;
				}

				/**
					* Saves To file.
					* @throws IOException Input Output Exception.
					*/
				public void saveTo() throws IOException {
								try (FileWriter fw = new FileWriter(fileName, true)) {
												fw.write(log);
								} catch (FileNotFoundException e) {
												e.printStackTrace();
								}
				}
}
