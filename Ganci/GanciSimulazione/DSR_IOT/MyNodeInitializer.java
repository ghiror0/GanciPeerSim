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
	private static final String SINGLEPARAM = "singleParam";
	private static final String PARAMNUM = "paramNum";
	//private static final String INACTIVENODE = "inactiveNode";
	//private static final String QUERYNUM = "queryNum";
	
	private static final int MAXEXECTIME = 1000;
	private static final int MAXCOST = 1000;
	
	
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private static int pid;
	public static String[] parameters; 
	public static int freeQueryId = 0;
	public String name;
	public static StringBuilder nodesInfo = new StringBuilder();
	public static StringBuilder queryInfo = new StringBuilder();
	
	//static int queryNum;
	static boolean singleParam;
	static int paramNum;
	//static int inactiveNode;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public MyNodeInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		name = prefix;
		singleParam = Configuration.getBoolean(name +"." + SINGLEPARAM);
		paramNum = Configuration.getInt(name +"." + PARAMNUM);
		//inactiveNode = Configuration.getInt(name +"." + INACTIVENODE,Network.size());
		//queryNum = Configuration.getInt(name +"." + QUERYNUM,0);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public boolean execute() {
		initParamArray();
		initializeProtocol();

		//if(new TestClass().start(Network.get(0),pid)) return true;  //TODO rimuovere 
		
		return false;
	}
	
	
	public void initParamArray() {
		StringBuffer buf = new StringBuffer();
		for(int i = 1; i<=paramNum;i++) {
			buf.append(i+";");
		}
		parameters = buf.toString().split(";");
	}
	
	
	public void initializeProtocol() {
		Node n;
		MyProtocol prot;

		for (int i = 0; i < Network.size(); i++) { 			//Per ogni nodo della rete
			n = Network.get(i);
			prot = (MyProtocol) n.getProtocol(pid); 		//Prendo e inizializzo il protocollo del nodo
			setProtocol(i,prot);
			nodesInfo.append(prot.getNodeInfo().takeNodeInfo());
		}
		
	}
	

	private void setProtocol(int index, MyProtocol prot) {
		NodeInfo node;
		if(singleParam) {
			String input =  parameters[CommonState.r.nextInt(parameters.length)];
			node = new NodeSingle(index,input, setSingleOutput(input));
			node.setAvaiability(Math.abs(CommonState.r.nextDouble()));
			node.setCost(Math.abs(CommonState.r.nextInt(MAXCOST)));
			node.setThroughput(Math.abs(CommonState.r.nextDouble()));
			node.setExecTime(Math.abs(CommonState.r.nextInt(MAXEXECTIME)));
			
		}else {		//Nodo con parametri multipli
			node=null;
		}
		prot.setNodeInfo(node);
	}



	//Ritorna un output diverso dall'input
	public static String setSingleOutput(String input) {
		String output = parameters[CommonState.r.nextInt(parameters.length)];
		while (input.equals(output)) {
			output = parameters[CommonState.r.nextInt(parameters.length)];
		}
		return output;
	}
	
	
	
	
	/*
	private void initializeQuery() {
		
		for(int i = 0; i < queryNum;i++) {
			sendQuery();
		}
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
		
		Message mex = new Message(prot.getNodeInfo().getId(), MyProtocol.timeToLive, query);

		prot.addMessage(mex);
		mex.mexInfo();
		queryInfo.append(takeQueryInfo(query));

	}

	
	private String takeQueryInfo(Query query) {
		return query.getInfo();
	}




*/
}
