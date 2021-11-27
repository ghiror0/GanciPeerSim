

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class MyObserver implements Control {
// ------------------------------------------------------------------------
// Parameters
// ------------------------------------------------------------------------

	static private final String PAR_PROT = "protocol";
	static private final String CYCLES = "simulation.cycles";
	static private final String SEED = "random.seed";
	static private final String NETSIZE = "network.size";
	static private final String CACHE = "MyProtocol.cache";
	static private final String LIVEOPT = "MyProtocol.liveOpt";
	static private final String NUMQUERY = "init.2.queryNum";
	static private final String DIR = "results/QueryResult";
	static private final String CONNECT = "init.1.connectivity";

	long seed;
	private int lastCycle;
	long netSize;
	boolean cache;
	boolean liveOpt;
	char cacheChar;
	char liveOptChar;
	int numQuery;
	double connect;

	private String result_filename;
// ------------------------------------------------------------------------
// Fields
// ------------------------------------------------------------------------

	private final String name;
	static ArrayList<ArrayList<PathInfo>> pathList;
	static ArrayList<ArrayList<Long>> messages;
	static ArrayList<Integer> queryId;
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
		cache = Configuration.getBoolean(CACHE, true);
		liveOpt = Configuration.getBoolean(LIVEOPT, true);
		numQuery =  Configuration.getInt(NUMQUERY);
		connect = Configuration.getDouble(CONNECT);
		cacheChar = cache ? 'C' : 'N';
		liveOptChar = liveOpt ? 'O' : 'N';
		
		result_filename = DIR + seed + "-" + netSize + "-"+ connect +  "-" + numQuery + "-" + cacheChar + liveOptChar + ".txt";
		
		
		pathList = new ArrayList<ArrayList<PathInfo>>();
		messages = new ArrayList<ArrayList<Long>>();
		queryId = new ArrayList<Integer>();
		selectedPath = new ArrayList<Integer>();

	}

// Control interface method.
	public boolean execute() {

		for (int i = 0; i < Network.size(); i++) {

			MyProtocol protocol = (MyProtocol) Network.get(i).getProtocol(pid);

			if (protocol.find) { 							//Se un protocollo ha una soluzione
				protocol.find = false;

				for (Message mex : protocol.concludedMessage) {		//analizzo i messaggi conclusi
					mex.setFind(false);

					for (PathInfo path : mex.paths) {				//Per ogni path presente, vedo se aggiungerlo alla lista dei path soluzione
						addPath(mex.getMessageId(), MyProtocol.sendMessage, path);
						
					}

					addSelected(mex.getMessageId(), mex.getSelectedPath());

				}
				// protocol.concludedMessage.clear();
				// return true;
			}

		}

		if (CommonState.getTime() == lastCycle - 1) {
			// TODO calcolare tempo di esecuzione totale
			printResult();
			writeResult();
		}

		return false; // true fa terminare
	}

	public void addSelected(int query, int selected) {
		selectedPath.add(queryId.indexOf(query), selected);
	}

	public void printFind(int query, long messages, PathInfo path) {
		System.out.println("Id query: " + query + "\tMessaggi inviati: " + messages);
		System.out.println("time: " + CommonState.getTime()); // PRINTA IL NUMEOR DEL CICLO
		path.printPath();
		System.out.println("\n");
	}

	public void addPath(int query, long messagesNum, PathInfo newPath) {
		int index;

		if (!queryId.contains(query)) {
			queryId.add(query);
			pathList.add(new ArrayList<PathInfo>());
			messages.add(new ArrayList<Long>());
			index = queryId.size() - 1;
		} else {
			index = queryId.indexOf(query);

			for (PathInfo path : pathList.get(index)) {

				if (path.isRedundant(newPath)) { // Se il path è ridondante
					return;
				}

			}
		}

		messages.get(index).add(messagesNum);
		pathList.get(index).add(newPath.getDuplicatePath());
		printFind(query, messagesNum, newPath);
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

		if (cache) {
			pstr.println("Utilizzato meccanismo di cache");
		}
		if (liveOpt) {
			pstr.println("Utilizzato meccanismo di ottimizzazione live");
		}

		pstr.println("\n\n\n\n");
	}
	
}





