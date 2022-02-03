package DSR_IOT;

import peersim.core.*;
import utility.FindBest;

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
	private static final String MERGEPATH = "mergePath";
	private static final String SENDTOSENDER = "sendToSender";
	private static final String ADDSINGLEPATHCACHE = "addSinglePathCache";
	private static final String READONLYONETIME = "readOnlyOneTime";
	private static final String MAXRETURNEDPATHCACHE = "maxReturnedCachePath";
	private static final String MAXCACHEPATH = "maxCachePath";
	private static final String MAXCACHESIZE = "maxCacheSize";
	private static final String TIMETOLIVE = "timeToLive";
	private static final String QUERYTOADD = "queryToAdd";
	private static final String ADDCYCLE = "addCycle";
	private static final String SELECTMODE = "selectMode";
	private static final String MAXWAIT = "maxWait";


	private static boolean verbose;
	static boolean cache;
	static boolean liveOptimization;
	static boolean sendToSender;
	static boolean addSingleBestPathToCache;   					//true aumenta il numero dei messaggi inviati  
	static boolean mergePathWhenReceived;	 					//I messaggi con stesso id, e stesso valore find, uniscono i path
																//delle loro query in un unico messaggio
	static boolean readOnlyOneTime;
	static boolean maxReturnedCachePath;
	static int maxCachePath;
	static int maxCacheSize;
	static int timeToLive;
	static int queryToAdd;
	static int addCycle;
	static String selectMode;
	
	
	
	static int maxWait;;

	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	public static int pid;
	boolean find = false;

	private NodeInfo nodeInfo;

	static long sendMessage = 0;
	public static long startTime;

	public List<Message> messagesBuffer;
	public List<Query> concludedQuery;
	public List<Path> cachePath;
	public List<Long> alreadyReadMessageId;

	// ------------------------------------------------------------------------
	// Initialization
	// ------------------------------------------------------------------------
	public MyProtocol(String prefix) {
		super();
		pid = Configuration.getPid(prefix);

		verbose = Configuration.getBoolean(prefix + "." + VERBOSE, false);
		cache = Configuration.getBoolean(prefix + "." + CACHE);
		liveOptimization = Configuration.getBoolean(prefix + "." + LIVEOPT);
		mergePathWhenReceived = Configuration.getBoolean(prefix + "." + MERGEPATH);
		sendToSender = Configuration.getBoolean(prefix + "." + SENDTOSENDER);
		addSingleBestPathToCache = Configuration.getBoolean(prefix + "." + ADDSINGLEPATHCACHE);
		readOnlyOneTime = Configuration.getBoolean(prefix + "." + READONLYONETIME);
		maxReturnedCachePath = Configuration.getBoolean(prefix + "." + MAXRETURNEDPATHCACHE);
		maxCachePath = Configuration.getInt(prefix + "." + MAXCACHEPATH);
		maxCacheSize = Configuration.getInt(prefix + "." + MAXCACHESIZE);
		timeToLive = Configuration.getInt(prefix + "." + TIMETOLIVE);
		maxWait = Configuration.getInt(prefix + "." + MAXWAIT);
		queryToAdd = Configuration.getInt(prefix + "." + QUERYTOADD);
		addCycle = Configuration.getInt(prefix + "." + ADDCYCLE);
		selectMode = Configuration.getString(prefix + "." + SELECTMODE,"executionTime");
		startTime = System.currentTimeMillis();
	}

	public Object clone() {
		MyProtocol pr = null;
		try {
			pr = (MyProtocol) super.clone();
			pr.messagesBuffer = new ArrayList<>();
			pr.cachePath = new ArrayList<>();
			pr.concludedQuery = new ArrayList<>();
			if(readOnlyOneTime) {
				pr.alreadyReadMessageId = new ArrayList<>();
			}
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

		int mexSize = messagesBuffer.size();

		for (int i = mexSize - 1; i >= 0; i--) {		//Per ogni messaggio nel buffer

			Message mex = messagesBuffer.get(i);

			if (mex.getFind()) { 						// Se è un messaggio di risposta
				concludedMessageAction(mex);

			} else { 										// Se è un messaggio di richiesta

				if (readOnlyOneTime) { 						// Se devo scartare messaggi con stesso ID
					if (alreadyReadMessageId.contains(mex.getMessageId())) {

						mex.removeActualMex();
						messagesBuffer.remove(i);
						continue;
					}
					alreadyReadMessageId.add(mex.getMessageId());

				}

				analyzeMex(mex);
				sendAction(node, protocolID, mex); // Invio del messaggio: inoltro richiesta o invio risposta

			}

			messagesBuffer.remove(i); // Eliminao il messaggio dal buffer

		}

		checkResponse();

	}

	//////////////////////////////////////////////////////// MESSAGE//////////////////////////////////////////////////////////////////

	//Controllo se è già presente una query con lo stesso id
	public boolean checkQuery(Query newQuery) {
		for (Query query : concludedQuery) {
			if (query.getId() == newQuery.getId()) {
				return true;
			}
		}
		return false;
	}

	//analizzo il messaggio 
	public void analyzeMex(Message mex) {

		Query query = mex.getQuery();
		
		query.analyzeNode(nodeInfo, liveOptimization);

		if (cache) useCache(query);				//Se è abilitato il meccanismo di cache

		mex.setFind(query.checkFind());			//Controllo se il messaggio può diventare un messaggio di risposta
	}

	// azioni da fare in caso di messaggio di risposta
	public void concludedMessageAction(Message mex) {
		Query query = mex.getQuery();
		
		addConcludedQuery(query);
		if ( cache ) addQueryToCache(query);

	}
	

	public boolean addConcludedQuery(Query newQuery) {

		for (Query query : concludedQuery) {
			
			if (query.getId() == newQuery.getId()) {  	//Se gia presente una query con stesso ID
				if(CommonState.getTime() - query.getStartingTime() > maxWait) {
					//Se la risposta arriva in ritardo, non la considero
					return false;
				}
				query.mergePath(newQuery);
				return true;
			}
		}
		
		concludedQuery.add(newQuery);
		return true;
	}


	
	public void checkResponse() {
		for (Query query : concludedQuery) {
			if (CommonState.getTime() - query.getStartingTime() == maxWait) {
				
				FindBest bst = MyObserver.oracle;
				query.setBestTime(bst.getBestExecTime(query));
				int best = query.selectBestPath(selectMode); 									
				query.setSelectedPath(best);
				if(best < 0 ) { 
					return;
				}
				MyObserver.addConcludedQuery(query);
			}
		}
	}

	///////////////////////////////////////////////////////////CACHE ///////////////////////////////////////////////////////////////

	public void addToCache(Path newPath) {
		
		if (newPath.getSize() <= 1) { return;}
		
		for (Path cPath : cachePath) { // Per ogni path in cache controllo che il path non sia ridondante

			if (cPath.isRedundant(newPath)) return;		//Se il path è ridondante
			
		}

		inverseRedundant(newPath); // Controllo che non ci siano path in cache ridondanti con il nuovo path

		if(cachePath.size() > maxCacheSize) {
			removeCache();
		}
		cachePath.add(newPath); // Se il path non è ridondante con nessun path in cache
		
	}
	
	private void removeCache() {
		cachePath.remove(0);
	}


	public void useCache(Query query) { 

		int size = query.getPaths().size();

		
		Path cPath;
		for (int i = cachePath.size()-1; i>=0 ;i--) {		//Per ogni path in cache
			cPath = cachePath.get(i);
			
			if(!cPath.allNodeUp()) {						//Controllo se attivo
				cachePath.remove(i);
				continue;
			}
			query.findSolutionPath(nodeInfo, cPath);		//Utilizzo il path per trovare possibili soluzioni
		}
			
		if(maxReturnedCachePath) {							//Meccanismo che limita il numero di path soluzione ritornati dalla cache 
			int newSize = query.getPaths().size() - size;
			if(newSize > maxCachePath) {
				query.removePaths(size,newSize-maxCachePath);
			}
		}

		addQueryToCache(query);

	}

	private void addQueryToCache(Query query) {
		
		if(addSingleBestPathToCache) {  					
			addToCache(query.getPaths().get(query.getSelectedPath()));
			return;
		}
		
		for (Path path : query.getPaths()) {
			if(!path.allNodeUp()) {
				continue;
			}
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

		if (mex.getFind()) { 				// Se la ricerca risulta completa
			sendMessageBack(mex);
			mex.removeActualMex();
		}else if(mex.getTimeToLive()>=1) {
			sendToNeighbors(node, protocolID, mex);
		}
		mex.removeActualMex();
		
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

		mex.getQuery().clearUnused(); // Elimina i path sbagliati
		MyProtocol pro = findNodeById(mex.getStarterNodeId());
		
		sendMessage(mex,pro);

	}

	// Inserisce il messaggio nel buffer
	public void addMessage(Message newMex) {
		sendMessage++;
		Query newQuery = newMex.getQuery();

		Query query;
		if (mergePathWhenReceived) {
			for (Message mex : messagesBuffer) {
				query = mex.getQuery();

				if (mex.getMessageId() == newMex.getMessageId() && newMex.getFind() == mex.getFind() && query.getId() == newQuery.getId()) {
					query.mergePath(newQuery);
					return; 
				}

			}
		}

		messagesBuffer.add(newMex);

	}

	public void sendMessage(Message mex, MyProtocol nodeTo) {

		Message newMex = new Message(mex);

		newMex.addActualMex();
		
		newMex.setSenderId(nodeInfo.getId());
		nodeTo.addMessage(newMex);

	}

	public void sendToNeighbors(Node receiverNode, int protocolID, Message mex) {

		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) receiverNode.getProtocol(linkableID);
		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);

			//Controllo se il nodo è attivo
			if (!peer.isUp()) {
				continue;
			}
			MyProtocol n = (MyProtocol) peer.getProtocol(protocolID);
			NodeInfo nodeInfo = n.getNodeInfo();

			if (!sendToSender) {
				if (mex.getSenderId() == nodeInfo.getId()) { 	// non invio al nodo che mi ha inviato il messaggio 
					continue;
				}
			}

			sendMessage(mex, n);

		}
	}

	//////////////////////////////////////////////////////////// PRINT////////////////////////////////////////////////////////////////

	public void printCompleteNodeInfo() {
		
		nodeInfo.printInfo();

		System.out.println("\n_________________________Messaggi nel buffer:_________________________\n");
		for (Message mex : messagesBuffer) {
			mex.printInfo();
		}

		System.out.println("\n_________________________Path in cache:_________________________\n");
		for (Path path : cachePath) {
			path.printPath();
		}

		System.out.println("\n_________________________Query conclusi:_________________________\n");
		for (Query query : concludedQuery) {
			query.printInfo();
		}
	}

}