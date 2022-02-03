package utility;

import java.util.ArrayList;

import DSR_IOT.MyNodeInitializer;
import DSR_IOT.MyProtocol;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.query.Query;
import DSR_IOT.query.QuerySingle;
import peersim.core.Network;
import peersim.core.Node;

//Implementazione dell'algoritmo proposto nell'articolo, versione semplificata per trovare solo
//tempo di esecuzione migliore rispetto al caso semplificato singolo input/singolo output


public class FindBest {
	ArrayList<NodeSingle> S = new ArrayList<>();		//Tutti i servizi
	ArrayList<String> D = new ArrayList<>();			//Tutti i dati
	
	public FindBest() { 
		init();
	}
	
	private void init() {
		addAllService();
		addAllData();
	}
	
	public void resetService() {
		S.clear();
		addAllService();
	}
	
	public void addAllService() {
		Node n;
		MyProtocol prot;
		
		for(int i = 0; i < Network.size(); i++) {
			n = Network.get(i);
			
			if(!n.isUp()) {
				continue;
			}
			
			prot = (MyProtocol) n.getProtocol(MyProtocol.pid); 	
			S.add((NodeSingle)prot.getNodeInfo());
			
		}
	}
	
	public void addAllData() {
		for(int i = 0; i < MyNodeInitializer.parameters.length; i++) {
			D.add(MyNodeInitializer.parameters[i]);
		}
	}
	
	public void insert(HeapBox newBox,ArrayList<HeapBox> H) {
		HeapBox box;
		for(int i = 0; i < H.size();i++) {
			box = H.get(i);
			if(box.value>newBox.value) {
				moveByOne(i,H);
				H.add(i,newBox);
				return;
			}
		}
		H.add(newBox);
	}
	
	public void moveByOne(int index,ArrayList<HeapBox> H) {

		HeapBox move,save;
		int size = H.size();
		move = H.get(index);
		
		for(int i = index+1; i < size;i++) {
			save = H.get(i);
			H.add(i,move);			
			H.remove(i-1);			
			move = save;

		}
		H.remove(size-1);
		H.add(move);
	}
	
	
	public HeapBox root(ArrayList<HeapBox> H) {
		HeapBox ret = H.get(0);
		H.remove(0);
		return ret;
	}
	
	
	public void decrease(HeapBox newBox,ArrayList<HeapBox> H) {
		HeapBox box;
		for(int i = 0; i < H.size();i++) {
			box = H.get(i);
			if(box.param.equals(newBox.param)) {	
				H.remove(i);
				insert(newBox,H);
				return;
			}
		}
	}
	
	
	
	public long getBestExecTime(Query myQuery) {	
		resetService();
		QuerySingle query = (QuerySingle) myQuery;
		ArrayList<HeapBox> H = new ArrayList<>();							//etichette dati temporanee
		ArrayList<NodeSingle> LS = new ArrayList<>();						//Servizi con etichette definitive
		ArrayList<String> LD = new ArrayList<>();							//Dati con etichette definiteive
		
		NodeSingle S0 = new NodeSingle(-1,"",query.getInput());
		S0.setExecTime(0);
		
		NodeSingle END = new NodeSingle(-2,query.getOutput(),"");
		END.setExecTime(0);
		
		S.add(END);
		S.add(S0);
		
		
		long[] ED= new long[D.size()];
		long[] ES= new long[S.size()];
		
		
		for(int i = 0; i < ED.length;i++) {
			ED[i]=Long.MAX_VALUE;
		}
		
		for(int i = 0; i < ES.length;i++) {
			ES[i]=Long.MAX_VALUE;
		}
		
		
		ES[S.indexOf(S0)] = 0;
		LS.add(S0);
		
		ED[D.indexOf(S0.getOutput())] = 0;
		HeapBox box = new HeapBox(S0.getOutput(),0);
		insert(box,H);
	
		while(LS.indexOf(END)==-1 && H.size()!=0){
			
			box = root(H);
			String param = box.param;
			long value = box.value;

			ArrayList<NodeSingle> tempS = new ArrayList<>();
			tempS.addAll(S);
			tempS.removeAll(LS);
			
			for(NodeSingle node: tempS) {
				if(node.getInput().equals(param)) {
					
					LS.add(node);
					ES[S.indexOf(node)]=value;
					long V = value + node.getExecTime();
					
					
					ArrayList<String> tempD = new ArrayList<>();	
					tempD.addAll(D);
					tempD.removeAll(LD);
					String tempParam = node.getOutput();
					if(LD.indexOf(tempParam)==-1 && !tempParam.equals("")) {
					
						if(ED[D.indexOf(tempParam)]==Long.MAX_VALUE) {
							ED[D.indexOf(tempParam)]=V;
							HeapBox tempBox = new HeapBox(tempParam,V);
							insert(tempBox,H);
						}else if(ED[D.indexOf(tempParam)]>V){
							ED[D.indexOf(tempParam)]=V;
							HeapBox tempBox = new HeapBox(tempParam,V);
							decrease(tempBox,H);
						}
					}
					LD.add(param);
				}
			}
		}

		long best = ES[S.indexOf(END)];
		if(best == Long.MAX_VALUE) {
			best = -1;
		}
		S.remove(END);
		S.remove(S0);
		return best;
	}


	public void printH(ArrayList<HeapBox> H) {
		System.out.print( "\n");
		HeapBox box;
		for(int i = 0; i < H.size();i++) {
			box = H.get(i);
			System.out.print(box.param + "->");
		}

		System.out.print( "\n");
	}

	

	public void printD() {
		for(String param : D) {
			System.out.print(param + "\n");
		}
	}
	
}

