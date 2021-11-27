

import peersim.core.*;
import java.util.ArrayList;
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
	
	private static boolean verbose;
	private static boolean cache;
	private static boolean liveOptimization;
	private static final int maxWait = 10;
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private static int pid;


	// Attributi del nodo
	private int id;
	private String input;
	private String output;
	private int executionTime;
	static public long startTime;
	boolean find = false;

	static long sendMessage = 0;  //TODO un valore per ogni query

	public ArrayList<Message> messagesBuffer;
	public ArrayList<Message> concludedMessage;
	public ArrayList<Long> responseTime;
	public ArrayList<PathInfo> cachePath;



	// ------------------------------------------------------------------------
	// Initialization
	// ------------------------------------------------------------------------
	public MyProtocol(String prefix) {
		super();
		pid = Configuration.getPid(prefix);
		
		verbose = Configuration.getBoolean(prefix + "." + VERBOSE, false);
		cache = Configuration.getBoolean(prefix + "." + CACHE, true);
		liveOptimization = Configuration.getBoolean(prefix + "." + LIVEOPT, true);
		
		startTime = System.currentTimeMillis();
	}

	public Object clone() {
		MyProtocol pr = null;
		try {
			pr = (MyProtocol) super.clone();
			pr.messagesBuffer = new ArrayList<Message>();
			pr.cachePath = new ArrayList<PathInfo>();
			pr.concludedMessage = new ArrayList<Message>();
			pr. responseTime = new ArrayList<Long>();
		} catch (CloneNotSupportedException e) {
		} // never happens
		return pr;
	}

	/////////////////////////////////////////// GET / SET
	/////////////////////////////////////////// /////////////////////////////////////////////////////////////////
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public double getExecTime() {
		return executionTime;
	}

	public void setExecTime(int time) {
		executionTime = time;
	}

	///////////////////////////////////////////// NEXT CYCLE

	public void nextCycle(Node node, int protocolID) {

		if (verbose) {
			printInfo();
			System.out.println("Messaggi presenti all'inizio:");
			printMessages();
		}

		for (Message mex : messagesBuffer) { 			//Per ogni messaggio ricevuto
			
			if(mex.getFind()) {							//Se è un messaggio di risposta	
				concludedMessageAction(mex);
				continue;
			}

			analyzeMex(mex);							//Se è una messaggio di richiesta

			if (cache) {								//se è abilitato meccanismo di cache
				useCache(mex);
				
			}

			sendAction(node, protocolID, mex);			//Invio del messaggio: inoltro richiesta o invio risposta
		   }
		

		if (verbose) {
			System.out.println("Messaggi dopo analisi:");
			printMessages();
		}
		
		clearMessages();								//Elimina i messaggi dal buffer
		
		//checkResponse();

	}

	//////////////////////////////////////////////////////// MESSAGE//////////////////////////////////////////////////////////////////
	//TESTED
	public void analyzeMex(Message mex) {

		String lastOutput;
		int inputIndex;
		int outputIndex;

		for (int i=0; i< mex.paths.size();i++) { 

			PathInfo path = mex.paths.get(i);
			
			lastOutput = path.getLastOutput();			//Prendo l'ultimo output del path
			inputIndex = path.getParamIndex(input); 	//Prendo l'indice del path del parametro di input, se assente -1
			outputIndex = path.getParamIndex(output);  	//Prendo l'indice del path del parametro di output, se assente -1

			if (inputIndex >= 0) { 			// Posso utilizzare il servizio --> l'input è presente nel path

				if (outputIndex >= 0) { 	// Output già presente
					
					if(inputIndex < outputIndex && liveOptimization) { 		//se l'input è presente nel path prima dell'output, allora posso ottimizzare
						optimizePath(path, inputIndex, outputIndex-1);		// il -1 serve perche i metodi considerano l'indice dell'id non del parametro		
					}

				} else { 					// Output non presente

					if (input.equals(lastOutput)) {							//Se l'input del servizio è l'ultimo degli output del path, posso inserire le info del nodo in coda
						path.addEndNode(id, output, executionTime);

					} else {												//Se l'input è presente nel mezzo del path, creo un secondo percorso e lo aggiungo al messaggio
						addNewPath(mex, path, inputIndex);
					}

					if (output.equals(mex.queryOutput)) { 					// Query conclusa
						mex.setFind(true);
					}
				}
			}
		}
	}

	//TESTED
	public void addNewPath(Message mex, PathInfo path, int index) {

		PathInfo newPath = path.getAlternativePath(id, output,executionTime, index);

		mex.addPath(newPath);
	}

	//TESTED
	//Controlla se il nuovo nodo ha un tempo di esecuzione minore dei nodi che andrebbe a sostituire, in caso positivo modifica il path
	public void optimizePath(PathInfo path, int start, int end) {
		if(path.getExecTime(start, end) >= this.executionTime) {
			path.modifyPath(id, output,executionTime, start, end);
		}
	}
	

	//azioni da fare in caso di messaggio di risposta
	public void concludedMessageAction(Message mex) {
		
		for(PathInfo path : mex.paths) {
			addToCache(path);
		}
		
		addConcludedMessage(mex);
		find=true;
		
	}
	
	//Inserisce un messaggio nel buffer delle risposte  //TODO TODO TODO 
	public void addConcludedMessage(Message newMex) {
		int best;
		
		for(Message mex: concludedMessage) {
			if(mex.getMessageId() == newMex.getMessageId()) {
				mex.meshPath(newMex);
				best = mex.selectBestPath();  //TODO controllo tra i 2 migliori, non su tutti //TODO selectBestPath modifica il valore di selectePAth
				mex.setSelectedPath(best);
				mex.setFind(true);
				return;
			}
		}
		
		concludedMessage.add(newMex);  //Il selectedpath già è stato trovato?
		responseTime.add(CommonState.getTime());
	}
	
	public void checkResponse() {
		for(int i= 0; i <responseTime.size(); i++) {
			if(CommonState.getTime()-responseTime.get(i)>=maxWait) {
				
				//TODO massimo tempo di attesa
				//TODO Scriverlo su un file separato per vedere i path totali e quelli selezionati alla fine?
			}
		}
	}
	
	/////////////////////////////////////////////////////////// CACHE TESTED///////////////////////////////////////////////////////////////
	

	public void addToCache(PathInfo newPath) { 
		
		for (PathInfo cPath : cachePath) { // Per ogni path in cache controllo che il path non sia ridondante
			

			if(newPath.idSize()==0) {
				return;
			}

			/*if (newPath.getLastOutput().equals(output) && newPath.idSize()>1) {		//Sel'ultimo output corrisponde a quello del servizio, controllo sul path sena l'ultimo output
				if (cPath.isRedundant(newPath.getTrunk(0, newPath.idSize() - 1))) {
					return;
				}
			}*/

			if (cPath.isRedundant(newPath)) { // Se il path è ridondante con uno in cache
				return;
			}
		}

		
		inverseRedundant(newPath); //Controllo che non ci siano path in cache ridondanti con il nuovo path

		cachePath.add(newPath); // Se il path non è ridondante con nessun path in cache

	}

	public void checkCacheQuery(Message mex) {
		int inputIndex;
		int outputIndex;
		String output = mex.queryOutput;

		/*if (output.equals(this.output)) {		//se l'output della query corrisponde a quello del servizio attuale, cerco direttamente l'input del servizio
			output = this.input;				//TODO non sempre esatto
		}*/

		for (PathInfo path : cachePath) {
			if ((inputIndex = path.getParamIndex(mex.queryInput)) < 0) {		//Se è presente l'input della query
				continue;
			}

			if ((outputIndex = path.getParamIndex(output)) <= 0) {				//Se è presente l'output della query
				continue;
			}

			if ((inputIndex < outputIndex)) {									//Se l'input è presente prima della query
				PathInfo newPath = path.getTrunk(inputIndex, outputIndex);

				/*if (mex.queryOutput.equals(this.output)) {						//se ho cercato l'input del servizio invece che l'output della query
					newPath.addEndNode(this.id, this.output,executionTime);
				}*/

				mex.addPath(newPath);
				mex.setFind(true);
			}
		}
	}

	public PathInfo checkCachePath(PathInfo path, String output) {// TODO da fare per ogni path nel messaggio

		int inputIndex;
		int outputIndex;
		String findOutput = output;

		/*if (output.equals(this.output)) {			//TODO non sempre esatto
			findOutput = this.input;
		}*/

		for (PathInfo cPath : cachePath) {
			
			outputIndex = cPath.getParamIndex(findOutput);		
			if (outputIndex <= 0) { 					// Se l'output è presente
				continue;
			}

			for (int i = outputIndex; i >= 0; i--) { // Controllo tutti i parametri del path per vedere se presente un
													// input di quelli cercati

				inputIndex = path.getParamIndex(cPath.getParamByIndex(i));

				if (inputIndex >= 0) { // Input trovato, mi creo il path usando l'inizio del path del mex e la fine del
										// path in cache
					
					PathInfo newPath = path.getTrunk(0, inputIndex);
					newPath.addPath(cPath.getTrunk(i, outputIndex)); //TODO controllo

					/*if (output.equals(this.output)) {
						newPath.addEndNode(this.id, this.output,executionTime);
					}*/
					return newPath;
				}
			}

		}
		return null;
	}

	private void analyzeWithCache(Message mex) {
		ArrayList<PathInfo> actualPaths = new ArrayList<PathInfo>();
		actualPaths.addAll(mex.paths);
		PathInfo cPath;

		for (PathInfo path : actualPaths) { // per ogni percorso

			cPath = checkCachePath(path, mex.queryOutput); // Controlla se qualcuno può risolvere la query

			if (cPath != null) {
				mex.addPath(cPath);
				mex.setFind(true);
				//addToCache(cPath); //TODO vale la pena aggiungere anche questo path
			}
			addToCache(path);
			
		}
	}

	public void useCache(Message mex) {
		checkCacheQuery(mex); 
		analyzeWithCache(mex);

	}

	public void inverseRedundant(PathInfo newPath) { //Metodo per ripulire la cache da path ridondanti.
		ArrayList<PathInfo> actualPaths = new ArrayList<PathInfo>();
		actualPaths.addAll(cachePath);
		for (PathInfo cPath : actualPaths) {
			if(newPath.isRedundant(cPath)) {
				cachePath.remove(cPath);
			}
		}

	}
	
	
	//////////////////////////////////////////////////////////// SEND ////////////////////////////////////////////////////////////////
	
	
	public void sendAction(Node node, int protocolID, Message mex) {

		if (mex.getFind()) { // Se la ricerca risulta completa
			sendBack(mex);
		} else { 			// Se la ricerca deve continuare
			sendToNeighbor(node, protocolID, mex);
		}
	}

	public MyProtocol findNodeById(int id) { //TODO migliorabile?

		for (int i = 0; i < Network.size(); i++) {

			MyProtocol protocol = (MyProtocol) Network.get(i).getProtocol(pid);

			if (protocol.id == id) {
				return protocol;
			}

		}
		return null;
	}

	public void sendBack(Message mex) { // TODO

		mex.clearUnused();
		MyProtocol pro = findNodeById(mex.getStarterNodeId());
		Message newMex = mex.duplicateMex();
		newMex.setSenderId(id);
		pro.addMessage(newMex);
	
	}


	//Inserisce il messaggio nel buffer
	public void addMessage(Message newMex) {
		sendMessage++;
		
		for (Message mex : messagesBuffer) {

			if (mex.getMessageId() == newMex.getMessageId() && newMex.getFind() == mex.getFind()) {
				mex.meshPath(newMex);
				return; 		// TODO scarto troppo o troppo poco?? fai prova ma con questa
								// linea i messaggi vengono drasticamente diminuiti
								// grazie alla cache solo 1 ciclo in più risparminado mex:
								// da 102.300 a 292
			}

		}

		messagesBuffer.add(newMex);
		
	}

	public void sendMessage(Message mex, MyProtocol node) {

		Message newMex = mex.duplicateMex();
		newMex.setSenderId(id);
		node.addMessage(newMex);

		if (verbose) {
			System.out.println("\t\tinviato al nodo con id: " + node.getId() + " il messaggio: ");
			mex.printInfo();
		}

	}

	public void sendToNeighbor(Node node, int protocolID, Message mex) {

		int linkableID = FastConfig.getLinkable(protocolID);
		Linkable linkable = (Linkable) node.getProtocol(linkableID);

		if (verbose)
			System.out.println("Vicini del nodo:  " + id);

		for (int i = 0; i < linkable.degree(); ++i) {
			Node peer = linkable.getNeighbor(i);
			
			// The selected peer could be inactive
			if (!peer.isUp()) {
				continue;
			}
			MyProtocol n = (MyProtocol) peer.getProtocol(protocolID);

			if (verbose) {
				System.out.println(
						"\tid vicino:\t " + n.getId() + "\tInput:\t" + n.getInput() + "\tOutput:\t" + n.getOutput());
			}

			if (mex.getSenderId() == n.getId()) { // non invio al nodo che mi ha inviato il messaggio //TODO possibilità di scelta tramite parametri?
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

	public void printInfo() {

		System.out.println("\nID Nodo: " + id + "\tInput:\t" + input + "\tOutput:\t" + output + "\tExecTime: " + executionTime);

	}

	public void printMessages() {
		
		
		System.out.println("\n////////////////////////////////////////Messaggi nel buffer:\n");
			for (Message mex : messagesBuffer) {
				mex.printInfo();
			}
			
			
			System.out.println("\n/////////////////////////////////////Path in cache:\n");
			for(PathInfo path: cachePath) {
				path.printPath();
			}
			
			
			System.out.println("\n////////////////////////////////////Messaggi conclusi\n");
			for (Message mex : concludedMessage) {
				mex.printInfo();
			}
	}

	////////////////////////////// NON USATE///////////////////////////////

	public void sendToNeighborAA(Node node, int protocolID) {

		for (Message mex : messagesBuffer) {
			if (mex.getFind()) {
				sendBack(mex);
			} else {

				int linkableID = FastConfig.getLinkable(protocolID);
				Linkable linkable = (Linkable) node.getProtocol(linkableID);

				if (verbose) {
					System.out.println("informazioni nodi:");
				}
				for (int i = 0; i < linkable.degree(); ++i) {
					Node peer = linkable.getNeighbor(i);
					// The selected peer could be inactive
					if (!peer.isUp()) {
						continue;
					}
					MyProtocol n = (MyProtocol) peer.getProtocol(protocolID);

					if (verbose) {
						System.out.println("\tid vicino:\t " + n.getId() + "\tInput:\t" + n.getInput() + "\tOutput:\t"
								+ n.getOutput());
					}

					if (mex.getSenderId() == n.getId()) { // non invio al nodo che mi ha inviato il messaggio //TODO
						continue;
					}
					sendMessage(mex, n);

				}

			}
		}

	}

	public PathInfo checkCacheQueryAA(String input, String output) {
		int inputIndex;
		int outputIndex;

		for (PathInfo path : cachePath) {
			if ((inputIndex = path.getParamIndex(input)) < 0) {
				continue;
			}

			if ((outputIndex = path.getParamIndex(output)) <= 0) {
				continue;
			}

			if ((inputIndex < outputIndex)) {
				return path.getTrunk(inputIndex, outputIndex);
			}
		}

		return null;
	}

	public void addToCacheAA(PathInfo newPath) {
		boolean add = true;

		for (PathInfo cPath : cachePath) {
			if (cPath.isRedundant(newPath)) {
				add = false;
				break;
			}
		}
		if (add) {
			cachePath.add(newPath);
			// System.out.println("\nPath aggiunto alla cache\n\n");
		} /*
			 * else { System.out.println("\nPath NON aggiunto alla cache\n\n"); }
			 */

	}

	public void checkCacheQuery2(Message mex) {
		int inputIndex;
		int outputIndex;

		for (PathInfo path : cachePath) {
			if ((inputIndex = path.getParamIndex(mex.queryInput)) < 0) {
				continue;
			}

			if ((outputIndex = path.getParamIndex(mex.queryOutput)) <= 0) {
				continue;
			}

			if ((inputIndex < outputIndex)) {
				mex.addPath(path.getTrunk(inputIndex, outputIndex));
			}
		}
	}

	public void addToCache2(PathInfo newPath) {
		for (PathInfo cPath : cachePath) {

			if (cPath.isRedundant(newPath)) { // Se il path è ridondante con uno in cache
				return;
			}
		}

		cachePath.add(newPath); // Se il path non è ridondante con nessun path in cache

	}

	public PathInfo checkCachePath2(PathInfo path, String output) {// TODO da fare per ogni path nel messaggio
		int inputIndex;
		int outputIndex;
		// PathInfo tempPath;

		for (PathInfo cPath : cachePath) {
			// cPath.printParam();
			outputIndex = cPath.getParamIndex(output);
			if (outputIndex <= 0) { // Controllo se il pathCache contiene l'output
				continue;
			}

			// System.out.println("\nindice di output:" + output +"\t: " + outputIndex);

			// tempPath = cPath.getTrunk(0, outputIndex); // Prendo la parte di path che
			// contiene l'output

			for (int i = 0; i < outputIndex; i++) { // Controllo tutti i parametri del path per vedere se presente un
													// input di quelli cercati

				inputIndex = path.getParamIndex(cPath.getParamByIndex(i));

				if (inputIndex >= 0) { // Input trovato, mi creo il path usando l'inizio del path del mex e la fine del
										// path in cache
					// System.out.println("\nindice di input:" + inputIndex);
					PathInfo newPath = path.getTrunk(0, inputIndex);
					newPath.addPath(cPath.getTrunk(i, outputIndex)); // era outputIndex + 1

					return newPath;
				}
			}
		}
		return null;
	}

	public void useCache2(Message mex) { // TODO possibile ottimizzazione con controllo di input/output del nodo prima
											// di analizi
		// se analyze modifica in parte il path, ne trovo 2 ridondanti solo con
		// l'aggiunta del nodo attuale ma la cache ne salva 2
		checkCacheQuery2(mex); // Posso rispondere con la memoria cache
		analyzeWithCache2(mex);

	}

	private void analyzeWithCache2(Message mex) {
		ArrayList<PathInfo> actualPaths = new ArrayList<PathInfo>();
		actualPaths.addAll(mex.paths);
		PathInfo cPath;

		for (PathInfo path : actualPaths) { // per ogni percorso

			cPath = checkCachePath2(path, mex.queryOutput); // Controlla se qualcuno può risolvere la query

			if (cPath != null) {
				mex.addPath(cPath);
			}
			addToCache(path);
		}
	}

	/*
	 * NON USATE
	 * 
	 * 
	 * 
	 * public boolean checkParameter2(String param, PathInfo path) { //TODO da
	 * cancellare for (String par : path.getParams()) { if(param.equals(par)) {
	 * return true; } } return false; }
	 * 
	 * public int checkParameter3(String param, PathInfo path) { return
	 * path.getParams().indexOf(param); }
	 * 
	 * private void addNewPath(Message mex, PathInfo path, String input, String
	 * output) {
	 * 
	 * PathInfo newPath = path.alternativePath(id, output,
	 * path.getParams().indexOf(input));
	 * 
	 * mex.addPath(newPath); }
	 * 
	 * 
	 */
	
	public double getTotalExecTime2(PathInfo path) {
		
		Double total = 0.0;
		MyProtocol pro;
		
		for(int id: path.getIds()) {
			pro = findNodeById(id);
			total+=pro.getExecTime();
		}
		
		return total;
	}
	

	public void addResponse2(Message newMex) {
		for (Message mex : concludedMessage) {
			if (newMex.getMessageId() == mex.getMessageId()) {
				mex.meshPath(newMex);
				return;
			}
		}
		concludedMessage.add(newMex);
	}

	
	public double getPathExecTime2(PathInfo path, int start, int end) {
		
		Double total = 0.0;
		MyProtocol pro;
		
		for(int id: path.getTrunk(start,end).getIds()) {
			pro = findNodeById(id);
			total+=pro.getExecTime();
			//System.out.println("node: " + pro.getId() + "\ttime: " + pro.getExecTime());
		}
		
	
		return total;
	}
}