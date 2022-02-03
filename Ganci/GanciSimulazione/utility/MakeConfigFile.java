package utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

//Classe che si occupa di creare i file di configurazione necessari a peerSim
//non ancora implementato il metodo che prende i dati da un possibile file di testo
//i valori vanno cambiati manualmente e la classe nuovamente eseguita
public class MakeConfigFile {

	static long seeds[];
	static int seedNum = 30;
	
	private int cycles = 61;
	long netSize = 6000;
	
	
	double connect = 0.0025;
	int inactiveNode = 0;
	
	
	boolean cache = true;
	boolean liveOpt = true;
	boolean readOnlyOneTime = true;
	

	int timeToLive = 500;  		
	int queryToAdd = 5;
	int addCycle = 5;
	int maxWait = 5;				
	
	int paramNum=300;				
	
	int addNodeNum = 0;			
	int removeNodeNum = 0;			
	
	////////////////////////////////////NON NECESSARIO MODIFICARE
	
	
	boolean verbose = false;
	int maxCacheSize = 50;
	int maxCachePath = 5;
	boolean mergePath = false;
	boolean sendToSender = false;
	boolean addSingleBestPathToCache = false;
	boolean maxReturnedCachePath = false;
	boolean singleParam = true;
	int changeNodeNum = 0;
	
	
	
	String selectMode = "executionTime";
	char selectModeChar;
	char cacheChar;
	char liveOptChar;
	char mergePathChar;
	char addSingleBestPathToCacheChar;
	char readOnlyOneTimeChar;
	char maxReturnedCachePathChar;
	char singleParamChar; 
	char sendToSenderChar; 
	
	String configDir ;
	String mainDir = "simulationDir";
	String configFileDir = "configFile";
	String dataDir = "data";
	
	String modeExecTime = "executionTime";
	String modeCost = "cost";
	String modeAvaiability = "avaiability";
	String modeThroughput = "throughput";
	
	
	boolean deleteOld = true;


	String baseText = "random.seed %d\r\n"
			+ "\r\n"
			+ "simulation.cycles %d\r\n"
			+ "\r\n"
			+ "control.shf Shuffle\r\n"
			+ "\r\n"
			+ "network.size %d\r\n"
			+ "\r\n"
			+ " \r\n"
			+ "protocol.lnk IdleProtocol\r\n"
			+ "\r\n"
			+ "protocol.NetworkProtocol NetworkProtocol\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "#________________________________Network\r\n"
			+ "init.0 InitNetwork\r\n"
			+ "init.0.protocol NetworkProtocol\r\n"
			+ "\r\n"
			+ "init.1 MyWireTopology\r\n"
			+ "init.1.protocol lnk\r\n"
			+ "init.1.coord_protocol NetworkProtocol\r\n"
			+ "init.1.connectivity %1.12f\r\n"
			+ "init.1.inactiveNode %d\r\n"
			+ "\r\n"
			+ "#________________________________MyProtocol\r\n"
			+ "protocol.MyProtocol MyProtocol\r\n"
			+ "protocol.MyProtocol.linkable lnk\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "protocol.MyProtocol.verbose %b	\r\n"
			+ "protocol.MyProtocol.cache %b		\r\n"
			+ "protocol.MyProtocol.liveOpt %b		\r\n"
			+ "protocol.MyProtocol.readOnlyOneTime %b				\r\n"
			+ "protocol.MyProtocol.maxCacheSize %d						\r\n"
			+ "protocol.MyProtocol.timeToLive %d						\r\n"
			+ "protocol.MyProtocol.queryToAdd %d						\r\n"
			+ "protocol.MyProtocol.addCycle %d							\r\n"
			+ "protocol.MyProtocol.selectMode executionTime			\r\n"
			+ "protocol.MyProtocol.maxWait %d	\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "protocol.MyProtocol.mergePath %b						\r\n"
			+ "protocol.MyProtocol.sendToSender %b					\r\n"
			+ "protocol.MyProtocol.addSinglePathCache %b			\r\n"
			+ "protocol.MyProtocol.maxReturnedCachePath %b			\r\n"
			+ "protocol.MyProtocol.maxCachePath %d						\r\n"
			+ "\r\n"
			+ "#________________________________MyNodeInitializer\r\n"
			+ "init.2 MyNodeInitializer\r\n"
			+ "init.2.protocol MyProtocol\r\n"
			+ "init.2.paramNum %d   							\r\n"
			+ "init.2.singleParam %b\r\n"
			+ "\r\n"
			
