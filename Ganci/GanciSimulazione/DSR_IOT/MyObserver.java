package DSR_IOT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


import DSR_IOT.query.Query;
import DSR_IOT.query.QuerySingle;
import network.MyWireTopology;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import utility.FindBest;
import utility.WriteClass;

public class MyObserver implements Control {
// ------------------------------------------------------------------------
// Parameters
// ------------------------------------------------------------------------

	private static final String PAR_PROT = "protocol";
	private static final String CYCLES = "simulation.cycles";
	private static final String SEED = "random.seed";
	private static final String NETSIZE = "network.size";
	private static final String DIR = "results/QR-";
	private static final String CONNECT = "init.1.connectivity";
	private static final String REMOVENODENUM = "removeNodeNum";
	private static final String CHANGENODENUM = "changeNodeNum";
	private static final String ADDNODENUM = "control.dnet.add";

	long seed;
	private int lastCycle;
	int netSize;
	boolean cache;
	boolean liveOpt;
	boolean mergePath;
	int paramNum;
	boolean singleParam;
	char cacheChar;
	char liveOptChar;
	char mergePathChar;
	char singleParamChar;
	char sendToSenderChar;
	char addSingleBestPathToCacheChar;
	char readOnlyOneTimeChar;
	char maxReturnedCachePathChar;
	int maxCachePath;
	int maxCacheSize;
	int timeToLive;
	int queryToAdd;
	int addCycle;
	static String selectMode;
	char selectModeChar;
	
	int addNodeNum;
	int removeNodeNum;
	int waitCycle = 10;
	int changeNodeNum;
	int inactiveNode;

	String configName;
	StringBuffer actualMexBuf;
	StringBuffer scartoMedioBuf;

	int numQuery=0;
	double connect;

	boolean sendToSender;
	boolean addSingleBestPathToCache;
	boolean readOnlyOneTime;
	boolean maxReturnedCachePath;

	private String result_filename;
	
	static public  ArrayList<Long> noDeleteList = new ArrayList<>();
	
	static public FindBest oracle; 
// ------------------------------------------------------------------------
// Fields
// ------------------------------------------------------------------------

	private final String name;
	static ArrayList<Query> queries;

