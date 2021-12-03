package DSR_IOT;

import peersim.core.*;
import java.util.ArrayList;
import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.path.Path;
import DSR_IOT.query.Query;
import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;

public class MyProtocol implements CDProtocol {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------

	private static final String VERBOSE = "verbose";
	private static final String CACHE = "cache";
	private static final String LIVEOPT = "liveOpt";
	private static final String MESHPATH = "meshPath";

	private static boolean verbose;
	private static boolean cache;
	private static boolean liveOptimization;
	private static boolean meshPathWhenReceived; // I messaggi con stesso id, e stesso valore find, uniscono i path
													// delle loro query in un unico messaggio
	private static final int maxWait = 10;
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private static int pid;
	boolean find = false;

	private NodeInfo nodeInfo;

	static long sendMessage = 0;
	public static long startTime;

	public List<Message> messagesBuffer;
	public List<Query> concludedQuery;
	public List<Long> responseTime;
	public List<Path> cachePath;

	// ------------------------------------------------------------------------
	// Initialization
	// ------------------------------------------------------------------------
	public MyProtocol(String prefix) {
		super();
		pid = Configuration.getPid(prefix);

		verbose = Configuration.getBoolean(prefix + "." + VERBOSE, false);
		cache = Configuration.getBoolean(prefix + "." + CACHE);
		liveOptimization = Configuration.getBoolean(prefix + "." + LIVEOPT);
		meshPathWhenReceived = Configuration.getBoolean(prefix + "." + MESHPATH);

		startTime = System.currentTimeMillis();
	}

	public Object clone() {
		MyProtocol pr = null;
		try {
			pr = (MyProtocol) super.clone();
			pr.messagesBuffer = new ArrayList<>();
			pr.cachePath = new ArrayList<>();
			pr.concludedQuery = new ArrayList<>();
			pr.responseTime = new ArrayList<>();
		} catch (CloneNotSupportedException e) {
		} // never happens
		return pr;
	}

	/////////////////////////////////////////// GET / SET
	/////////////////////////////////////////// /////////////////////////////////////////////////////////////////

	public void setNodeInfo(NodeInfo node) {
		this.nodeInfo = node;
	}

	public NodeInfo getNodeInfo() {
		return nodeInfo;
	}

	///////////////////////////////////////////// NEXT CYCLE

	public void nextCycle(Node node, int protocolID) {

		if (verbose) {
			this.nodeInfo.printInfo();
			System.out.println("Messaggi presenti all'inizio:");
			printMessages();
		}

		int mexSize = messagesBuffer.size();
		
		for (int i = mexSize-1; i >= 0; i--) {
			
			Message mex = messagesBuffer.get(i);
			
			if (mex.getFind()) { // Se è un messaggio di risposta
				concludedMessageAction(mex);
				continue;
			}

			analyzeMex(mex); // Se è una messaggio di richiesta

			sendAction(node, protocolID, mex); // Invio del messaggio: inoltro richiesta o invio risposta
		}

		if (verbose) {
			System.out.println("Messaggi dopo analisi:");
			printMessages();
		}

		clearMessages(); // Elimina i messaggi dal buffer

		// checkResponse();

	}

	//////////////////////////////////////////////////////// MESSAGE//////////////////////////////////////////////////////////////////



	public void analyzeMex(Message mex) {

		Query query = mex.getQuery();

		query.analyzeNode(nodeInfo, liveOptimization);

		if (cache) { // se è abilitato meccanismo di cache
			useCache(query);
		}

		mex.setFind(query.checkFind());
	}

	// azioni da fare in caso di messaggio di risposta
	public void concludedMessageAction(Message mex) {
		Query query = mex.getQuery();

		if (cache) {
			addQueryToCache(query);
		}

		addConcludedQuery(query);
		find = true;

	}

	// Inserisce un messaggio nel buffer delle risposte //TODO TODO TODO
	public void addConcludedQuery(Query newQuery) {
		int best;

		for (Query query : concludedQuery) {
			if (query.getId() == newQuery.getId()) {
				query.meshPath(newQuery);
				best = query.selectBestPath(); 	// TODO controllo tra i 2 migliori, non su tutti 	//TODO selectBestPath											
				query.setSelectedPath(best);	// modifica il valore di selectePath
				return;
			}
		}
		
		best = newQuery.selectBestPath(); 
		newQuery.setSelectedPath(best);

		concludedQuery.add(newQuery); 
		responseTime.add(CommonState.getTime());
	}

	public void checkResponse() {
		for (int i = 0; i < responseTime.size(); i++) {
			if (CommonState.getTime() - responseTime.get(i) >= maxWait) {

				// TODO massimo tempo di attesa
				// TODO Scriverlo su un file separato per vedere i path totali e quelli
				// selezionati alla fine?
			}
		}
	}

	/////////////////////////////////////////////////////////// CACHE
	/////////////////////////////////////////////////////////// TESTED///////////////////////////////////////////////////////////////

