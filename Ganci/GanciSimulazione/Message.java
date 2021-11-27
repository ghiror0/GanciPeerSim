

import java.util.ArrayList;

public class Message {
	static int globalId = 0;
	private int messageId;
	private int starterNodeId;
	private int senderId;
	private boolean find = false;
	
	
	String queryInput;
	String queryOutput;
	ArrayList<PathInfo> paths;
	
	private int selectedPath = -1;

	public Message(int id, String input, String output) {
		messageId = globalId++;
		starterNodeId = id;
		senderId = id;
		queryInput = input;
		queryOutput = output;
		paths = new ArrayList<PathInfo>();
		PathInfo startingPath = new PathInfo();
		startingPath.addStartParameter(input);
		addPath(startingPath);
	}
	
	public Message() {
	}
	


	
	///////////////////////////////////////////////////////////GET_SET ///////////////////////////////////

	public void setSenderId(int id) {
		senderId = id;
	}

	public int getSenderId() {
		return senderId;
	}
	
	public void setMessageId(int id) {
		messageId = id;
	}
	
	public int getMessageId() {
		return messageId;
	}
	
	public void setStarterNodeId(int id) {
		starterNodeId = id;
	}
	
	public int getStarterNodeId() {
		return starterNodeId;
	}

	public void setSelectedPath(int id) {
		selectedPath = id;
	}
	
	public int getSelectedPath() {
		return selectedPath;
	}
	
	public void setFind(boolean find) {
		this.find = find;
	}
	
	public boolean getFind() {
		return find;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//Unisce la lista dei path dl nuovo messaggio con quelli presenti
	public void meshPath(Message mex) {
		for (PathInfo path : mex.paths) {
			addPath(path);
		}
	}

	//Inserisce un nuovo path, se non ridondante
	public boolean addPath(PathInfo newPath) {
		if (redundantPath(newPath)) {
			return false;
		}
		paths.add(newPath);
		return true;
	}
	
	//Selezione il path migliore tramite il calcolo del tempo di esecuzione
	public int selectBestPath() {
		int size = paths.size();
		
		if(size<1) {
			return -1;
		}
		
		int best = 0;
		double actualBest = paths.get(0).getTotalExecTime();
		double temp;
		
		for(int i = 1; i < paths.size();i++) { //per ogni path presente
			temp = paths.get(i).getTotalExecTime();
			
			if(temp< actualBest) { //Se il tempo di esecuzione � minore del minore attuale
				best = 1;
				actualBest = temp;
				
			}else if(temp == actualBest) { //Se il tempo di esecuzione � uguale al minore attuale
				if(paths.get(i).idSize()<paths.get(best).idSize()) { //Se il nuovo path contiene meno nodi, diventa il nuovo minimo
					best = 1;
					actualBest = temp;
				}
			}
		}
		return best;
	}


	//TESTED
	//Controllo che il nuovo path non sia ridondante (P.S. non controllo se il path gia presente � ridodante con il nuovo)
	public boolean redundantPath(PathInfo newPath) {
		for (PathInfo path : paths) {
			if (path.isRedundant(newPath)) {
				return true;
			}
		}
		return false;
	}

	public Message duplicateMex() {
		Message newMex = new Message();

		newMex.messageId = messageId;	
		newMex.starterNodeId = starterNodeId;
		
		newMex.queryInput = queryInput;
		newMex.queryOutput = queryOutput;
		
		newMex.find = find;
		newMex.selectedPath = selectedPath;

		
		newMex.paths = new ArrayList<PathInfo>();
		
		
		for (PathInfo path : paths) {

			PathInfo copy = path.getDuplicatePath();

			newMex.paths.add(copy);
		}

		return newMex;
	}

	
	//TESTED
	//Rimuove i path che non rispondono alla query 
	public void clearUnused() {
		ArrayList<PathInfo> actualPaths = new ArrayList<PathInfo>();
		actualPaths.addAll(paths);
		for (PathInfo path : actualPaths) {
			if (!path.getLastOutput().equals(queryOutput)) {
				paths.remove(path);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////PRINT//////////////////////////////////////////////////////////////////////

	public void mexInfo() {
		
		System.out.println("Id message: " + messageId);
		System.out.println(
				"Input: " + queryInput + "\tOutput: " + queryOutput + "\tID Nodo iniziale: " + starterNodeId + "\n\n");
	}

	

	public void printInfo() {
		mexInfo();
		System.out.println("Paths:\n");

		for (PathInfo path : paths) {
			path.printPath();
			System.out.println("\n");
		}
	}
	
	public void printDettailsInfo2() {
		mexInfo();
		if (find) {
			System.out.println("CONCLUSO");
		}
		System.out.println("\t\t\tI path presenti sono:");
		System.out.print("\t\t\t");
		for (PathInfo path : paths) {
			path.printParam2();
			System.out.print("\n\t\t\t");
			path.printIds2();
			System.out.print("\n\t\t\t");
		}
		System.out.print("\n\n");
	}

}
