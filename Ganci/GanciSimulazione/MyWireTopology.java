

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireGraph;
import peersim.graph.Graph;

public class MyWireTopology extends WireGraph {
	private static final String PAR_COORDINATES_PROT = "coord_protocol";
	private final int coordPid;
	private double connectivity;

	public MyWireTopology(String prefix) {
		super(prefix);

		coordPid = Configuration.getPid(prefix + "." + PAR_COORDINATES_PROT);
		connectivity = Configuration.getDouble(prefix + "." + "connectivity", 0.5);
	}

	public void wire(Graph g) {

		for (int i = 0; i < Network.size(); ++i) { // Per ogni nodo
			//Node n = (Node) g.getNode(i);

			for (int j = 0; j < Network.size(); j++) { // Per tutti gli altri nodi

				if (i == j)
					continue;

				//Node parent = (Node) g.getNode(j);
				if (CommonState.r.nextDouble() <= connectivity) {
					g.setEdge(i, j);
				}
			}
		}
	}
}
