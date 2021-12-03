package network;


import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class InitNetwork implements Control {
	private static final String PAR_PROT = "protocol";
	private static int pid;
	
	public InitNetwork(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}
	
	public boolean execute() {
		Node n = Network.get(0);
		NetworkProtocol prot = (NetworkProtocol) n.getProtocol(pid);
		prot.setX(0.5);
		prot.setY(0.5);
		
		for(int i = 1; i < Network.size(); i++) {
			n = Network.get(i);
			prot = (NetworkProtocol) n.getProtocol(pid);
			prot.setX(i);
			prot.setY(i);
		}
		return false;
		
	}
}
