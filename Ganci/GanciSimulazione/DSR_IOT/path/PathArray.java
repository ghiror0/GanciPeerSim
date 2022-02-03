package DSR_IOT.path;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.query.Query;
import DSR_IOT.query.QuerySingle;

public class PathArray implements Path {
	private ArrayList<String> parameters; // nodeID 'index' --> input: 'index', output 'index+1'
	private ArrayList<Long> nodesId;
	private ArrayList<Long> executionTime;
	
	//Value info
	//operazione: somma o moltiplicazione
	//max o min: 
	//tipo: double o long
	//private ArrayList<Object> value = new ArrayList<>();

	public PathArray() {
		parameters = new ArrayList<>();
		nodesId = new ArrayList<>();
		executionTime = new ArrayList<>();
	}
	
	public void initPath(Query query) {
		QuerySingle myQuery = (QuerySingle) query;
		addStartParameter( myQuery.getInput());
	}
	

	/////////////////////////////////////////////////////////////// GET-SET//////////////////////////////////
	/*public List<Long> getIds() {
		return nodesId;
	}*/

	private int getParamIndex(String param) {
		return parameters.indexOf(param);
	}

	public int getNodeIndex(long nodeId) {
		return nodesId.indexOf(nodeId);
	}

	private String getParamByIndex(int index) {
		return parameters.get(index);
	}

	public int getSize() {
		return nodesId.size();
	}

	/////////////////////////////////////////////////////////// ADD
	/////////////////////////////////////////////////////////// ///////////////////////////////////////////////////

	private void addExecTime(List<Long> time) {
		for (long newTime : time) {
			executionTime.add(newTime);
		}
	}

	private void removeExecTime(int start, int end) { // Rimuove i tempi di esecuzione dei nodi da end a start compresi
		for (int i = end; i >= start; i--) {
			executionTime.remove(i);
		}
	}

	// TESTED
	/*
	 * Metodo per aggiungere un intero path: rimuove il primo input del path da
	 * aggiungere poichè deve corrispondere all'ultimo output del path presente
	 */
	public void addPath(Path Path) {
		PathArray newPath = (PathArray) Path;
		newPath.parameters.remove(0);
		parameters.addAll(newPath.parameters);
		nodesId.addAll(newPath.nodesId);
		addExecTime(newPath.executionTime);
	}

	/**
	 * TESTED
	 * 
	 * @param nodeId
	 * @param output
	 * @param index: posizione in cui si vuoi inserire il nodo
	 * 
	 *               Aggiunge un nodo, con relativo output, in posizione 'index'
	 */
	private void addNode(NodeSingle node, int index) {
		nodesId.add(index, node.getId());
		parameters.add(index + 1, node.getOutput());
		executionTime.add(index, node.getExecTime());
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
		nodesId.add(myNode.getId());
		parameters.add(myNode.getOutput());
		executionTime.add(myNode.getExecTime());
	}

	/**
	 * 
	 * @param input Parametro iniziale disponibile dalla query utente
	 * 
	 *              Inserisce il parametro disponibile fornito dalla query utente
	 */
	public void addStartParameter(String input) {
		parameters.add(input);
	}
	
	public void addStartParameter(Query query) {
		QuerySingle myQuery = (QuerySingle) query;
		parameters.add(myQuery.getInput());
	}

	/////////////////////////////////////////////////////////// GET
	/////////////////////////////////////////////////////////// //////////////////////////////////////////////////////

	// TESTED
	/**
	 * 
	 * @return Ultimo output disponibile
	 */
	public String getLastOutput() {
		return parameters.get(parameters.size() - 1);
	}

	public double getExecTime(int start, int end) {
		Double total = 0.0;

		for (double eTime : executionTime.subList(start, end)) {
			total += eTime;
		}

		return total;
	}

	public long getTotalExecTime() {
		long total = 0;

		for (double eTime : executionTime) {
			total += eTime;
		}

		return total;
	}
	public double getTotalAvaiability() {
		return 0.0;
	}
	
	public long getTotalCost() {
		return 0;
	}
	public double getTotalThroughput() {
		return 0.0;
	}
	// TESTED
	/**
	 * Fornisce una copia del path
	 * 
	 * @return Copia del path
	 */
	public PathArray getDuplicatePath() {

		PathArray newPath = new PathArray();
		newPath.nodesId.addAll(nodesId);
		newPath.parameters.addAll(parameters);
		newPath.addExecTime(executionTime);
		return newPath;
	}

