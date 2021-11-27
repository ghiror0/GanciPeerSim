
import java.util.ArrayList;

public class Query {  //TODO divisa in 2 classi. una per i messaggi, una per i nodi responsabili
	
	//query info
	int queryId;
	String input;
	String output;
	
	//Paths
	ArrayList<PathInfo> pathList;
	ArrayList<Long> messages;
	
	//First path
	Long time;
	
	//Selected path
	int selectedPath;
	double totalExecutionTime;
}
