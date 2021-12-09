package DSR_IOT;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
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
	ArrayList<Query> query;

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
		
		query = new ArrayList<>();
		

	}

public boolean execute() {
		
		if (CommonState.getTime() == lastCycle - 1) {
			
			for (int i = 0; i < Network.size(); i++) {

				MyProtocol protocol = (MyProtocol) Network.get(i).getProtocol(pid);

				
				for (Query query : protocol.concludedQuery) { 			// analizzo i messaggi conclusi

					
					query.clearUnused(); 							 	//TODO da rimuovere
					query.setSelectedPath(query.selectBestPath());		//TODO da rimuovere
					this.query.add(query);

				}
			}

			query = orderQuery();
			writeResult();

		}
		return false; // true fa terminare
	}

	public void printResult() {
		printResult(System.out);

	}
	
	public void printResult(PrintStream ps) {
		Query myQuery;
		ps.println("Risultati Simulazione:\n\n");
		for (int j = 0; j < query.size(); j++) {
			myQuery = query.get(j);
			ps.println(myQuery.getResultInfo());
			ps.println("__________________________________________________________________________________________");
		}

	}

	public ArrayList<Query> orderQuery() {
		ArrayList<Query> newList = new ArrayList<>();
		Query myQuery;
		for(int i = 0; i < query.size();i++) {
			for(int j = 0; j < query.size();j++) {
				myQuery = query.get(j);
				if(myQuery.getId()==i) {
					newList.add(myQuery);
				}
			}
		}
		query.clear();
		return newList;
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
		
		pstr.println("Messaggi totali: " + MyProtocol.sendMessage + "\n\n\n\n");
		
	}
	
}