	private final int pid;

// ------------------------------------------------------------------------
// Constructor
// ------------------------------------------------------------------------
	public MyObserver(String prefix) {
		name = prefix;
		pid = Configuration.getPid(name + "." + PAR_PROT);

		seed = Configuration.getLong(SEED);
		lastCycle = Configuration.getInt(CYCLES);
		netSize = Configuration.getInt(NETSIZE);
		connect = Configuration.getDouble(CONNECT);
		addNodeNum = Configuration.getInt(ADDNODENUM);
		removeNodeNum = Configuration.getInt(name+"."+REMOVENODENUM);
		changeNodeNum = Configuration.getInt(name+"."+CHANGENODENUM);

		// Parametri inizializzatore nodi e query
		paramNum = MyNodeInitializer.paramNum;
		singleParam = MyNodeInitializer.singleParam;
		waitCycle = MyProtocol.maxWait;
		

		// Parametri del protocollo
		cache = MyProtocol.cache;
		liveOpt = MyProtocol.liveOptimization;
		mergePath = MyProtocol.mergePathWhenReceived;
		sendToSender = MyProtocol.sendToSender;
		addSingleBestPathToCache = MyProtocol.addSingleBestPathToCache;
		readOnlyOneTime = MyProtocol.readOnlyOneTime;
		maxReturnedCachePath = MyProtocol.maxReturnedCachePath;
		maxCachePath = MyProtocol.maxCachePath;

		maxCacheSize = MyProtocol.maxCacheSize;
		timeToLive = MyProtocol.timeToLive;
		queryToAdd = MyProtocol.queryToAdd;
		addCycle = MyProtocol.addCycle;
		selectMode = MyProtocol.selectMode;
		inactiveNode = MyWireTopology.inactiveNode;

		singleParamChar = singleParam ? 'S' : 'M';
		cacheChar = cache ? 'C' : 'N';
		liveOptChar = liveOpt ? 'O' : 'N';
		mergePathChar = mergePath ? 'M' : 'N';

		sendToSenderChar = sendToSender ? 'S' : 'N';
		addSingleBestPathToCacheChar = addSingleBestPathToCache ? 'B' : 'N';
		readOnlyOneTimeChar = readOnlyOneTime ? 'O' : 'N';
		maxReturnedCachePathChar = maxReturnedCachePath ? 'C' : 'N';
		selectModeChar = setSelectModeChar(selectMode);

		result_filename = DIR + seed + "-C" + lastCycle + "-" + addCycle + "-N" + netSize + "-" + connect + "-Q"
				+ singleParamChar + numQuery + "-" + queryToAdd + "T" + timeToLive + selectModeChar + "-"
				+ paramNum +"P" + cacheChar + maxCacheSize + liveOptChar + mergePathChar + "-" + sendToSenderChar
				+ addSingleBestPathToCacheChar + readOnlyOneTimeChar + maxReturnedCachePathChar + ".txt";

		queries = new ArrayList<>();

		configName = "N" + netSize +
				"-" + connect +
				"I" + inactiveNode +
				"-C" + lastCycle + 
				"-" + addCycle + 
				"-Q" + singleParamChar +  queryToAdd + 
				"T" + timeToLive + 
				"W" + waitCycle +
				"P" + paramNum + 
				"-AN" + addNodeNum +
				"RN" + removeNodeNum +
				"CN" + changeNodeNum +
				"-" + selectModeChar +
				"CS" + maxCacheSize +
				"-" + cacheChar + liveOptChar + readOnlyOneTimeChar + mergePathChar + 
				sendToSenderChar + addSingleBestPathToCacheChar + maxReturnedCachePathChar +
				"-" + maxCachePath; 

		actualMexBuf = new StringBuffer();
		scartoMedioBuf = new StringBuffer();

		oracle = new FindBest();
	}
	

	
	public boolean execute() {
		
		long time = CommonState.getTime();

		if (time % MyProtocol.addCycle == 0) {  // Se è un ciclo generativo

			if ((lastCycle - 1 - waitCycle) > time) {	//evita di generare queries che non hanno il tempo di poter rispondere
				for (int i = 0; i < MyProtocol.queryToAdd; i++) {
					while(!sendQuery());
				}
			}
		}
		
		deactiveNodes(removeNodeNum);			//disattiva nodi ad ogni ciclo
		
		if (time == lastCycle - 1) {		//Se è l'ultimo ciclo
			finalAction();
			
		}
		
		//System.out.println("NetSize: " + Network.size() + "\n");
		//System.out.println("Mex: " + MyProtocol.sendMessage + "\n");
		//System.out.println("Ciclo numero: " + time + "\tActualMex: " + Message.actualMex + "\n");
		actualMexBuf.append(Message.actualMex + ";");
		
		scartoMedioBuf.append(String.format("%f;",meanDifference()));
		return false; // true fa terminare
	}
	

	
	public void finalAction() {
		Node n;
		for (int i = 0; i < Network.size(); i++) {		//Controllo tutti i nodi per assicurarmi di aver preso tutte le queries

			n = Network.get(i);
			MyProtocol protocol = (MyProtocol) n.getProtocol(pid);

			for (Query query : protocol.concludedQuery) { // analizzo i messaggi conclusi
				addConcludedQuery(query);
			}
		}

		checkResult();
				
		writeResult();
		writeData();
		
	}
	
	public int actualResolvedQuery() {
		int ret = 0;
		for(Query q: queries) {
			if(q.getSelectedPath()!=-1) {
				ret++;
			}
		}
		return ret;
	}
	
	public static void addConcludedQuery(Query newQuery) {
		for (Query q : queries) { 
			if(q.getId() == newQuery.getId() ) {				
				return;
			}
		}
	
		newQuery.setBestTime(oracle.getBestExecTime(newQuery));
		int best = newQuery.selectBestPath(selectMode); 									
		newQuery.setSelectedPath(best);
		queries.add(newQuery);
	}
	
	
	
	//calcola lo scarto medio rispetto alla soluzione ottima
	public double meanDifference() {
		double total=0;
		long best;
		long selected;
		if(queries.size()<=0) {
			return -1;
		}
		int falseQueries = 0;
		for(Query q: queries) {
			
			if(q.getSelectedPath()==-1) {
				falseQueries ++;
				continue;
			}
			
			best = q.getBestTime();
			selected = q.getSelectedTime();
		

			if(best>=0) {
				total += (selected-best);
				if(selected-best<0) {
					System.out.println("ERRORREEEEEE NEL CALCOLO MEDIO DELLO SCARTO");
				}
			}else {
				System.out.println("BEST minore di 0, significa che non esiste soluzione ma qui dice che c'è!!!!!");
			}
		}
		
		total = total/(queries.size()-falseQueries);
		
		return total;
	}

