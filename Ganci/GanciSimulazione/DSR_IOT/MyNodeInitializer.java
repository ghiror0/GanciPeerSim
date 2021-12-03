package DSR_IOT;


import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.query.Query;
import DSR_IOT.query.QuerySingle;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
//import test.TestClass;

public class MyNodeInitializer implements Control {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	private static final String QUERYNUM = "queryNum";
	private static final String SINGLEPARAM = "singleParam";
	private static final String PARAMARRAY = "paramArray";
	
	private static final int MAXEXECTIME = 1000;
	
	private static final String[] SHORTPARAM = {"a","b","c","d","e","f","g","h","i","l","m","n","o","p","q"};
	private static final String[] LONGPARAM = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "l", "m", "n", "o", "p", "q","r", "s", "t", "u", "v", "z", "aa", "bb", "cc", "dd", "ff", "gg", "qq" };
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private static int pid;
	private static String[] parameters; 
	private int freeId = 0;
	private int freeQueryId = 0;
	public String name;
	public static StringBuilder nodesInfo = new StringBuilder();
	public static StringBuilder queryInfo = new StringBuilder();
	
	int queryNum;
	boolean singleParam;
	boolean paramArray;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public MyNodeInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		name = prefix;
		queryNum = Configuration.getInt(name +"." + QUERYNUM,1);
		singleParam = Configuration.getBoolean(name +"." + SINGLEPARAM,true);
		paramArray = Configuration.getBoolean(name +"." + PARAMARRAY,true);
		
		if(paramArray) {
			parameters=SHORTPARAM;
		}else{
			parameters=LONGPARAM;
		}
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public boolean execute() {
		initializeProtocol();

		initializeQuery();
		
		//if(new TestClass().start(Network.get(0),pid)) return true;  //TODO rimuovere 
		
		return false;
	}
	
	
	public void initializeProtocol() {
		Node n;
		MyProtocol prot;

		for (int i = 0; i < Network.size(); i++) { 			//Per ogni nodo della rete
			n = Network.get(i);
			prot = (MyProtocol) n.getProtocol(pid); 		//Prendo e inizializzo il protocollo del nodo
			setProtocol(prot);
			nodesInfo.append(prot.getNodeInfo().takeNodeInfo());
			prot.getNodeInfo().printInfo();  								
		}
		
		System.out.println("\n\n\n"); 						//TODO rimuovere
	}
	
	
	private void initializeQuery() {
		
		for(int i = 0; i < queryNum;i++) {
			sendQuery();
		}
	}
	
	
	private String takeQueryInfo(Query query) {
		return query.getInfo();
	}
	
	
	private void setProtocol(MyProtocol prot) {
		NodeInfo node;
		if(singleParam) {
			String input =  parameters[CommonState.r.nextInt(parameters.length)];
			node = new NodeSingle(freeId++,input, setSingleOutput(input),Math.abs(CommonState.r.nextInt(MAXEXECTIME)));
		}else {		//Nodo con parametri multipli
			node=null;
		}
		prot.setNodeInfo(node);
	}



	//Ritorna un output diverso dall'input
	private String setSingleOutput(String input) {
		String output = parameters[CommonState.r.nextInt(parameters.length)];
		while (input.equals(output)) {
			output = parameters[CommonState.r.nextInt(parameters.length)];
		}
		return output;
	}

	//Crea ed invia una query
	private void sendQuery() {
		Node n = Network.get(CommonState.r.nextInt(Network.size()));
		MyProtocol prot = (MyProtocol) n.getProtocol(pid);
		
		Query query;

		if(singleParam) {
			String input = parameters[CommonState.r.nextInt(parameters.length)];

			query = new QuerySingle(freeQueryId++,input,setSingleOutput(input));
		
		}else {
			query = null;
		}
		
		Message mex = new Message(prot.getNodeInfo().getId(), query);

		prot.addMessage(mex);
		mex.mexInfo();
		queryInfo.append(takeQueryInfo(query));

	}
}