			+"control.dnet DynamicNetwork\r\n"
			+ "control.dnet.add %d\r\n"
			+ "\r\n"
			+ "control.dnet.init.0 MyNodeInitializer\r\n"
			+ "control.dnet.init.0.protocol MyProtocol\r\n"
			+ "control.dnet.init.0.paramNum 0 \r\n"
			+ "control.dnet.init.0.singleParam true\r\n"
			+ "control.dnet.init.0.initLink true\r\n"
			+ "#________________________________MyObserver\r\n"
			+ "control.avgo MyObserver\r\n"
			+ "control.avgo.protocol MyProtocol\r\n"
			+ "control.avgo.addNodeNum 0\r\n"
			+ "control.avgo.removeNodeNum %d\r\n"
			+ "control.avgo.changeNodeNum %d\r\n"
			+ "\r\n";
			
	
	public static void main(String[] args) {
		
		if (args.length!=1) {
			return;
		}
		
		long mainSeed = Long.parseLong(args[0]);
		
		MakeConfigFile me = new MakeConfigFile();
		
		me.init();
	
		seeds = me.initSeed(mainSeed, seedNum);
		
		int i = 1;
		for(long seed : seeds) {
			me.makeFile(seed,i);
			i++;
			System.out.println(seed);
		}
	}
	
	public long[] initSeed(long mainSeed, int size){ //Aspettta 10.000 generazioni di numeri random, e ne prende 'size'
		long temp[] = new long[size];
		Random myGen = new Random(mainSeed);
		for(int i = 0; i <size;i++) {
			long newSeed = 0;
			for(int j = 0; j <10.000;j++) {
				newSeed = myGen.nextLong();
				while(newSeed<0) {
					newSeed = myGen.nextLong();
				}
				
			}
			temp[i]=newSeed;
		}
		
		
		return temp;
	}
	
	public void init() {
		cacheChar = cache ? 'C' : 'N';
		liveOptChar = liveOpt ? 'O' : 'N';
		mergePathChar = mergePath ? 'M' : 'N';
		singleParamChar = singleParam ? 'S' : 'M';
		sendToSenderChar = sendToSender ? 'S' : 'N';
		selectModeChar = setSelectModeChar(selectMode);
		
		addSingleBestPathToCacheChar = addSingleBestPathToCache ? 'B' : 'N';
		readOnlyOneTimeChar = readOnlyOneTime ? 'O' : 'N';
		maxReturnedCachePathChar = maxReturnedCachePath ? 'C' : 'N';
		
		configDir = mainDir + "/" + 
				"N" + netSize +
				"-" + connect +
				"I" + inactiveNode +
				"-C" + cycles + 
				"-" + addCycle + 
				"-Q" + singleParamChar +  queryToAdd + 
				"T" + timeToLive + 
				"W" + maxWait +
				"P" + paramNum + 
				"-AN" + addNodeNum +
				"RN" + removeNodeNum +
				"CN" + changeNodeNum +
				"-" + selectModeChar +
				"CS" + maxCacheSize +
				"-" + cacheChar + liveOptChar + readOnlyOneTimeChar + mergePathChar + 
				sendToSenderChar + addSingleBestPathToCacheChar + maxReturnedCachePathChar +
				"-" + maxCachePath; 

		initConfigDir();
		initConfigFileDir();
		initDataDir();
	}
	
	public char setSelectModeChar(String mode) {
		char ret;

		if (mode.equals(modeAvaiability)) {
			ret = 'A';
		} else if (mode.equals(modeCost)) {
			ret = 'C';
		} else if (mode.equals(modeThroughput)) {
			ret = 'T';

		} else {
			ret = 'E';
		}
		return ret;
	}
	
	public boolean initDir(String dir) {
		boolean success;
		File directory = new File(dir);
		if (directory.exists()) {
			System.out.println("Directory already exists");
			return true;
		} else {
			System.out.println("Directory not exists, creating now");

			success = directory.mkdirs();
			if (success) {
				System.out.printf("Successfully created new directory : %s%n", dir);
			} else {
				System.out.printf("Failed to create new directory: %s%n", dir);
			}
		}
		return false;
	}
	
	public boolean deleteDir(String dir) {
		File directory = new File(dir);
		if (directory.exists()) {
			System.out.println("sto eliminando la cartella");
			for(File file : directory.listFiles()) {
				file.delete();
			}
			return true;
		}
		return false;
	}

	public void initConfigDir() {
		if(initDir(configDir) && deleteOld) {
			deleteDir(configDir + "/" + configFileDir);
			deleteDir(configDir + "/" + dataDir);
		}
	}

	public void initConfigFileDir() {
		initDir(configDir + "/" + configFileDir);
	}

	public void initDataDir() {
		initDir(configDir + "/" + dataDir);
	}
	
	public void makeFile(long seed, int index) {
		FileWriter csvWriter;

		try {
			csvWriter = new FileWriter(configDir + "/" + configFileDir + "/config" + index + ".txt");
			csvWriter.append(configText(seed));
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public String configText(long seed) {
		return String.format(baseText,
				seed,
				cycles,
				netSize,
				connect,
				inactiveNode,
				verbose,
				cache,
				liveOpt,
				readOnlyOneTime,
				maxCacheSize,
				timeToLive,
				queryToAdd,
				addCycle,
				maxWait,
				mergePath,
				sendToSender,
				addSingleBestPathToCache,
				maxReturnedCachePath,
				maxCachePath,
				paramNum,
				singleParam,
				addNodeNum,
				removeNodeNum,
				changeNodeNum).replaceAll(",", ".");
	}

}
