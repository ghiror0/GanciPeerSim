package DSR_IOT.query;

import java.util.ArrayList;
import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.path.Path;
import DSR_IOT.path.PathArray;

public class QuerySingle implements Query { 

	// query info
	private long id;
	private String input;
	private String output;

	// Paths
	private ArrayList<Path> pathList;

	// Selected path
	private int selectedPath;

	public QuerySingle(long id, String input, String output) {
		this.id = id;
		this.input = input;
		this.output = output;

		pathList = new ArrayList<>();

		PathArray startingPath = new PathArray();
		startingPath.addStartParameter(input);
		addPath(startingPath);
	}

	public Query getCopy() {
		QuerySingle newQuery = new QuerySingle(id, input, output);
		newQuery.pathList.remove(0);
		newQuery.pathList.addAll(this.pathList);
		return newQuery;
	}

	////////////////////////////////////////////////////////////////// GET_SET
	public long getId() {
		return id;
	}

	public String getInput() {
		return input;
	}

	public String getOutput() {
		return output;
	}

	public int getSelectedPath() {
		return selectedPath;
	}

	public void setSelectedPath(int selected) {
		selectedPath = selected;
	}

	public List<Path> getPaths() {
		return pathList;
	}

	//////////////////////////////////////////////////////////////////////////////////////

	public void findSolutionPath(NodeInfo node, Path cPath) {
		PathArray myPath = (PathArray) cPath;			
		NodeSingle myNode = (NodeSingle) node;
		findCompleteSolutionPath(myNode,myPath);
		findComposedSolutionPath(myNode,myPath);
	}
	
	private void findCompleteSolutionPath(NodeSingle node, PathArray cPath) {
		cPath.getSolutionPath(this, node);
	}
	
	private void findComposedSolutionPath(NodeSingle node, PathArray cPath) {
		PathArray path;
		int size = pathList.size();

		for (int i = 0; i < size; i++) {
			path = (PathArray) pathList.get(i);
			path.getComposedSolutionPath(this,node, cPath);
		}
	}

/////////////////////////////////////////////////////////////////////////

	// Inserisce un nuovo path, se non ridondante
	public void addPath(Path newPath) {

		if (redundantPath(newPath)) {
			return;
		}
		pathList.add(newPath);
	}
	
	// TESTED
	// Controllo che il nuovo path non sia ridondante (P.S. non controllo se il path
	// gia presente è ridodante con il nuovo)
	public boolean redundantPath(Path newPath) {
		for (Path path : pathList) {
			if (path.isRedundant(newPath)) {
				return true;
			}
		}
		return false;
	}

	// Unisce la lista dei path dl nuovo messaggio con quelli presenti
	public void meshPath(Query query) {
		for (Path path : query.getPaths()) {
			addPath(path);
		}
	}

	// Selezione il path migliore tramite il calcolo del tempo di esecuzione
	public int selectBestPath() {
		int size = pathList.size();

		if (size < 1) {
			return -1;
		}

		int best = 0;
		double actualBest = pathList.get(0).getTotalExecTime();
		double temp;

		for (int i = 1; i < pathList.size(); i++) { // per ogni path presente
			temp = pathList.get(i).getTotalExecTime();

			if (temp < actualBest || (temp == actualBest && pathList.get(i).size() < pathList.get(best).size())) { 
				// Se il tempo di esecuzione è minore del minore attuale
				// Oppure se i tempi sono uguali ma la lunghezza è minore
				// Il path diventa il nuovo best
				best = i;
				actualBest = temp;
			}
		}
		return best;
	}

	public void analyzeNode(NodeInfo node, boolean liveOptimization) {
		
		int size = pathList.size();
		Path newPath;
		
		for (int i = 0; i < size; i++) {
					
			newPath = pathList.get(i).analyzePath(node, liveOptimization);
			
			if(newPath!=null) {
				addPath(newPath);
			}
				
		}	
	}
	
	public boolean checkFind() {
		for (Path path : pathList) {
			if (path.checkFind(this)) {
				return true;
			}
		}
		return false;
	}

	// TESTED
	// Rimuove i path che non rispondono alla query
	public void clearUnused() {

		PathArray path;
		int size = pathList.size();

		for (int i = size - 1; i >= 0; i--) { 
			path = (PathArray) pathList.get(i);
			if (!path.getLastOutput().equals(output)) {
				pathList.remove(path);
			}
		}

	}

	///////////////////////////////////////////////////////////////// PRINT
	public String getInfo() {
		return "Query ID: " + id + "\tInput: " + input + "\tOutput: " + output + "\n\n";
	}

	public void printInfo() {
		System.out.println("Query ID: " + id + "\tInput: " + input + "\tOutput: " + output + "\n\n");
	}

	public void printPathsInfo() {
		for (Path path : pathList) {
			path.printPath();
			System.out.println("\n");
		}
	}

}
