package network;


import peersim.config.Configuration;
import peersim.core.CommonState;
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

	public MyWireTopology(String prefix) {
		super(prefix);

		coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
		connectivity = Configuration.getDouble(prefix + "." + CONNECT);
		inactiveNode = Configuration.getInt(prefix +"." + INACTIVENODE);
		activeNode = Network.size() - inactiveNode;
	}
	
	private void initDeactiveNode() {
		for (int i = activeNode; i < Network.size(); ++i) { //Disattivo tutti i nodi extra
				Network.get(i).setFailState(Node.DOWN);	
				//System.out.printf("disattivo nodo con id: %d e valore i %d\n ",Network.get(i).getIndex(),i);
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
			
			//addNode(n);
			
			
			for (int j = 0; j < Network.size(); j++) { // Per tutti gli altri nodi

				if (i == j)
					continue;

				//Node parent = (Node) g.getNode(j);
				if (CommonState.r.nextDouble() <= connectivity) {
					g.setEdge(i, j);
				}
			}
		}
		
		initDeactiveNode();
	}

	public static void addNodeLink(Node node) {
		for (int i = 0; i < Network.size(); ++i) { // Per ogni nodo
			if (node.getIndex() == i) {
				continue;
			}

			if (Network.get(i).isUp() && CommonState.r.nextDouble() <= connectivity) {
				graph.setEdge(node.getIndex(), i);
			
			}
		}
	}	
	
	public static void deleteNodeLink(Node n) {
		int nIndex = n.getIndex();
		for(int nodeIndex : graph.getNeighbours(nIndex)){
			graph.clearEdge(nIndex, nodeIndex);
			System.out.println("Cambiato vicino");
		}
	}
	
}
