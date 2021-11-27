

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class MyNodeInitializer implements Control {
	// ------------------------------------------------------------------------
	// Parameters
	// ------------------------------------------------------------------------
	private static final String PAR_PROT = "protocol";
	private static final int MAXTIME = 1000;
	// ------------------------------------------------------------------------
	// Fields
	// ------------------------------------------------------------------------
	private static int pid;
	private static String[] parameters =  {"a","b","c","d","e","f","g","h","i","l","m","n","o","p","q"};
	//private static String[] parameters = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "l", "m", "n", "o", "p", "q","r", "s", "t", "u", "v", "z", "aa", "bb", "cc", "dd", "ff", "gg", "qq" };
	private int freeId = 0;
	public String name;
	
	int queryNum;

	// ------------------------------------------------------------------------
	// Constructor
	// ------------------------------------------------------------------------
	public MyNodeInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		name = prefix;
		queryNum = Configuration.getInt(name +"." + "queryNum",1);
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
			prot.printInfo();  								//TODO Scrivere su file
		}
		
		System.out.println("\n\n\n"); 						//TODO rimuovere
	}
	
	private void initializeQuery() {
		for(int i = 0; i < queryNum;i++) {
			sendQuery();
		}
	}
	
	
	private void setProtocol(MyProtocol prot) {
		prot.setId(freeId++);
		setParameters(prot);
		prot.setExecTime(Math.abs(CommonState.r.nextInt(MAXTIME)));
	}

	//Seleziona un input ed un output
	private void setParameters(MyProtocol prot) {
		String input = parameters[CommonState.r.nextInt(parameters.length)];
		String output = setOutput(input);
		prot.setInput(input);
		prot.setOutput(output);
	}

	//Ritorna un output diverso dall'input
	private String setOutput(String input) {
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

		String input = parameters[CommonState.r.nextInt(parameters.length)];
		String output = setOutput(input);

		Message mex = new Message(prot.getId(), input, output);

		prot.addMessage(mex);
		mex.mexInfo();

	}
}