	//crea ed invia ad un nodo la queries
	public boolean sendQuery() {
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		if(!n.isUp()) {
			return false;
		}
		MyProtocol prot = (MyProtocol) n.getProtocol(pid);

		Query query;

		if (singleParam) {
			String input = MyNodeInitializer.parameters[CommonState.r.nextInt(MyNodeInitializer.parameters.length)];

			query = new QuerySingle(MyNodeInitializer.freeQueryId++, input, MyNodeInitializer.setSingleOutput(input));

		} else {
			query = null;
		}

		Message mex = new Message(prot.getNodeInfo().getId(), MyProtocol.timeToLive, query);

		prot.addMessage(mex);
		MyNodeInitializer.queryInfo.append(query.getInfo());
		noDeleteList.add(n.getID());
		return true;
	}

	public char setSelectModeChar(String mode) {
		char ret;

		if (mode.equals(Query.modeAvaiability)) {
			ret = 'A';
		} else if (mode.equals(Query.modeCost)) {
			ret = 'C';
		} else if (mode.equals(Query.modeThroughput)) {
			ret = 'T';

		} else {
			ret = 'E';
		}
		return ret;
	}


	
	public ArrayList<Query> orderQuery() {
		ArrayList<Query> newList = new ArrayList<>();
		Query myQuery;
		for (int i = 0; i < queries.size(); i++) {
			for (int j = 0; j < queries.size(); j++) {
				myQuery = queries.get(j);
				if (myQuery.getId() == i) {
					newList.add(myQuery);
				}
			}
		}
		queries.clear();
		return newList;
	}
	
	

	
	public void checkResult() {
		int id;
		boolean error = false;
		for (Query myQuery : queries) {
			id = myQuery.checkQuery();
			if (id >= 0) {
				System.out.println("ERROREEEE nella query con Id: " + myQuery.getId());
				error = true;
			}
		}
		if (!error) {
			System.out.println("Tutto ok, input ed output corrispondono");
		}
	}
	
	
	/////////////////////////////////////////NODE ACTION////////////////////////
	
	
	
	public void deactiveNodes(int num) { //Rimuove 'num' nodi dalla rete --> li setta a DOWN
		for(int i = 0; i<num;i++) {
			while(!deactiveRandomNode());
		}
	}
	
	public boolean deactiveRandomNode() {
		int index = CommonState.r.nextInt(Network.size());
		Node node = Network.get(index);
		if(noDeleteList.contains(node.getID())) {
			return false;
		}
		if(node.isUp()) {
			deactiveNode(node);
			return true;
		}
		return false;
		
	}
	
	public void deactiveNode(Node node) {
		//node.setFailState(Node.DOWN);
		node.setFailState(Node.DEAD);
	}
	
	public void activeNode(Node node) {
		node.setFailState(Node.OK);
	}
	
	public void removeNode(Node node) {
		node.setFailState(Node.DEAD);
	}
	
	public void changeNodesLink(int num) {
		for(int i = 0; i<num;i++) {
			while(!changeRandomNodeLink());
		}
	}
	
	public boolean changeRandomNodeLink() { //TODO non funzionante, non si hanno i permessi per modificare i link
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		if(!n.isUp()) {
			return false;
		}
			
		changeNodeLink(n);	
		return true;
	}
	
	public void changeNodeLink(Node n) {		//TODO non implementata
	//	MyWireTopology.deleteNodeLink(n);
	//	MyWireTopology.addNodeLink(n);
	}

	public void addNodes(int num) {
		for(int i = 0; i<num;i++) {
			while(!addRandomNode());
		}
	}
	
	public boolean addRandomNode() { //Se non riesco ad aggiungerli, crearne di più e renderli di nuovo operativi. possibile?
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		if(n.isUp()) {
			return false;
		}
			
		addNode(n);	
		return true;
	}
	
	public void addNode(Node n) {
		n.setFailState(Node.OK);
	}
	

	
	////////////////////////////////PRINT
	

	public void printResult() {
		printResult(System.out);

	}

