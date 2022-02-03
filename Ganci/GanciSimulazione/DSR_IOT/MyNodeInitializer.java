package DSR_IOT;


import DSR_IOT.nodeInfo.NodeSingle;
import network.MyWireTopology;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.NodeInitializer;

public class MyNodeInitializer implements NodeInitializer, Control  {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	private static final String SINGLEPARAM = "singleParam";
	private static final String PARAMNUM = "paramNum";
	private static final String INITLINK = "initLink";
	
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
	
	static boolean singleParam;
	static int paramNum;
	static boolean initLink;


	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public MyNodeInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		name = prefix;
		singleParam = Configuration.getBoolean(name +"." + SINGLEPARAM);
		paramNum = Configuration.getInt(name +"." + PARAMNUM);
		initLink = Configuration.getBoolean(name + "." + INITLINK,false);
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	public boolean execute() {
		initParamArray();
		initializeProtocol();	
		return false;
	}
	
	//Crea l'array di possibili tipi di dati
	public void initParamArray() {
		if(parameters !=null)return;
		StringBuffer buf = new StringBuffer();
		for(int i = 1; i<=paramNum;i++) {
			buf.append(i+";");
		}
		parameters = buf.toString().split(";");
	}
	
	//inizializza i nodi della rete
	public void initializeProtocol() {
		Node n;

		for (int i = 0; i < Network.size(); i++) { 			//Per ogni nodo della rete
			n = Network.get(i);
			initialize(n);
		}
	}
	

	@Override
	public void initialize(Node n) {
		MyProtocol prot  = (MyProtocol) n.getProtocol(pid); 
		if(singleParam) {
			String input =  parameters[CommonState.r.nextInt(parameters.length)];
			NodeSingle node = new NodeSingle(n.getID(),input, setSingleOutput(input));
			node.setAvaiability(Math.abs(CommonState.r.nextDouble()));
			node.setCost(Math.abs(CommonState.r.nextInt(MAXCOST)));
			node.setThroughput(Math.abs(CommonState.r.nextDouble()));
			node.setExecTime(Math.abs(CommonState.r.nextInt(MAXEXECTIME)));
			prot.setNodeInfo(node);
			nodesInfo.append(prot.getNodeInfo().takeNodeInfo());
			if(initLink) {
				MyWireTopology.addLink(n);
			}
			
		}else {		//Nodo con parametri multipli
			
		}
		
	}

	//Ritorna un output diverso dall'input
	public static String setSingleOutput(String input) {
		String output = parameters[CommonState.r.nextInt(parameters.length)];
		while (input.equals(output)) {
			output = parameters[CommonState.r.nextInt(parameters.length)];
		}
		return output;
	}
}
