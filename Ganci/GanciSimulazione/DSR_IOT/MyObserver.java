package DSR_IOT;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import DSR_IOT.path.Path;
import DSR_IOT.query.Query;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class MyObserver implements Control {
// ------------------------------------------------------------------------
// Parameters
// ------------------------------------------------------------------------

	private static final String PAR_PROT = "protocol";
	private static final String CYCLES = "simulation.cycles";
	private static final String SEED = "random.seed";
	private static final String NETSIZE = "network.size";
	private static final String CACHE = "protocol.MyProtocol.cache";
	private static final String LIVEOPT = "protocol.MyProtocol.liveOpt";
	private static final String MESHPATH = "protocol.MyProtocol.meshPath";
	private static final String NUMQUERY = "init.2.queryNum";
	private static final String DIR = "results/QR-";
	private static final String CONNECT = "init.1.connectivity";
	private static final String PARAMARRAY = "init.2.paramArray";
	private static final String SINGLEPARAM = "init.2.singleParam";
	
	

	long seed;
	private int lastCycle;
	long netSize;
	boolean cache;
	boolean liveOpt;
	boolean meshPath;
	boolean paramArray;
	boolean singleParam;
	char cacheChar;
	char liveOptChar;
	char meshPathChar;
	char singleParamChar;
	char paramArrayChar;
	int numQuery;
	double connect;

	private String result_filename;
// ------------------------------------------------------------------------
// Fields
// ------------------------------------------------------------------------

	private final String name;
	static ArrayList<ArrayList<Path>> pathList;
	static ArrayList<ArrayList<Long>> messages;
	static ArrayList<Long> queryId;
	static ArrayList<Integer> selectedPath;

	private final int pid;

// ------------------------------------------------------------------------
// Constructor
// ------------------------------------------------------------------------
	public MyObserver(String prefix) {
		name = prefix;
		pid = Configuration.getPid(name + "." + PAR_PROT);
		
		seed = Configuration.getLong(SEED);
		lastCycle = Configuration.getInt(CYCLES);
		netSize = Configuration.getLong(NETSIZE);
		cache = Configuration.getBoolean(CACHE);
		liveOpt = Configuration.getBoolean(LIVEOPT);
		meshPath = Configuration.getBoolean(MESHPATH);
		paramArray = Configuration.getBoolean(PARAMARRAY);
		singleParam = Configuration.getBoolean(SINGLEPARAM);
		numQuery =  Configuration.getInt(NUMQUERY);
		connect = Configuration.getDouble(CONNECT);
		cacheChar = cache ? 'C' : 'N';
		liveOptChar = liveOpt ? 'O' : 'N';
		meshPathChar = meshPath ? 'M' : 'N';
		singleParamChar= singleParam ? 'S':'M';
		paramArrayChar= paramArray ? 'S' : 'L';
		
		result_filename = DIR +  seed + "-C" + lastCycle + "-N" + netSize + "-"+ connect +
				"-Q" + singleParamChar + numQuery + "-" + paramArrayChar  + cacheChar + liveOptChar + meshPathChar+ ".txt";
		
		
		pathList = new ArrayList<>();
		messages = new ArrayList<>();
		queryId = new ArrayList<>();
		selectedPath = new ArrayList<>();

	}

// Control interface method.
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {

			MyProtocol protocol = (MyProtocol) Network.get(i).getProtocol(pid);
			if(!protocol.find /*&& CommonState.getTime() != lastCycle - 1 */) continue;
			for (Query query: protocol.concludedQuery) {		//analizzo i messaggi conclusi
				

				query.clearUnused(); //TODO da rimuovere
				for (Path path : query.getPaths()) {				//Per ogni path presente, vedo se aggiungerlo alla lista dei path soluzione
					addPath(query.getId(), MyProtocol.sendMessage, path);
					addSelected(query.getId(), query.getSelectedPath());
				}

				
			}
		}
		
		if (CommonState.getTime() == lastCycle - 1) {  //Se è l'ultimo ciclo

			
			printResult();
			writeResult();
		}

		return false; // true fa terminare
	}



	public void addSelected(long query, int selected) {
			selectedPath.add(queryId.indexOf(query), selected);
	}

	public void printFind(int query, long messages, Path path) {
		System.out.println("Id query: " + query + "\tMessaggi inviati: " + messages);
		System.out.println("time: " + CommonState.getTime()); // PRINTA IL NUMEOR DEL CICLO
		path.printPath();
		System.out.println("\n");
	}

	public void addPath(long query, long messagesNum, Path newPath) {
		int index;

		if (!queryId.contains(query)) {
			queryId.add(query);
			pathList.add(new ArrayList<>());
			messages.add(new ArrayList<>());
			index = queryId.size() - 1;
		} else {
			index = queryId.indexOf(query);

			for (Path path : pathList.get(index)) {

				if (path.isRedundant(newPath)) { // Se il path è ridondante
					return;
				}

			}
		}

		messages.get(index).add(messagesNum);
		pathList.get(index).add(newPath.getDuplicatePath());
	}

	public void printResult() {
		printResult(System.out);

	}
	
	public void printResult(PrintStream ps) {
		ps.println("Risultati Simulazione:\n\n");
		for (int j = 0; j < queryId.size(); j++) {
			ps.println("QueryId :" + queryId.get(j));
			ps.println("Selected Path: " + selectedPath.get(j));
			ps.println("Lista dei path:\n\n");
			
			for (int i = 0; i < pathList.get(j).size(); i++) {
				ps.print("id path : " + i + "\t");
				pathList.get(j).get(i).printPath(ps);
				ps.println("MessagesSend: " + messages.get(j).get(i));
				ps.println("\n");
			}
			ps.println("__________________________________________________________________________________________");
		}

	}

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

		pstr.println("Dettagli simulazione:\nSeed: " + seed + "\nNumero di cicli: " + lastCycle + "\nNumero Nodi: "+ netSize);
		pstr.println("Parametro di connessione: " + connect);
		pstr.println("Numero di query: " + numQuery + "\n");
		
		if (cache) {
			pstr.println("Utilizzato meccanismo di cache");
		}
		if (liveOpt) {
			pstr.println("Utilizzato meccanismo di ottimizzazione live");
		}
		if(meshPath) {
			pstr.println("Utilizzato meccaniscmo di meshPath nell'inserimento del messaggio nel buffer");
		}
		if(paramArray) {
			pstr.println("Utilizzato array dei parametri breve\n");
		}else {
			pstr.println("Utilizzato array dei parametri lungo\n");
		}
		
		if(singleParam) {
			pstr.println("I nodi richiedono e forniscono un singolo parametro");
		}else {
			pstr.println("I nodi richiedono e forniscono molteplici parametri" );
		}
		

		pstr.println("\n\n\n\nLista dei nodi:\n\n");
		
		pstr.println(MyNodeInitializer.nodesInfo);
		
		pstr.println("\n\n\n\nLista delle query:\n\n");
		
		pstr.println(MyNodeInitializer.queryInfo);
		
		pstr.println("\n\n\n\n");
	}
	
}