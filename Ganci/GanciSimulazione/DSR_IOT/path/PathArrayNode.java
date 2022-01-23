package DSR_IOT.path;

import java.io.PrintStream;
import java.util.ArrayList;

import DSR_IOT.MyProtocol;
import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.query.Query;
import DSR_IOT.query.QuerySingle;
import peersim.core.Network;
import peersim.core.Node;

public class PathArrayNode implements Path{

	private ArrayList<NodeSingle> nodes;

	public PathArrayNode() {
		nodes = new ArrayList<>();	
	}
	 
	public PathArrayNode(Query query) {
		nodes = new ArrayList<>();	
		initPath(query);
	}
	
	public void initPath(Query query) {
		QuerySingle myQuery = (QuerySingle) query;
		addStartParameter(myQuery.getInput());
	}
	
	/////////////////////////////////////////////////////////////// GET-SET//////////////////////////////////
	private int getInputIndex(String param) {
		for (int i = 1; i < nodes.size(); i++) {
			if (nodes.get(i).getInput().equals(param)) {
				return i;
			}
		}
		return -1;
	}

	private int getOutputIndex(String param) { 
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getOutput().equals(param)) {
				return i;
			}
		}
		return -2;
	}

	private String getInputByIndex(int index) {

		if (index < nodes.size()) {
			return nodes.get(index).getInput();
		}

		return null;
	}

	private String getOutputByIndex(int index) {

		if (index < nodes.size()) {
			return nodes.get(index).getOutput();
		}

		return null;
	}
			
	public int getNodeIndex(long nodeId) {
		for (int i = 1; i < nodes.size(); i++) {
			if (nodeId == nodes.get(i).getId()) {
				return i;
			}
		}
		return -1;
	}

	public int getSize() {
		return nodes.size();

	}
	
	/////////////////////////////////////////////////////////// ADD
	/////////////////////////////////////////////////////////// ///////////////////////////////////////////////////

	
	/*
	 * Metodo per aggiungere un intero path: rimuove il primo input del path da
	 * aggiungere poich� deve corrispondere all'ultimo output del path presente
	 */
	public void addPath(Path path) {
		PathArrayNode newPath = (PathArrayNode) path;
		nodes.addAll(newPath.nodes);
	}

	/**
	 * 
	 * @param nodeId
	 * @param output
	 * @param index: posizione in cui si vuoi inserire il nodo
	 * 
	 *               Aggiunge un nodo, con relativo output, in posizione 'index'
	 */
	private void addNode(NodeSingle node, int index) {
		nodes.add(index, node);
	}

	/**
	 * 
	 * @param nodeId
	 * @param output
	 * 
	 *               Aggiunge un nodo, con relativo output, alla fine del path
	 */
	public void addEndNode(NodeInfo node) {
		NodeSingle myNode = (NodeSingle) node;
		nodes.add(myNode);
	}

	/**
	 * 
	 * @param input Parametro iniziale disponibile dalla query utente
	 * 
	 *              Inserisce il parametro disponibile fornito dalla query utente
	 */
	public void addStartParameter(String input) {
		NodeSingle node = new NodeSingle(-1,"",input);
		nodes.add(0,node);		
	}
	


	/////////////////////////////////////////////////////////// GET
	/////////////////////////////////////////////////////////// //////////////////////////////////////////////////////
	

	/**
	 * 
	 * @return Ultimo output disponibile
	 */
	public String getLastOutput() {
		return nodes.get(nodes.size()-1).getOutput();
	}
	
	
	public boolean checkNode(long nodeId) {
		Node n;
		for(int i = 0; i < Network.size();i++) {
			n = Network.get(i);
			MyProtocol protocol = (MyProtocol) n.getProtocol(MyProtocol.pid);

			if (protocol.getNodeInfo().getId() == nodeId) {
				return n.isUp();
			}
		}
		return false;

	}

	public boolean allNodeUp() {
		NodeSingle node;
		for(int i = 1; i < nodes.size();i++) {
			node = nodes.get(i);
			if(!checkNode(node.getId())) {
				return false;
			}
		}
		return true;
	}

	//Somma dei tempi di esecuzione da start a end compresi
	public long getExecTime(int start, int end) {
		long total = 0;

		for (NodeSingle node: nodes.subList(start, end+1)) {
			/*if(!checkNode(node.getId())) { //Non pi� necessario, faccio controllo prima di chiamarlo
				return Long.MAX_VALUE;
			}*/
			total += node.getExecTime();
		}

		return total;
	}
	
	//Somma dei tempi di esecuzione da start a end compresi
	public double getAvaiability(int start, int end) {
		double total = 1;

		for (NodeSingle node: nodes.subList(start, end+1)) {
			/*if(!checkNode(node.getId())) {
				return 0;
			}*/
			total *= node.getAvaiability();
		}

		return total;
	}
	
	//Somma dei tempi di esecuzione da start a end compresi
	public double getThroughput(int start, int end) {
		double total = 1;

		for (NodeSingle node: nodes.subList(start, end+1)) {
			/*if(!checkNode(node.getId())) {
				return 0;
			}*/
			total *= node.getThroughput();
		}

		return total;
	}
	
	//Somma dei tempi di esecuzione da start a end compresi
	public long getCost(int start, int end) {
		long total = 0;

		for (NodeSingle node: nodes.subList(start, end+1)) {
			/*if(!checkNode(node.getId())) {
				return Long.MAX_VALUE;
			}*/
			total += node.getCost();
		}

		return total;
	}

	public long getTotalExecTime() {
		return getExecTime(1,nodes.size()-1);
		/*long total = 0;

		for (NodeSingle node: nodes) {
			total += node.getExecTime();
		}

		return total;*/
	}
	
	public long getTotalCost() {
		return getCost(1,nodes.size()-1);
	}
	
	public double getTotalThroughput() {
		return getThroughput(1,nodes.size()-1);
	}
	
	public double getTotalAvaiability() {
		return getAvaiability(1,nodes.size()-1);
	}
	


	
		/**
		 * Fornisce una copia del path
		 * 
		 * @return Copia del path
		 */
	public PathArrayNode getDuplicatePath() {
			PathArrayNode newPath = new PathArrayNode();
			newPath.nodes.addAll(nodes);
			return newPath;
		}

		
		/**
		 * 
		 * @param nodeId
		 * @param output
		 * @param index
		 * 
		 *               Crea una copia del path attuale, modificandolo inserendo un
		 *               nodo in posizione 'index'
		 * 
		 * @return Nuovo path
		 */
	public PathArrayNode getAlternativePath(NodeInfo node, int index) {
			PathArrayNode newPath = new PathArrayNode();	
			newPath.nodes.addAll(nodes.subList(0, index));
			newPath.addEndNode(node);
			return newPath;
		}
	

	
	/**
	 * 
	 * @param start, compreso nel trunk
	 * @param end,   escluso dal trunk
	 * @return Ritorna una copia di una parte del path
	 */
	public PathArrayNode getTrunk(int start, int end) {
		PathArrayNode newPath = new PathArrayNode();
		newPath.nodes.addAll(nodes.subList(start, end+1));
		return newPath;
	}