	// TESTED
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
	public PathArray getAlternativePath(NodeInfo node, int index) {
		PathArray newPath = new PathArray();
		newPath.nodesId.addAll(nodesId.subList(0, index));
		newPath.parameters.addAll(parameters.subList(0, index + 1));
		newPath.addExecTime(executionTime.subList(0, index));

		newPath.addEndNode(node);
		return newPath;
	}
	
	public boolean allNodeUp() {
		return true;
	}

	// TESTED
	/**
	 * 
	 * @param start, compreso nel trunk
	 * @param end,   escluso dal trunk
	 * @return Ritorna una copia di una parte del path
	 */
	public PathArray getTrunk(int start, int end) {
		PathArray newPath = new PathArray();
		newPath.nodesId.addAll(nodesId.subList(start, end));
		newPath.parameters.addAll(parameters.subList(start, end + 1));
		newPath.addExecTime(executionTime.subList(start, end));
		return newPath;
	}

	private PathArray getTrunk(String start, String end) {

		int inputIndex = getParamIndex(start);
		int outputIndex = getParamIndex(end);

		if (outputIndex > 0 && inputIndex >= 0 && (inputIndex < outputIndex)) {
			return getTrunk(inputIndex, outputIndex);
		}

		return null;
	}

	// Cerca una composizione di path che possa unire
	private PathArray findComposedPath(PathArray cPath, String qOutput) {

		int outputIndex = cPath.getParamIndex(qOutput);
		int pInputIndex;

		if (outputIndex <= 0) {
			return null;
		}

		for (int i = outputIndex; i >= 0; i--) { // Controllo tutti i parametri del path per vedere se
													// presente un input di quelli presenti nel cache path

			pInputIndex = getParamIndex(cPath.getParamByIndex(i));

			if (pInputIndex >= 0) { // Input trovato: mi creo il path usando l'inizio del path del mex e la fine del
									// path in cache -->Escludo il caso index = 0 perche prenderebbe solo un pezzo
									// del path cPath, già considerato in altri metodi

				if(pInputIndex==0) return null;	//Fare >= se si vuole includere la possibilità di prendere anche i path completamente in cPath
				PathArray newPath = getTrunk(0, pInputIndex);
				newPath.addPath(cPath.getTrunk(i, outputIndex));
				return newPath;
			}
		}
		return null;

	}

	// Controlla se il path può restituire una soluzione alla query
	public Path getSolutionPath(Query query, NodeInfo node) {
		QuerySingle myQuery = (QuerySingle) query;
		NodeSingle myNode = (NodeSingle) node;
		return getSolutionPath(myQuery,myNode);
	}
	
	public Path getSolutionPath(Query query) {
		
		return null;
	}
	
	
	private Path getSolutionPath(QuerySingle query, NodeSingle node) {
		String qInput = query.getInput();
		String qOutput = query.getOutput();
		String nOutput = node.getOutput();
		String nInput = node.getInput();

		PathArray newPath = getTrunk(qInput, qOutput);

		if (newPath != null) {
			query.addPath(newPath);

		}

		if (qOutput.equals(nOutput) && getParamIndex( nInput) < getParamIndex(qOutput)) {
			newPath = getTrunk(qInput,nInput);

			if (newPath != null) {
				newPath.addEndNode(node);
				query.addPath(newPath);

			}
		}
		return null;
	}

	public Path getComposedSolutionPath(Query query, NodeInfo node, Path cPath) { //TODO SOLO COMPATIBILITA
		
		QuerySingle myQuery = (QuerySingle) query;
		PathArray myCPath = (PathArray) cPath;
		NodeSingle myNode = (NodeSingle) node;
		getComposedSolutionPath(myQuery,myNode,myCPath);
		return null;
	}
	public Path getComposedSolutionPath(Query query,  Path cPath) { //TODO finta
		return null;
	}
	
