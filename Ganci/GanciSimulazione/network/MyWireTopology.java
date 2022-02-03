package network;


import DSR_IOT.MyProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

public class MyWireTopology extends WireGraph {
	private static final String PAR_COORDINATES_PROT = "coord_protocol";
	private static final String INACTIVENODE = "inactiveNode";
	private static final String CONNECT = "connectivity";
	private final int coordPid;
	private static double connectivity;
	private static Graph graph;
	public static int inactiveNode;
	private int activeNode;
	Linkable linkable;

	public MyWireTopology(String prefix) {
		super(prefix);

		coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
		connectivity = Configuration.getDouble(prefix + "." + CONNECT);
		inactiveNode = Configuration.getInt(prefix +"." + INACTIVENODE,0);
		activeNode = Network.size() - inactiveNode;
	}
	
	private void initDeactiveNode() {	
		for (int i = activeNode; i < Network.size(); ++i) { //Disattivo tutti i nodi extra
				Network.get(i).setFailState(Node.DOWN);	
		}
	}
	

	public void wire(Graph g) {
		System.out.println("Nodi attivi: " + activeNode);
		System.out.println("Nodi disattivati: " + inactiveNode);
		
		graph = g;
		Node n;
		
		for (int i = 0; i < Network.size(); ++i) { // Per ogni nodo
			n = Network.get(i);
			
			if(!n.isUp()) {  //Se il nodo non è attivo, non lo unisco al grafo della rete
				continue;
			}
			
			for (int j = 0; j < Network.size(); j++) { // Per tutti gli altri nodi

				if (i == j)
					continue;


				if (CommonState.r.nextDouble() <= connectivity) {
					//creo entrambi i rami, in modo da rendere il link bidirezionale
					g.setEdge(i, j);
					g.setEdge(j, i);	
				}
			}
		}
		
		initDeactiveNode();
	}
	
	public static void deleteNodeLink(Node n) {	//TODO non funzionante, non è possibile eliminare edge dal grafo
		int nIndex = n.getIndex();
		for(int nodeIndex : graph.getNeighbours(nIndex)){
			graph.clearEdge(nIndex, nodeIndex);
		}
	}
	
	public static int addLink(Node n) { 
		int linkableID = FastConfig.getLinkable(MyProtocol.pid);
		Linkable linkable = (Linkable) n.getProtocol(linkableID);
		
		Node node;
		for (int i = 0; i < Network.size(); ++i) { // Per ogni nodo
			
			if (CommonState.r.nextDouble() > connectivity) {
				continue;
			}
			
			node = Network.get(i);
			
			if(!node.isUp()) {  //Se il nodo non è attivo, non lo unisco al grafo della rete
				continue;
			}
			
			if(node.getID()==n.getID()) {
				continue;
			}
			linkable.addNeighbor(node);
			Linkable linkable2 = (Linkable) node.getProtocol(linkableID);
			linkable2.addNeighbor(n);
		}
		return linkable.degree();
	}
	
}
