package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AnalysisClass {
	
	String mainDir = "simulationDir";
	String pathToConfig = "configFile";
	String pathToDataDir = "data";
	String graphDir = "graph";
	String cdCmd = "cd ";
	String dirCmd ="dir /b";
	
	String resultDir = "simulationResult";
	
	String actualMex = "actualMex.csv";
	String messageSend = "messageSend.csv";
	String numQueries = "numQueriesConcluded.csv";
	String averageQoS = "meanTimeDifference.csv";
	
	
	String commandGetConfingList = cdCmd + "  " + mainDir + " && " + dirCmd;
	String startPoint = "/c";
	String makeCommand="javac -cp \"peersim-lib/*\" "
			+ " GanciSimulazione/DSR_IOT/*.java "
			+ "GanciSimulazione/DSR_IOT/path/*.java "
			+ "GanciSimulazione/DSR_IOT/query/*.java "
			+ "GanciSimulazione/DSR_IOT/nodeInfo/*.java "
			+ "GanciSimulazione/network/*.java "
			+ "GanciSimulazione/utility/*.java && ";
	String simulationCommand = "java -cp \"./GanciSimulazione;peersim-lib/*\" peersim.Simulator ";// GanciSimulazione/config/config.txt";
	
	ArrayList<String> configList = new ArrayList<>();
	ArrayList<String> configFileList = new ArrayList<>();
	
	
	
	
	public static void main(String[] args) {
		AnalysisClass me = new AnalysisClass();	
		me.execute();
	}
	
	
	public void execute() {
		deleteOldResult();
		initDir();
		doAllSimulation();
		doAllAnalisys();
	}
	
	
	public void doAllSimulation() {
		configList = (ArrayList<String>) getArray(executeCmd(commandGetConfingList));
		for(String config: configList) {
			configFileList = (ArrayList<String>) getArray(executeCmd(cdCmd + "" + mainDir + "/" + config +  "/" + pathToConfig +" && " + dirCmd));
			for(String configFile : configFileList) {
				getArray(executeCmd(simulationCommand + "\"" + mainDir + "/" + config +  "/" + pathToConfig +  "/" + configFile +  "\" "),true); 
				}
		}
	}

	public void doAllAnalisys() {
		
		configList = (ArrayList<String>) getArray(executeCmd(commandGetConfingList));
		
		for(String config: configList) {		//Per ogni tipologia di configurazione		
				actualMexAnalysis(config);
				messageSendAnalysis(config);
				QoSAverageAnalysis(config);
				numQueriesAnalysis(config);
		}
	}
	
	
////////////////////////////////////////////////////////////////FILE SYSTEM OPERATION
	
	public void initDir(boolean verbose) {
		boolean success;
		var directory = new File(resultDir);
		if (directory.exists()) {
			if(verbose) System.out.println("Directory already exists");

		} else {
			if(verbose) System.out.println("Directory not exists, creating now");

			success = directory.mkdirs();
			if (success) {
				if(verbose) System.out.printf("Successfully created new directory : %s%n", resultDir);
			} else {
				if(verbose) System.out.printf("Failed to create new directory: %s%n", resultDir);
			}
		}

	}
	
	public void initDir() {
		initDir(false);
	}
	
	public void deleteOldResult() {
		String mainCmd = "cd " + resultDir +" && del /q *.* ";
		getArray(executeCmd(mainCmd),true);	
	}
	
///////////////////////////////////////////////////////////////////ANALYSIS
	
	public void doAnalysis(String pathToFile, String config, String fileName) throws IOException  {
		double[][] matrix = readMatrixData(pathToFile);
		ValueCalculator[] result = ValueCalculator.analizeMatrixData(matrix);
		writeAnalysis(fileName,config,result);
	}


	public void actualMexAnalysis(String config) {
		try {
			doAnalysis(mainDir+"/"+config+"/"+pathToDataDir+"/" + actualMex, config, actualMex);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE nell'analisi dei dati del file :" + actualMex + " della configurazione " + config);
		}
	}
	
	public void messageSendAnalysis(String config) {
		try {
			doAnalysis(mainDir+"/"+config+"/"+pathToDataDir+"/" + messageSend, config, messageSend);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE nell'analisi dei dati del file :" + messageSend + " della configurazione " + config);
		}
	}
	
	public void numQueriesAnalysis(String config) {
		try {
			doAnalysis(mainDir+"/"+config+"/"+pathToDataDir+"/" + numQueries, config, numQueries);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE nell'analisi dei dati del file :" + numQueries + " della configurazione " + config);
		}
	}
	
	public void QoSAverageAnalysis(String config){
		try {
			doAnalysis(mainDir+"/"+config+"/"+pathToDataDir+"/" + averageQoS, config, averageQoS);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("ERRORE nell'analisi dei dati del file :" + averageQoS + " della configurazione " + config);
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////WRITE AND READ
	
	
	public double[][] readMatrixData(String pathToFile) throws IOException {
		double[][] matrix;
		
		var csvReader = new BufferedReader(new FileReader(pathToFile));
		
		String row;
		ArrayList<String[]> datas = new ArrayList<>();
		
		while ((row = csvReader.readLine()) != null) {
			row = row.replaceAll(",",".");
		    String[] data = row.split(";");
		    datas.add(data);
		    
		}
		csvReader.close();
	
		int rowNum = datas.size();
		int rowSize = datas.get(0).length;
		matrix = new double[rowNum][rowSize];
		
		for(int i = 0; i<rowNum;i++) {
			for(int j = 0; j < rowSize;j++) {
				matrix[i][j] = Double.parseDouble(datas.get(i)[j]);
			}

		}
	
		
		return matrix;
		
	}
	
	
	
	public void writeAnalysis(String fileName, String config, ValueCalculator[] result) {
		String where = resultDir + "/" + fileName;
		
		var strMean = new StringBuilder();
		var strS = new StringBuilder();
		var strV = new StringBuilder();
		var strInt = new StringBuilder();
		
		strMean.append(config + ";");
		strS.append("S;");
		strV.append("V;");
		strInt.append("Intervallo;");
		
		String temp;
		for(ValueCalculator v: result) {
			temp = "%.7f;";
			
			strMean.append(String.format(temp,v.mean));
			strS.append(String.format(temp,v.s));
			strV.append(String.format(temp,v.V));
			strInt.append(v.getInterval() + ";");
		}
		
		String text = strMean + "\n" + strS + "\n"+ strV + "\n" + strInt +"\n";
		writeAction(where,text);
	}
	
	public void writeAction(String where, String text) {
		FileWriter csvWriter;

		try {
			csvWriter = new FileWriter(where,true);
			csvWriter.append(text);
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getArray(BufferedReader r, boolean verbose) {
		String line;
		ArrayList<String> ret = new ArrayList<>();
		while (true) {
			try {
				line = r.readLine();
				if (line == null) {
					break;
				}
				ret.add(line);
				if(verbose) System.out.println(line);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		if(verbose) System.out.println("___________________________________________");
		return ret;
	}
	
	public List<String> getArray(BufferedReader r) {
		return getArray(r,false);
	}
	
	/////////////////////////////////////////////////////////COMMAND_LINE
	
	public BufferedReader executeCmd(String cmd) {
		ProcessBuilder builder = new ProcessBuilder("cmd.exe","/C",cmd);
		
		builder.redirectErrorStream(true);
		Process p;
		try {
			p = builder.start();
			return new BufferedReader(new InputStreamReader(p.getInputStream()));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}

	/////////////////////////////////////////////////////////////PRINT
	public void printMatrix(double[][] matrix) {
		for(int i = 0; i<matrix.length;i++) {
			for(int j = 0; j < matrix[i].length;j++) {
				System.out.print(matrix[i][j]); 
				System.out.print(";"); 
			}
			System.out.print("\n"); 

		}
	}
	
	
	
	
	/////////////////////////////////////////NON USATE
	
	
	/*
	public void simpleAverageAnalysis(String pathToFile, String config) throws IOException {
		double[] res = makeAverage(pathToFile);
		String where = resultDir + "/" + actualMex;
		String text = config +";" + fromArrayToString(res)+"\n";
		
		writeAction(where,text);
	}
	
	public String fromArrayToString(String[] array) {
		StringBuffer ret = new StringBuffer();
		for(String str: array) {
			ret.append(str+";");
		}
		return ret.toString();
	}
	
	public String fromArrayToString(double[] array) {
		StringBuffer ret = new StringBuffer();
		for(Double dbl: array) {
			ret.append(dbl+";");
		}
		return ret.toString();
	}
	
	public void deleteOldResult2() {
		String mainCmd = "cd " + resultDir +" && del ";
		
		ArrayList<String> resultList = getArray(executeCmd(cdCmd + "" + resultDir + " && " + dirCmd));
		for(String resultFile: resultList) {
			getArray(executeCmd(mainCmd + resultFile));		
		}	
	}
	
		
	public double[] makeAverage(String pathToFile) throws IOException {
	
		BufferedReader csvReader = new BufferedReader(new FileReader(pathToFile));
		String row;
		ArrayList<String[]> datas = new ArrayList<>();
		
		while ((row = csvReader.readLine()) != null) {
		    String[] data = row.split(";");
		    datas.add(data);
		    // do something with the data
		}
		csvReader.close();
		
		int rowNum = datas.size();
		int rowSize = datas.get(0).length;
		double res[] = new double[rowSize];
		
		for(int i = 0; i<rowSize;i++) {
			double total = 0;
			for(String[] dataRow: datas) {
				total += Long.parseLong(dataRow[i]);
			}
			res[i] = total/rowNum;
			//System.out.print("total : " + total + "\trowSize:" + rowSize +  "\n");
			//System.out.print(res[i]+";");
		}
		return res;
	}
	
	*/
}
