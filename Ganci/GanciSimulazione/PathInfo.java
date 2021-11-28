

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class PathInfo {
	private ArrayList<String> parameters; // nodeID 'index' --> input: 'index', output 'index+1'
	private ArrayList<Integer> nodesId;
	private ArrayList<Integer> executionTime;

	public PathInfo() {
		parameters = new ArrayList<>();
		nodesId = new ArrayList<>();
		executionTime = new ArrayList<>();
	}
	
	
	///////////////////////////////////////////////////////////////GET-SET//////////////////////////////////
	public List<Integer> getIds(){
		return nodesId;
	}

	public int getParamIndex(String param) {
		return parameters.indexOf(param);
	}

	public int getNodeIndex(int nodeId) {
		return nodesId.indexOf(nodeId);
	}

	public String getParamByIndex(int index) {
		return parameters.get(index);
	}
	
	public int idSize() {
		return nodesId.size();
	}
	
	/////////////////////////////////////////////////////////// ADD ///////////////////////////////////////////////////

	public void addExecTime(List<Integer> time) {
		for(int newTime: time) {
			executionTime.add(newTime);
		}
	}
	
	public void removeExecTime(int start, int end) { //Rimuove i tempi di esecuzione dei nodi da end a start compresi
		for(int i = end; i >= start;i--) {
			executionTime.remove(i);
		}
	}
	
	//TESTED
	/*Metodo per aggiungere un intero path: rimuove il primo input del path da aggiungere poichè deve corrispondere all'ultimo output del path presente*/
	public void addPath(PathInfo newPath) {
		newPath.parameters.remove(0);
		parameters.addAll(newPath.parameters);
		nodesId.addAll(newPath.nodesId);
		addExecTime(newPath.executionTime);
	}

	/**TESTED
	 * 
	 * @param nodeId
	 * @param output
	 * @param index: 	posizione in cui si vuoi inserire il nodo
	 * 
	 *               	Aggiunge un nodo, con relativo output, in posizione 'index'
	 */
	private void addNode(int nodeId, String output, int execTime, int index) {
		nodesId.add(index, nodeId);
		parameters.add(index + 1, output);
		executionTime.add(index, execTime);
	}
	
	

	/**
	 * 
	 * @param nodeId
	 * @param output
	 * 
	 *               Aggiunge un nodo, con relativo output, alla fine del path
	 */
	public void addEndNode(int nodeId, String output, int execTime) {
		nodesId.add(nodeId);
		parameters.add(output);
		executionTime.add(execTime);
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

	
	/////////////////////////////////////////////////////////// GET //////////////////////////////////////////////////////
	
	//TESTED
	/**
	 * 
	 * @param start, compreso nel trunk
	 * @param end, escluso dal trunk
 	 * @return Ritorna una copia di una parte del path
	 */
	public PathInfo getTrunk(int start, int end) {
		PathInfo newPath = new PathInfo();
		newPath.nodesId.addAll(nodesId.subList(start, end));
		newPath.parameters.addAll(parameters.subList(start, end + 1));
		newPath.addExecTime(executionTime.subList(start, end));
		return newPath;
	}

	//TESTED
	/**
	 * 
	 * @return Ultimo output disponibile
	 */
	public String getLastOutput() {
		return parameters.get(parameters.size() - 1);
	}

	public double getExecTime(int start, int end) {
		Double total = 0.0;
		
		for(double eTime: executionTime.subList(start, end)) {
			total+=eTime;
		}
		
		return total;
	}
	
	public double getTotalExecTime() {
		Double total = 0.0;
		
		for(double eTime: executionTime) {
			total+=eTime;
		}
		
		return total;
	}
	
	//TESTED
	/**
	 * Fornisce una copia del path
	 * 
	 * @return Copia del path
	 */
	public PathInfo getDuplicatePath() {
		PathInfo newPath = new PathInfo();
		newPath.nodesId.addAll(nodesId);
		newPath.parameters.addAll(parameters);
		newPath.addExecTime(executionTime);
		return newPath;
	}
	
	//TESTED
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
	public PathInfo getAlternativePath(int nodeId, String output, int execTime, int index) {
		PathInfo newPath = new PathInfo();
		newPath.nodesId.addAll(nodesId.subList(0, index));
		newPath.parameters.addAll(parameters.subList(0, index + 1));
		newPath.addExecTime(executionTime.subList(0, index));
		
		newPath.addEndNode(nodeId, output, execTime);
		return newPath;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//TESTED
	/*Controlla se "newPath" è ridondante nel path presente */
	public boolean isRedundant(PathInfo newPath) {
		

		int idIndex;
		
		if (newPath.nodesId.isEmpty()) { //Se non ci sono nodi, il path è considerato sempre ridondante
			return true;
		}

		idIndex = nodesId.indexOf(newPath.nodesId.get(0));

		if (idIndex < 0) {  //Se il primo nodo non è presente nel path, il nuovo path non può essere ridondante
			return false;
		}
		
		if(newPath.nodesId.size()+idIndex>nodesId.size()) { // Se la dimensione del nuovo path sommata all'indice del primo parametro in comune 
																	//è maggiore della dimensione del path, allora non può essere ridondante
			return false;
		}

		//Controllo se i nodi, dopo il primo in comune, continuano ad essere in comune
		for (int i = 1; i < newPath.nodesId.size() && i + idIndex < nodesId.size(); i++) {

			if (nodesId.get(idIndex + i).equals(newPath.nodesId.get(i))) { //Se i parametri sono uguali, passo ai prossimi
				continue;
			}
			return false;
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
	 * 		Elimina un segmento del path inserendoci un nodo
	 *   	sostitutivo
	 */
	public void modifyPath(int nodeId, String output, int execTime, int startIndex, int endIndex) { //TODO chiamarlo shortenPath()
		//rimuovo la parte da eliminare
		nodesId.removeAll(nodesId.subList(startIndex, endIndex));
		parameters.removeAll(parameters.subList(startIndex+1, endIndex+1));
		removeExecTime(startIndex,endIndex-1);
		
		
		//Aggiungo il nuovo nodo nella posizione desiderata
		addNode(nodeId, output, execTime, startIndex);
	}
	



	
	
	
	
	
	
	
	//////////////////////////////////////////////////////PRINT///////////////////////////////////////////////////////////
	
	



	public void printPath() {
		printPath(System.out);
	}
	
	public void printPath(PrintStream ps) {
		ps.print("\nParam:\n\t");
		for (String param : parameters) {
			ps.print(param + "->\t");
		}
		
		ps.print("\nNodes id:\n\t");
		for (int id : nodesId) {
			ps.print(id + "->\t");
		}
		
		ps.print("\nExecTime:\n\t");
		for (int time : executionTime) {
			ps.print(time + "\t");
		}
		
		ps.println("\n");
	}

	
	
	
	
	
	
	///////////////////////////////////////////////////////NON USATE //////////////////////////////////////////////////////
	/*
	 * public ArrayList<String> getParams() { return parameters; } public
	 * ArrayList<Integer> getIds() { return nodesId; }
	 */

	/*
	 * private void setPath(ArrayList<Integer> nodesId,ArrayList<String> parameters)
	 * { this.nodesId = nodesId; this.parameters = parameters; }
	 */
	
	
	public void getNodeInfo(int index) {
		System.out.println("Il nodo di id: " + nodesId.get(index) + " possiede i seguenti parametri: " + "Input--> "
				+ parameters.get(index) + "\tOutput-->" + parameters.get(index + 1));
	}


	
	public void printParam2() {
		for (String param : parameters) {
			System.out.print(param + "->\t");
		}
	}

	public void printIds2() {
		for (int id : nodesId) {
			System.out.print(id + "->\t");
		}
	}
	
	

	public boolean isRedundantWithParam(PathInfo newPath) {

		int paramIndex;
		int idIndex;
		if (newPath.nodesId.size() == 0) {
			return true;
		}

		paramIndex = parameters.indexOf(newPath.parameters.get(0));
		idIndex = nodesId.indexOf(newPath.nodesId.get(0));

		if (paramIndex < 0 || paramIndex != idIndex) {
			// System.out.println("Il path non possiede il primo parametro con lo stesso
			// nodo");
			return false;
		}

		for (int i = 1; i < newPath.nodesId.size() && i + paramIndex < nodesId.size(); i++) {
			if (parameters.get(paramIndex + i).equals(newPath.parameters.get(i))) { // Se i parametri sono uguali
				if (nodesId.get(paramIndex + i).equals(newPath.nodesId.get(i))) {
					continue;
				}
			}
			// System.out.println("Il path ha un nodo non uguale");
			return false;
		}

		if (paramIndex + newPath.nodesId.size() < nodesId.size()) {
			if (!parameters.get(paramIndex + newPath.nodesId.size())
					.equals(newPath.parameters.get(newPath.nodesId.size()))) {
				// System.out.println("L'ultimo nodo no è uguale");
				return false;
			}
		} else {
			return false;
		}

		return true;

	}


	
	/**
	 * 
	 * @param path
	 * @return true se i 2 path sono equivalenti
	 */
	public boolean isEquals(PathInfo path) {
		if (this.parameters.equals(path.parameters)) {
			if (this.nodesId.equals(path.nodesId)) {
				return true;
			}
		}

		return false;
	}

}