	public void addToCache(Path newPath) {
		
		if (newPath.size() == 0) return;
		
		for (Path cPath : cachePath) { // Per ogni path in cache controllo che il path non sia ridondante

			if (cPath.isRedundant(newPath)) return;		//Se il path è ridondante
			
			/*
			 * if (newPath.getLastOutput().equals(output) && newPath.idSize()>1) {
			 * //Sel'ultimo output corrisponde a quello del servizio, controllo sul path
			 * sena l'ultimo output if (cPath.isRedundant(newPath.getTrunk(0,
			 * newPath.idSize() - 1))) { return; } }
			 */
			
		}

		inverseRedundant(newPath); // Controllo che non ci siano path in cache ridondanti con il nuovo path

		cachePath.add(newPath); // Se il path non è ridondante con nessun path in cache
		
	}
	


	public void useCache(Query query) {

		for (Path cPath : cachePath) {
			query.findSolutionPath(nodeInfo, cPath);
		}

		addQueryToCache(query);

	}

	private void addQueryToCache(Query query) {
		for (Path path : query.getPaths()) {
			addToCache(path);
		}
	}

	public void inverseRedundant(Path newPath) { // Metodo per ripulire la cache da path ridondanti.
		int size = cachePath.size();
		Path cPath;

		for (int i = size - 1; i >= 0; i--) {
			
			cPath = cachePath.get(i);
			
			if (newPath.isRedundant(cPath)) {  //Se il path in cache è ridondante con il nuovo path da aggiungere in cache
				cachePath.remove(cPath);
			}
		}

	}

	//////////////////////////////////////////////////////////// SEND
	//////////////////////////////////////////////////////////// ////////////////////////////////////////////////////////////////

	public void sendAction(Node node, int protocolID, Message mex) {

		if (mex.getFind()) { // Se la ricerca risulta completa
			sendMessageBack(mex);
		} else { // Se la ricerca deve continuare
			sendToNeighbor(node, protocolID, mex);
		}
	}

	public MyProtocol findNodeById(long id) { // TODO migliorabile?

		for (int i = 0; i < Network.size(); i++) {

			MyProtocol protocol = (MyProtocol) Network.get(i).getProtocol(pid);

			if (protocol.nodeInfo.getId() == id) {
				return protocol;
			}

		}
		return null;
	}

	public void sendMessageBack(Message mex) {

		mex.getQuery().clearUnused(); // Elimina i path giusti //TODO perche alla fine me ne ritrovo alcuni?
		MyProtocol pro = findNodeById(mex.getStarterNodeId());
		sendMessage(mex,pro);

	}

	// Inserisce il messaggio nel buffer
	public void addMessage(Message newMex) {
		sendMessage++;
		Query newQuery = newMex.getQuery();

		Query query;
		if (meshPathWhenReceived) {
			for (Message mex : messagesBuffer) {
				query = mex.getQuery();

				if (mex.getMessageId() == newMex.getMessageId() && newMex.getFind() == mex.getFind()) {
					query.meshPath(newQuery);
					return; 
					// TODO scarto troppo o troppo poco?? fai prova ma con questa
					// linea i messaggi vengono drasticamente diminuiti
					// grazie alla cache solo 1 ciclo in più risparminado mex:
					// da 102.300 a 292
				}

			}
		}

		messagesBuffer.add(newMex);

	}

	public void sendMessage(Message mex, MyProtocol nodeTo) {

		Message newMex = mex.duplicateMex();
		newMex.setSenderId(nodeInfo.getId());
		nodeTo.addMessage(newMex);

		if (verbose) {
			System.out.println("\t\tinviato al nodo con id: " + nodeTo.nodeInfo.getId() + " il messaggio: ");
			mex.printInfo();
		}

	}

	public void sendToNeighbor(Node receiverNode, int protocolID, Message mex) {

		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) receiverNode.getProtocol(linkableID);

		if (verbose)
			System.out.println("Vicini del nodo:  " + nodeInfo.getId());

		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);

			// The selected peer could be inactive
			if (!peer.isUp()) {
				continue;
			}
			MyProtocol n = (MyProtocol) peer.getProtocol(protocolID);
			NodeInfo nodeInfo = n.getNodeInfo();

			if (verbose) {
				System.out.println("\tInfo Nodo Vicino:");
				nodeInfo.printInfo();
			}

			if (mex.getSenderId() == nodeInfo.getId()) { // non invio al nodo che mi ha inviato il messaggio //TODO
															// possibilità di scelta tramite parametri?
				continue;
			} else {
				sendMessage(mex, n);
			}
		}
	}

	public void clearMessages() {
		messagesBuffer.clear();
	}

	//////////////////////////////////////////////////////////// PRINT////////////////////////////////////////////////////////////////

	public void printMessages() {

		System.out.println("\n////////////////////////////////////////Messaggi nel buffer:\n");
		for (Message mex : messagesBuffer) {
			mex.printInfo();
		}

		System.out.println("\n/////////////////////////////////////Path in cache:\n");
		for (Path path : cachePath) {
			path.printPath();
		}

		System.out.println("\n////////////////////////////////////Messaggi conclusi\n");
		for (Query query : concludedQuery) {
			query.printInfo();
		}
	}

}