////////////////////////////////////////////////////////////////////////////////////MODIFICHE EFFETTUATE PER METTERE NODEINFO////////////////////////////////
	private PathArrayNode getTrunk(String start, String end) {

		int inputIndex = getInputIndex(start);
		int outputIndex = getOutputIndex(end);

		if (outputIndex >= 0 && inputIndex >= 0 && (inputIndex <= outputIndex)) {
			return getTrunk(inputIndex, outputIndex);
		}

		return null;
	}
	

	

	public Path analyzePath(NodeInfo node, boolean liveOptimization) {

		NodeSingle myNode = (NodeSingle) node;
		String lastOutput;
		int inputIndex;
		int outputIndex;

		String input = myNode.getInput();
		String output = myNode.getOutput();

		lastOutput = getLastOutput(); 			// Prendo l'ultimo output del path
		inputIndex = getOutputIndex(input); 	// Prendo l'indice del path del parametro di input, se assente -1
		outputIndex = getOutputIndex(output); 	// Prendo l'indice del path del parametro di output, se assente -1

		if (inputIndex >= 0) { 					// Posso utilizzare il servizio --> l'input � presente nel path

			if (outputIndex >= 0) { 			// Output gi� presente

				if (inputIndex < outputIndex && liveOptimization) { 	// se l'input � presente nel path prima dell'output,
																		// allora posso ottimizzare
					optimizePath(myNode, inputIndex + 1, outputIndex);

				}

			} else { 							// Output non presente

				if (input.equals(lastOutput)) { // Se l'input del servizio � l'ultimo degli output del path,
												// possoinserire le info del nodo in coda
					addEndNode(node);

				} else { 						// Se l'input � presente nel mezzo del path, creo un secondo percorso e lo
												// aggiungo al messaggio
					return getAlternativePath(node, inputIndex + 1);
				}
			}
		}
		return null;
	}

	
	
	
	
	
	// Cerca una composizione di path che possa unire
	public PathArrayNode findComposedPath(PathArrayNode cPath, String qOutput) {

		int outputIndex = cPath.getOutputIndex(qOutput);
		int pInputIndex;

		if (outputIndex < 0) {
			return null;
		}

		for (int i = outputIndex; i >= 0; i--) { 	// Controllo tutti i parametri del path per vedere se
													// presente un input di quelli presenti nel cache path

			pInputIndex = getOutputIndex(cPath.getInputByIndex(i));

			if (pInputIndex >= 0) { 	// Input trovato: mi creo il path usando l'inizio del path del mex e la fine del
										// path in cache -->Escludo il caso index = 0 perche prenderebbe solo un pezzo
										// del path cPath, gi� considerato in altri metodi


				if(pInputIndex==0) { 	//Escludo il caso index = 0 perche prenderebbe solo un pezzo
										// del path cPath, gi� considerato in altri metodi
					return null;
				}	
				
				if(getOutputIndex(qOutput)>=0 && pInputIndex>getOutputIndex(qOutput)) {
					return null;
				}
				PathArrayNode newPath = getTrunk(0, pInputIndex);
				newPath.addPath(cPath.getTrunk(i, outputIndex));
				return newPath;
			}
		}
		return null;

	}
	

	// Controlla se il path pu� restituire una soluzione alla query
	public Path getSolutionPath(Query query) {
		QuerySingle myQuery = (QuerySingle) query;
		
		return getSolutionPath(myQuery);
	}
		
	private Path getSolutionPath(QuerySingle query) {
		String qInput = query.getInput();
		String qOutput = query.getOutput();


		PathArrayNode newPath = getTrunk(qInput, qOutput);

		if (newPath != null) {
			newPath.initPath(query);
			return newPath;

		}
		return null;
		
	}
	// Controlla se il path pu� restituire una soluzione alla query
	public Path getSolutionPath(Query query, NodeInfo node) {
		QuerySingle myQuery = (QuerySingle) query;
		NodeSingle myNode = (NodeSingle) node;
		return getSolutionPath(myQuery,myNode);
	}
		
	private Path getSolutionPath(QuerySingle query, NodeSingle node) {
		String qInput = query.getInput();
		String qOutput = query.getOutput();
		String nOutput = node.getOutput();
		String nInput = node.getInput();

		if (qOutput.equals(nOutput) && (getOutputIndex( nInput) < getOutputIndex(qOutput) || getOutputIndex(qOutput)< 0 )) {
			
			PathArrayNode newPath = getTrunk(qInput,nInput);

			if (newPath != null) {
				newPath.initPath(query);
				newPath.addEndNode(node);
				return newPath;

			}
		}
		return null;
	}


	public Path getComposedSolutionPath(Query query, NodeInfo node, Path cPath) {
		
		QuerySingle myQuery = (QuerySingle) query;
		PathArrayNode myCPath = (PathArrayNode) cPath;
		NodeSingle myNode = (NodeSingle) node;
		return getComposedSolutionPath(myQuery,myNode,myCPath);
	}
	
	private Path getComposedSolutionPath(QuerySingle query, NodeSingle node, PathArrayNode cPath) {
		Path newPath;
		String qOutput = query.getOutput();
		String nOutput =  node.getOutput();
		String nInput = node.getInput();

		if (qOutput.equals(nOutput) && 
				(cPath.getOutputIndex(nInput) < cPath.getOutputIndex(qOutput) || cPath.getOutputIndex(qOutput)< 0 )) {
			
			newPath = findComposedPath(cPath,  nInput);

			
			if (newPath != null) {
				newPath.addEndNode(node);
				return newPath;
			}
		}
		
		return null;

	}
	
	public Path getComposedSolutionPath(Query query, Path cPath) {
		
		QuerySingle myQuery = (QuerySingle) query;
		PathArrayNode myCPath = (PathArrayNode) cPath;
		return getComposedSolutionPath(myQuery,myCPath);
	}
	
	private Path getComposedSolutionPath(QuerySingle query,  PathArrayNode cPath) {
		Path newPath;
		String qOutput = query.getOutput();

		
		if (getOutputIndex(qOutput) >= 0)  return null;	// se l'output gia � presente, non cerco nulla
		
		newPath = findComposedPath(cPath, qOutput); // Controllo se il cache path attuale mi risolve la query

		if (newPath != null) {
			return newPath;
		}
		return null;

	}

	
	/* Controlla se "newPath" � ridondante nel path presente */ 
	public boolean isRedundant(Path Path) {
		PathArrayNode newPath = (PathArrayNode) Path;
		
		int idIndex;
		int size = getSize();
		int newSize = newPath.getSize()-1;
		

		if (newPath.nodes.isEmpty() || newSize == 0) { // Se non ci sono nodi, il path � considerato sempre ridondante
			return true;
		}

		idIndex = getNodeIndex(newPath.nodes.get(1).getId());  //Prendo la posizione del nodo, se c'�
		
		if (idIndex < 0) { // Se il primo nodo non � presente nel path, il nuovo path non pu� essere
							// ridondante
			return false;
		}

		if (newSize + idIndex > size) { // Se la dimensione del nuovo path sommata all'indice
																	// del primo parametro in comune
																	// � maggiore della dimensione del path, allora non
																	// pu� essere ridondante
			return false;
		}

		// Controllo se i nodi, dopo il primo in comune, continuano ad essere in comune
		for (int i = 2; i <= newSize && i + idIndex < size; i++) {

			if (nodes.get(idIndex + i-1).getId() == newPath.nodes.get(i).getId()) { //Se i nodi sono uguali
				continue;
			}
			return false;
		}

		return true;

	}
	
	



	// TESTED
	// Controlla se il nuovo nodo ha un tempo di esecuzione minore dei nodi che
	// andrebbe a sostituire, in caso positivo modifica il path
	public void optimizePath(NodeSingle node, int start, int end) {
		long time = getExecTime(start, end);
		long nTime = node.getExecTime();
	
		
		if(time < nTime) {
			return;
		}
		if( time == nTime && start==end) {
			return;
		}
		modifyPath(node, start, end);	
		
	}


	/**
	 * 
	 * @param nodeId
	 * @param param
	 * @param startIndex
	 * @param endIndex   (non compreso)
	 * 
	 * 
	 *                   Elimina un segmento del path inserendoci un nodo
	 *                   sostitutivo
	 */
	public void modifyPath(NodeSingle node, int startIndex, int endIndex) { // TODO chiamarlo shortenPath()
		// rimuovo la parte da eliminare
		nodes.removeAll(nodes.subList(startIndex, endIndex+1)); 
		// Aggiungo il nuovo nodo nella posizione desiderata
		addNode(node, startIndex); 
		
	}
	
	
	public int checkPath() {
		String prec;
		String succ;
		NodeSingle node = nodes.get(0);
		if(node.getId()!=-1) return -2;
		prec = node.getInput();
		succ = node.getOutput();
		if(prec.equals(succ)) return 0;
		for(int i = 1; i < nodes.size();i++) {
			node = nodes.get(i);
			prec = node.getInput();
			if(!succ.equals(prec)) return i;
			succ = node.getOutput();
		}
		return -1;
	}
	
	//////////////////////////////////////////////////////CHECK///////////////////////////////////////////////////////////////
	
	public boolean checkFind(Query query) {
		QuerySingle myQuery = (QuerySingle) query;
		if (getLastOutput().equals(myQuery.getOutput())) {
			return true;
		}
		return false;
	}

	
	public boolean checkRemove(Query query) {
	
		return !checkFind(query);
	}

	////////////////////////////////////////////////////// PRINT///////////////////////////////////////////////////////////

	public void printPath2() {
		printPath2(System.out);
	}
	
	public void printPath() {
		printPath(System.out);
	}
	
	
	public void printPath(PrintStream ps) {
		NodeSingle node;
		for(int i = 0; i<nodes.size();i++) {
			node = nodes.get(i);
			ps.print(node.getInput() + "->(" + node.getId() + ")->");
		}
		node = nodes.get(nodes.size()-1);
		ps.print(node.getOutput());
		ps.print(";\n");
	}
	
	public void printPath2(PrintStream ps) {
		
		for(NodeSingle node: nodes) {
			ps.print(node.getOutput() + "->");
		}
		ps.print(";\n");
		
		for(NodeSingle node: nodes) {
			ps.print(node.getId() + "->");
		}
		ps.print(";\n");

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public boolean isEquals(Path newPath) {
		PathArrayNode myPath = (PathArrayNode) newPath;
		if(nodes.size() != myPath.nodes.size()) {
			return false;
		}
		for(int i = 0; i <nodes.size() ;i++) {
			if(nodes.get(i) != myPath.nodes.get(i)) {
				return false;
			}
		}
	
		return true;
	}
	/*
	public boolean isRedundant2(Path Path) {
		PathArrayNode newPath = (PathArrayNode) Path;
		//System.out.println("Sono dentro metodo con path: "  );
		//newPath.printPath();
		boolean verbose = false;
		int idIndex;
		int size = getSize();
		int newSize = newPath.getSize();

		if (newPath.nodes.isEmpty() || (newPath.nodes.size()==1 && newPath.nodes.get(0).getId() == -1 )) { // Se non ci sono nodi, il path � considerato sempre ridondante
			if (verbose) System.out.println("e' vuoto  o solo nodo parametri iniziali");
			return true;
		}

		idIndex = getNodeIndex(newPath.nodes.get(0).getId());
		//idIndex = getNodeIndex(newPath.getFirstNodeId());
		if (verbose) System.out.println("cerco il nodo: " + newPath.getFirstNodeId() + " indice: " + idIndex);
	
				
		if (idIndex < 0) { // Se il primo nodo non � presente nel path, il nuovo path non pu� essere
							// ridondante
			if (verbose) System.out.println("non c'� primo nodo");
			return false;
		}

		if (newSize + idIndex > size) { // Se la dimensione del nuovo path sommata all'indice
																	// del primo parametro in comune
																	// � maggiore della dimensione del path, allora non
																	// pu� essere ridondante
			if (verbose) System.out.println("dimensione troppo grande: size : " + size + " newSize: " + newSize + " idIndex: " + idIndex);
			return false;
		}

		// Controllo se i nodi, dopo il primo in comune, continuano ad essere in comune
		for (int i = 1; i < newSize && i + idIndex < size; i++) {

			if (nodes.get(idIndex + i).getId() == newPath.nodes.get(i).getId()) { //Se i nodi sono uguali
				continue;
			}
			if (verbose) System.out.println("trovato nodo diverso:" + nodes.get(idIndex + i).getId()+" -- " +  newPath.nodes.get(i).getId());
			return false;
		}

		if (verbose) System.out.println("Tutto uguale");
		return true;

	}
	
	private long getFirstNodeId() {
		NodeSingle node = nodes.get(0);
		long id = node.getId();
		if(id!=-1) {
			return id; 
		}
		if(nodes.size()>1) {
			return nodes.get(1).getId();
		}
		return -1;
	}
	

	private int checkParam2(String param) {
		
		if(nodes.get(0).getInput().equals(param)) {
			return 0;
		}
		return getOutputIndex(param);
	}
	
	public int size34() {
		int size = nodes.size();
		
		if(nodes.get(0).getId()==-1) {
			size+=-1;;
		}
		
		return size;
	}
	
	private void prova() {
		//System.out.println("Sono dentro prova:");
		if(nodes.size()==1 && nodes.get(0).getId()==-1) {
			//System.out.println("Nodo con id: "+ nodes.get(0).getId() + " eliminato");
			nodes.remove(0);
		}
	}
	private int checkParam(String param) {
		
	if(nodes.get(0).getInput().equals(param)) {
		return -1;
	}
	return getOutputIndex(param);
}
	
	
	public void standardizePath(String input, String fff) { //TODO da eliminare
	if(nodes.get(0).getId()!=-1) {
		System.out.println(fff);
		addStartParameter(input); 
	}
	
	
}

*
*
*	// Controlla se il path pu� restituire una soluzione alla query
	public void getSolutionPath2(Query query, NodeInfo node) {
		QuerySingle myQuery = (QuerySingle) query;
		NodeSingle myNode = (NodeSingle) node;
		getSolutionPath(myQuery,myNode);
	}
		
	private void getSolutionPath2(QuerySingle query, NodeSingle node) {
		String qInput = query.getInput();
		String qOutput = query.getOutput();
		String nOutput = node.getOutput();
		String nInput = node.getInput();

		PathArrayNode newPath = getTrunk(qInput, qOutput);

		if (newPath != null) {
			newPath.initPath(query);
			query.addPath(newPath);

		}

		if (qOutput.equals(nOutput) && (getOutputIndex( nInput) < getOutputIndex(qOutput) || getOutputIndex(qOutput)< 0 )) {
			
			newPath = getTrunk(qInput,nInput);

			if (newPath != null) {
				newPath.initPath(query);
				newPath.addEndNode(node);
				query.addPath(newPath);

			}
		}
	}
*
*
*
*
*	
	public void getComposedSolutionPath2(Query query, NodeInfo node, Path cPath) {
		
		QuerySingle myQuery = (QuerySingle) query;
		PathArrayNode myCPath = (PathArrayNode) cPath;
		NodeSingle myNode = (NodeSingle) node;
		getComposedSolutionPath(myQuery,myNode,myCPath);
	}
	
	private void getComposedSolutionPath2(QuerySingle query, NodeSingle node, PathArrayNode cPath) {
		Path newPath;
	
		boolean verbose = false;

		String qOutput = query.getOutput();
		String nOutput =  node.getOutput();
		String nInput = node.getInput();

		//if (checkParam(qOutput) >= 0)  return;// se l'output gia � presente, non cerco nulla
		if (getOutputIndex(qOutput) >= 0)  return;
		
			
		

		newPath = findComposedPath(cPath, qOutput); // Controllo se il cache path attuale mi
		// risolve la query

		if (newPath != null) {
			query.addPath(newPath);
		}

		if (qOutput.equals(nOutput) && 
				cPath.getOutputIndex(nInput) < cPath.getOutputIndex(qOutput) ) {
			if(verbose) System.out.println("Cerco con i parametri del nodo");
			newPath = findComposedPath(cPath,  nInput);

			
			if (newPath != null) {
				newPath.addEndNode(node);
				query.addPath(newPath);
				if(verbose) System.out.println("Path trovato con paremtri nodo");
				return;
			}
			if(verbose) System.out.println("Path non trovato con parametri nodo");
		}

	}

*
*/
}