package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class WriteClass {
	
	static final String RESULTDIR = "simulationDir";
	static final String CONCLUEDQUERY = "numQueriesConcluded.csv";
	static final String MEANTIMEDIFFERENCE = "meanTimeDifference.csv";
	static final String MESSAGESEND = "messageSend.csv";
	static final String ACTUALMESSAGE = "actualMex.csv";
	static final String DATADIR = "data";
	
	String dataLocation;
	
	
	public WriteClass(String configName) {
		dataLocation = RESULTDIR + "/" + configName + "/" + DATADIR ;
		initWrite();
	}
	

	public void initWrite() {
		initDir();
		initFile(CONCLUEDQUERY);
		initFile(MEANTIMEDIFFERENCE);
		initFile(MESSAGESEND);
		initFile(ACTUALMESSAGE);
	}
	
	public void writeCsv(String fileName, String text) {
		FileWriter csvWriter;

		try {
			csvWriter = new FileWriter(dataLocation + "/" + fileName, true);
			csvWriter.append(text);
			csvWriter.append("\n");
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void writeActualMex(String text) {
		writeCsv(ACTUALMESSAGE, text);
	}

	public void writeConcludedQuery(String text) {
		writeCsv(CONCLUEDQUERY,text);
	}
	public void writeMeanTimeDifference(String text) {
		writeCsv(MEANTIMEDIFFERENCE, text);
	}

	public void writeMessageSend(String text) {
		writeCsv(MESSAGESEND, text);
	}

	public void initDir() {
		boolean success;
		File directory = new File(dataLocation);
		if (directory.exists()) {
			System.out.println("Directory already exists");

		} else {
			System.out.println("Directory not exists, creating now");

			success = directory.mkdirs();
			if (success) {
				System.out.printf("Successfully created new directory : %s%n", dataLocation);
			} else {
				System.out.printf("Failed to create new directory: %s%n", dataLocation);
			}
		}

	}

	public void initFile(String fileName) {

		File f = new File(dataLocation + "/" + fileName);
		if (f.exists()) {
			System.out.println("File already exists");

		} else {
			System.out.println("No such file exists, creating now");
			try {
				boolean success = f.createNewFile();
				if (success) {
					System.out.printf("Successfully created new file: %s%n", f);
				} else {
					System.out.printf("Failed to create new file: %s%n", f);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


}
