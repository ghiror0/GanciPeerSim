package DSR_IOT.query;

import java.util.ArrayList;
import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.path.Path;
import DSR_IOT.path.PathArrayNode;
import peersim.core.CommonState;

public class QuerySingle implements Query { 

	// query info
	private long id;
	private String input;
	private String output;

	// Paths
	private ArrayList<Path> pathList;

	// Selected path
	private int selectedPath;
	private long selectedBestTime;
	
	//
	private long startingTime;
	
	//
	private long bestTime;

	public QuerySingle(long id, String input, String output) {
		this.id = id;
		this.input = input;
		this.output = output;
		startingTime = CommonState.getTime() ;

		pathList = new ArrayList<>();

		Path startingPath = new PathArrayNode();
	
		
		startingPath.initPath(this);
		addPath(startingPath);
	}
	
	public Query getCopy() {
		QuerySingle newQuery = new QuerySingle(id, input, output);
		newQuery.pathList.remove(0);
		Path newPath;

		
		for(Path path : pathList) {
			newPath = path.getDuplicatePath();
			newQuery.pathList.add(newPath);
		}
		
		newQuery.selectedPath = selectedPath;
		newQuery.startingTime = startingTime;
		newQuery.bestTime = bestTime;
		newQuery.selectedBestTime = selectedBestTime;
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
	
	public long getStartingTime() {
		return startingTime;
	}
	
	public long getBestTime() {
		return bestTime;
	}
	
	public void setBestTime(long bestTime) {
		this.bestTime = bestTime;
	}
	
	public long getSelectedTime() {
		return selectedBestTime;
	}

	
	

	//////////////////////////////////////////////////////////////////////////////////////

	public void findSolutionPath(NodeInfo node, Path cPath) {		
		NodeSingle myNode = (NodeSingle) node;
		findCompleteSolutionPath(myNode,cPath);
		findComposedSolutionPath(myNode,cPath);
	}
	
	private void findCompleteSolutionPath(NodeSingle node, Path cPath) {
		Path temp;
		
		temp = cPath.getSolutionPath(this);
		if(temp!=null) {
			addPath(temp);
		}
		
		temp = cPath.getSolutionPath(this, node);
		if(temp!=null) {
			addPath(temp);
		}
	}
	
	public void findComposedSolutionPath(NodeSingle node, Path cPath) {
		Path path, getPath;
		ArrayList<Path> temp = new ArrayList<>();
		int size = pathList.size();

		for (int i = 0; i < size; i++) {
			
			path = pathList.get(i);
			getPath = path.getComposedSolutionPath(this, cPath);
			
			if(getPath!=null) {
				temp.add(getPath);
			}
			
			getPath = path.getComposedSolutionPath(this, node, cPath);
			
			if(getPath!=null) {
				temp.add(getPath);
			}
			
		}
		
		for(Path addPath: temp) {
			addPath(addPath);
		}
		
		temp.clear();
	}
	


/////////////////////////////////////////////////////////////////////////

	// Inserisce un nuovo path, se non ridondante
	public void addPath(Path newPath) {

		if (redundantPath(newPath)) {
			return;
		}
		inverseRedundant(newPath);
		
		pathList.add(newPath);
	}
	

	
	public void inverseRedundant(Path newPath) {
		int size = pathList.size()-1;
		Path tempPath;
		for (int i = size; i >= 0; i--) {
			
			tempPath = pathList.get(i);
			
			if (newPath.isRedundant(tempPath)) {  //Se il path in cache è ridondante con il nuovo path da aggiungere in cache
				pathList.remove(i);
			}
		}
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
	public void mergePath(Query query) {
		for (Path path : query.getPaths()) {
			addPath(path);
		}
	}


	

	// Selezione il path migliore tramite il calcolo del tempo di esecuzione
	public int selectBestPath(String mode) {
		
		
		int size = pathList.size();

		if (size < 1) {
			return -1;
		}
		
		int best;
		
		if(mode.equals(modeAvaiability)) {
			best = selectAva(size);
		}else if(mode.equals(modeCost)) {
			best = selectCost(size);
		}else if(mode.equals(modeThroughput)) {
			best = selectTh(size);
			
		}else {
			best = selectExec(size);
		}

		
		
		return best;
		
	}
	
	private int selectAva(int size) {
		int best = -1;
		double actualBest = 0.0;//pathList.get(0).getTotalAvaiability();
		double temp;
		PathArrayNode path;

		for (int i = 0; i < pathList.size(); i++) { // per ogni path presente
			path = (PathArrayNode) pathList.get(i);
			
			if(!path.allNodeUp()) {
				continue;
			}
			
			temp =path.getTotalAvaiability();

			if (temp > actualBest || (temp == actualBest && best!=-1 && path.getSize() < pathList.get(best).getSize())) { 
				// Se il tempo di esecuzione è minore del minore attuale
				// Oppure se i tempi sono uguali ma la lunghezza è minore
				// Il path diventa il nuovo best
				best = i;
				actualBest = temp;
			}
		}
		return best;
	}
	
	private int selectCost(int size) {
		int best = -1;
		double actualBest = Long.MAX_VALUE;//pathList.get(0).getTotalCost();
		double temp;
		PathArrayNode path;
		for (int i = 0; i < pathList.size(); i++) { // per ogni path presente
			path = (PathArrayNode) pathList.get(i);
			
			if(!path.allNodeUp()) {
				continue;
			}
			
			temp =path.getTotalCost();

			if (temp < actualBest || (temp == actualBest && best!=-1 && pathList.get(i).getSize() < pathList.get(best).getSize())) { 
				// Se il tempo di esecuzione è minore del minore attuale
				// Oppure se i tempi sono uguali ma la lunghezza è minore
				// Il path diventa il nuovo best
				best = i;
				actualBest = temp;
			}
		}
		return best;
	}
	
	private int selectExec(int size) {
		int best = -1;
		long actualBest = Long.MAX_VALUE;//pathList.get(0).getTotalExecTime();
		long temp;
		PathArrayNode path;
		for (int i = 0; i < pathList.size(); i++) { // per ogni path presente
			path = (PathArrayNode) pathList.get(i);
			
			if(!path.allNodeUp()) {
				continue;
			}
			
			temp =path.getTotalExecTime();

			if (temp < actualBest || (temp == actualBest && best!=-1 && pathList.get(i).getSize() < pathList.get(best).getSize())) { 
				// Se il tempo di esecuzione è minore del minore attuale
				// Oppure se i tempi sono uguali ma la lunghezza è minore
				// Il path diventa il nuovo best
				best = i;
				actualBest = temp;
			}
		}
		selectedBestTime = actualBest;
		return best;
	}
	
	private int selectTh(int size) {
		int best = -1;
		double actualBest = 0; //pathList.get(0).getTotalThroughput();
		double temp;
		PathArrayNode path;
		for (int i = 0; i < pathList.size(); i++) { // per ogni path presente
			path = (PathArrayNode) pathList.get(i);
			
			if(!path.allNodeUp()) {
				continue;
			}
			
			temp =path.getTotalThroughput();

			if (temp > actualBest || (temp == actualBest && best!=-1 && pathList.get(i).getSize() < pathList.get(best).getSize())) { 
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
		ArrayList<Path> temp = new ArrayList<>();
		int size = pathList.size();
		Path newPath;
		
		for (int i = 0; i < size; i++) {
					
			newPath = pathList.get(i).analyzePath(node, liveOptimization);
			
			if(newPath!=null) {
				//addPath(newPath);
				temp.add(newPath);
			}
				
		}	
		
		for(Path path: temp) {
			addPath(path);
		}
		temp.clear();
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

		Path path;
		int size = pathList.size();

		for (int i = size-1 ; i >= 0; i--) { 
			path =  pathList.get(i);
			if (path.checkRemove(this)) {
				pathList.remove(i);
			}
		}

	}

	
	public void removePaths(int start, int delete) {  					//Seleziona "delete" path da eliminare, mantenendo i migliori
		ArrayList<Path> temp = new ArrayList<>();
		temp.addAll(pathList.subList(start, pathList.size()));
		
		ArrayList<Long> execTime = new ArrayList<>();
		
		for(Path path : temp) {
			execTime.add(path.getTotalExecTime());
		}
		
		for(int i = 0; i < pathList.size() - start - delete;i++) { 		//Rimuovo dai path da eliminare i path migliori
			
			int best = 0;
			double actualBest = execTime.get(0);
			double tempTime;

			for (int j = 1; j < execTime.size(); j++) { 					//Cerco il path migliore attualmente
				tempTime = execTime.get(j);

				if (tempTime < actualBest || (tempTime == actualBest && temp.get(j).getSize() < temp.get(best).getSize())) { 				
					best = j;
					actualBest = tempTime;
				}
			}
			temp.remove(best);
			execTime.remove(best);
		}

		pathList.removeAll(temp);
		
	}
	
	public int checkQuery() {
		PathArrayNode myPath;
		int id;
		for(Path path: pathList) {
			myPath = (PathArrayNode) path;
			id=myPath.checkPath();
			if(id>=0) return id;
		}
		return -1;
	}
	

	///////////////////////////////////////////////////////////////// PRINT
	public String getInfo() {
		return "Query ID: " + id + "\tInput: " + input + "\tOutput: " + output + "\n\n";
	}

	public void printInfo() {
		System.out.println("Query ID: " + id + "\tInput: " + input + "\tOutput: " + output + "\n\n");
	}
	
	public String getResultInfo(String selectMode) {
		String bestInfo = "Query ID: " + id +  "\nNumero path: " + pathList.size() +  "\nSelectedPath: " + selectedPath + "\n";
		String valueInfo;
		if(selectedPath >=0 ) {
			if(selectMode.equals(modeAvaiability)) {
				valueInfo= "Avaiability: " + pathList.get(selectedPath).getTotalAvaiability();
			}else if(selectMode.equals(modeCost)) {
				valueInfo= "Cost: " + pathList.get(selectedPath).getTotalCost();
			}else if(selectMode.equals(modeThroughput)) {
				valueInfo= "Throughput: " + pathList.get(selectedPath).getTotalThroughput();
			}else {
				valueInfo= "ExecutionTime: " + pathList.get(selectedPath).getTotalExecTime();
			}
			return bestInfo + valueInfo;
			
		}
		return  "Query ID: " + id +  "\nNumero path: " + pathList.size() +  "\nSelectedPath: NONE";
	}

	public void printPathsInfo() {
		for (Path path : pathList) {
			path.printPath();
			System.out.println("\n");
		}
	}

	
	
	/*private void findComposedSolutionPath2(NodeSingle node, Path cPath) {
	Path path;
	int size = pathList.size();

	for (int i = 0; i < size; i++) {
		path = pathList.get(i);
		path.getComposedSolutionPath(this, node, cPath);
	}
}*/
	
	
	// Selezione il path migliore tramite il calcolo del tempo di esecuzione
	public int selectBestPath2() { //TODO da eliminare
		
		
		int size = pathList.size();

		if (size < 1) {
			return -1;
		}

		int best = 0;
		long actualBest = pathList.get(0).getTotalExecTime();
		long temp;

		for (int i = 1; i < pathList.size(); i++) { // per ogni path presente
			temp = pathList.get(i).getTotalExecTime();

			if (temp < actualBest || (temp == actualBest && pathList.get(i).getSize() < pathList.get(best).getSize())) { 
				// Se il tempo di esecuzione è minore del minore attuale
				// Oppure se i tempi sono uguali ma la lunghezza è minore
				// Il path diventa il nuovo best
				best = i;
				actualBest = temp;
			}
		}
		selectedBestTime = actualBest;
		return best;
	}
	
}