	private void getComposedSolutionPath(QuerySingle query, NodeSingle node, PathArray cPath) {
		Path newPath;

		String qOutput = query.getOutput();
		String nOutput =  node.getOutput();
		String nInput = node.getInput();

		if (getParamIndex(qOutput) >= 0) { // se l'output gia è presente, non cerco nulla
			return;
		}

		newPath = findComposedPath(cPath, qOutput); // Controllo se il cache path attuale mi
		// risolve la query

		if (newPath != null) {
			query.addPath(newPath);
		}

		if (qOutput.equals(nOutput) && cPath.getParamIndex(nInput) < cPath.getParamIndex(qOutput)) {

			newPath = findComposedPath(cPath,  nInput);

			if (newPath != null) {
				newPath.addEndNode(node);
				query.addPath(newPath);
			}
		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// TESTED
	/* Controlla se "newPath" è ridondante nel path presente */
	public boolean isRedundant(Path Path) {
		PathArray newPath = (PathArray) Path;
		int idIndex;

		if (newPath.nodesId.isEmpty()) { // Se non ci sono nodi, il path è considerato sempre ridondante
			return true;
		}

		idIndex = nodesId.indexOf(newPath.nodesId.get(0));

		if (idIndex < 0) { // Se il primo nodo non è presente nel path, il nuovo path non può essere
							// ridondante
			return false;
		}

		if (newPath.nodesId.size() + idIndex > nodesId.size()) { // Se la dimensione del nuovo path sommata all'indice
																	// del primo parametro in comune
																	// è maggiore della dimensione del path, allora non
																	// può essere ridondante
			return false;
		}

		// Controllo se i nodi, dopo il primo in comune, continuano ad essere in comune
		for (int i = 1; i < newPath.nodesId.size() && i + idIndex < nodesId.size(); i++) {

			if (nodesId.get(idIndex + i).equals(newPath.nodesId.get(i))) { // Se i parametri sono uguali, passo ai
																			// prossimi
				continue;
			}
			return false;
		}

		return true;

	}
	
	public boolean isEquals(Path newPath) {
		PathArray myPath = (PathArray) newPath;
		if(nodesId.size() != myPath.nodesId.size()) {
			return false;
		}
		for(int i = 0; i <nodesId.size() ;i++) {
			if(nodesId.get(i) != myPath.nodesId.get(i)) {
				return false;
			}
		}
	
		return true;
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
		nodesId.removeAll(nodesId.subList(startIndex, endIndex));
		parameters.removeAll(parameters.subList(startIndex + 1, endIndex + 1));
		removeExecTime(startIndex, endIndex - 1);

		// Aggiungo il nuovo nodo nella posizione desiderata
		addNode(node, startIndex);
	}

	public Path analyzePath(NodeInfo node, boolean liveOptimization) {

		NodeSingle myNode = (NodeSingle) node;
		String lastOutput;
		int inputIndex;
		int outputIndex;

		String input =  myNode.getInput();
		String output =  myNode.getOutput();

		lastOutput = getLastOutput(); // Prendo l'ultimo output del path
		inputIndex = getParamIndex(input); // Prendo l'indice del path del parametro di input, se assente -1
		outputIndex = getParamIndex(output); // Prendo l'indice del path del parametro di output, se assente -1

		if (inputIndex >= 0) { // Posso utilizzare il servizio --> l'input è presente nel path

			if (outputIndex >= 0) { // Output già presente

				if (inputIndex < outputIndex && liveOptimization) { // se l'input è presente nel path prima dell'output,
																	// allora posso ottimizzare
					optimizePath(myNode, inputIndex, outputIndex); // il -1 serve perche i metodi considerano l'indice
																	// dell'id non del parametro
				}

			} else { // Output non presente

				if (input.equals(lastOutput)) { // Se l'input del servizio è l'ultimo degli output del path,
												// possoinserire le info del nodo in coda
					addEndNode(node);

				} else { // Se l'input è presente nel mezzo del path, creo un secondo percorso e lo
							// aggiungo al messaggio
					return getAlternativePath(node, inputIndex);
				}
			}
		}
		return null;
	}

	// TESTED
	// Controlla se il nuovo nodo ha un tempo di esecuzione minore dei nodi che
	// andrebbe a sostituire, in caso positivo modifica il path
	public void optimizePath(NodeSingle node, int start, int end) {
		if (getExecTime(start, end) >= node.getExecTime()) {
			modifyPath(node, start, end);
		}
	}

	
	
	////////////////////////////////////////////////////////CHECK///////////////////////////////////////////////
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

	public void printPath() {
		printPath(System.out);
	}

	public void printPath(PrintStream ps) {
		ps.print("\nParam:\n\t");
		for (String param : parameters) {
			ps.print(param + "->\t");
		}
		ps.print(";;");

		ps.print("\nNodes id:\n\t");
		for (long id : nodesId) {
			ps.print(id + "->\t");
		}
		ps.print(";;");

		ps.print("\nExecTime:\n\t");
		for (long time : executionTime) {
			ps.print(time + "\t");
		}
		ps.print(";;");

		ps.println("\n");
	}

}