	public void printAllPath() {
		for (Query myQuery : queries) {
			myQuery.printPathsInfo();
		}
	}

	public void printAllPath(int id) {
		System.out.println(
				"\n__________________________________________________________________________\nSoluzioni Query " + id
						+ ":\n");
		Query myQuery;
		for (int i = 0; i < queries.size(); i++) {
			myQuery = queries.get(i);
			if (myQuery.getId() == id) {
				myQuery.printPathsInfo();
			}
		}
	}



	public void printResult(PrintStream ps) {
		Query myQuery;
		ps.println("Risultati Simulazione:\n\n");
		for (int j = 0; j < queries.size(); j++) {
			myQuery = queries.get(j);
			ps.println(myQuery.getResultInfo(selectMode));
			ps.println("__________________________________________________________________________________________");
		}

	}

	
	//////////////////////////////////////////////WRITE///////////////////////////////////
	


	private void writeResult() {

		try {
			FileOutputStream fos = new FileOutputStream(result_filename);
			PrintStream pstr = new PrintStream(fos);

			writeSimulationDettails(pstr);

			printResult(pstr);

			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeSimulationDettails(PrintStream pstr) {

		pstr.println("Dettagli simulazione:\nSeed: " + seed + "\nNumero di cicli: " + lastCycle + "\nNumero Nodi: "
				+ netSize);
		pstr.println("Parametro di connessione: " + connect);
		pstr.println("Nodi disattivati: " + inactiveNode + "\n");
		pstr.println("Numero di query: " + numQuery + "\n");
		pstr.println("Numero di query generate ogni " + addCycle + " cicli: " + queryToAdd + "\n");
		pstr.println("Dimensione cache " + maxCacheSize + "\n");
		pstr.println("Time to live: " + timeToLive + "\n");
		pstr.println("Nodi aggiunti: " + addNodeNum + "\n");
		pstr.println("Nodi rimossi: " + removeNodeNum + "\n");
		pstr.println("Nodi cambiati: " + changeNodeNum + "\n");
		

		if (cache) {
			pstr.println("Utilizzato meccanismo di cache");
		}
		if (liveOpt) {
			pstr.println("Utilizzato meccanismo di ottimizzazione live");
		}
		if (mergePath) {
			pstr.println("Utilizzato meccaniscmo di meshPath nell'inserimento del messaggio nel buffer");
		}
		
		pstr.println("Numero di parametri utilizzati: " + paramNum + "\n");
		
		if (sendToSender) {
			pstr.println("Utilizzato meccanismo di invio al mittente");
		}
		if (addSingleBestPathToCache) {
			pstr.println("Inserito in cache solo il path migliore per ogni messaggio di risposta");
		}
		if (readOnlyOneTime) {
			pstr.println("I messaggi vengono analizzati solo una volta per nodo");
		}
		if (maxReturnedCachePath) {
			pstr.println("La cache restituisce al massimo " + maxCachePath + " numero di path soluzione");
		}

		if (singleParam) {
			pstr.println("I nodi richiedono e forniscono un singolo parametro");
		} else {
			pstr.println("I nodi richiedono e forniscono molteplici parametri");
		}

		pstr.println("\n\n\n\nLista dei nodi:\n\n");

		pstr.println(MyNodeInitializer.nodesInfo);

		pstr.println("\n\n\n\nLista delle query:\n\n");

		pstr.println(MyNodeInitializer.queryInfo);

		pstr.println("\n\n\n\n");

		pstr.println("Messaggi totali: " + MyProtocol.sendMessage + "\n");
		pstr.println("Query con soluzione: " + actualResolvedQuery() + "\tQuery Totali: "
				+ (numQuery + (queryToAdd * Math.ceil((((lastCycle - 1 - waitCycle) / (double)addCycle))))) + "\n\n\n\n");

	}
	
	
	public void writeData() {
		WriteClass myWriter = new WriteClass(configName);
		myWriter.writeActualMex(actualMexBuf.toString());
		myWriter.writeConcludedQuery(actualResolvedQuery()+ ";" + (numQuery + (queryToAdd * Math.ceil((((lastCycle - 1 - waitCycle) / (double)addCycle))))) + ";");
		myWriter.writeMeanTimeDifference(scartoMedioBuf.toString());
		myWriter.writeMessageSend(MyProtocol.sendMessage + ";");
	}	
}