/*
 * FileNameGenerator fng = new FileNameGenerator(result_filename +
 * Configuration.getInt("random.seed"), ".txt"); fname = fng.nextCounterName();
 */


	/*
	static long seed = Configuration.getLong(SEED);
	static private int lastCycle = Configuration.getInt(CYCLES);
	static long netSize = Configuration.getLong(NETSIZE);
	static boolean cache = Configuration.getBoolean(CACHE, true);
	static boolean liveOpt = Configuration.getBoolean(LIVEOPT, true);
	static char cacheChar = cache ? 'C' : 'N';
	static char liveOptChar = liveOpt ? 'C' : 'N';

	static private final String result_filename = "src/QueryResult" + seed + "-" + netSize + "-" + cacheChar
			+ liveOptChar + ".txt";*/

	/*
	 * public void printResult3() {
	 * System.out.println("Risultati Simulazione:\n\n"); for(int i = 0; i <
	 * pathList.size();i++) { System.out.println("QueryId :" + queryId.get(i)
	 * +"\tMessagesSend: " + messages.get(i));// + "\tInput: " + +"\tOutput");
	 * pathList.get(i).printPath(); System.out.println("\n"); } }
	 */

	/*
	 * public void addPath3(int query, long messages, PathInfo newPath) {
	 * for(PathInfo path: pathList) {
	 * 
	 * if(path.isRedundant(newPath) && queryId.get(pathList.indexOf(path))==query) {
	 * //Se il path è ridondante per la stessa query return; }
	 * 
	 * }
	 * 
	 * queryId.add(query); this.messages.add(messages);
	 * pathList.add(newPath.duplicatePath()); printFind(query,messages,newPath); }
	 */

	/*
	 * public void createResultFile() { FileNameGenerator fng = new
	 * FileNameGenerator(result_filename + System.currentTimeMillis(), ".txt");
	 * fname = fng.nextCounterName(); File f = new File(fname); if(f.exists()) {
	 * f.delete(); } }
	 */

	/*
	 * private void writeResult(int idQuery, String input, String output, PathInfo
	 * path, long sendMessage) { if(false)return; try { // FileOutputStream fos =
	 * new FileOutputStream(fname,true); PrintStream pstr = new PrintStream(fos);
	 * pstr.println("Query id: " + idQuery + "\nInput: " + input + "\nOutput: " +
	 * output); path.printPath(pstr); pstr.println("Messaggi inviati: " +
	 * sendMessage + "\n\n"); fos.close(); } catch (IOException e) { throw new
	 * RuntimeException(e); } }
	 */

	/*
	 * public void addMessage(Message newMex) {
	 * 
	 * for (Message mex : list) {
	 * 
	 * if (mex.messageId == newMex.messageId) {
	 * 
	 * for (PathInfo path : newMex.paths) {
	 * 
	 * if (mex.addPath(path)) {
	 * 
	 * System.out.println("Trovato nuovo path"); mex.mexInfo(); path.printPath();
	 * System.out.println("\nMessaggi inviati: " + MyProtocol.sendMessage);
	 * writeResult(mex.messageId,mex.queryInput,mex.queryOutput, path,
	 * MyProtocol.sendMessage); } } return; }
	 * 
	 * }
	 * 
	 * list.add(newMex.duplicateMex()); newMex.printInfo();
	 * System.out.println("Messaggi inviati: " + MyProtocol.sendMessage);
	 * for(PathInfo path: newMex.paths) {
	 * writeResult(newMex.messageId,newMex.queryInput,newMex.queryOutput, path,
	 * MyProtocol.sendMessage); }
	 * 
	 * }
	 */


