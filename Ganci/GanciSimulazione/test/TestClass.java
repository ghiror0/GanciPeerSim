package test;


import java.util.ArrayList;


import DSR_IOT.Message;
import DSR_IOT.MyObserver;
import DSR_IOT.MyProtocol;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.path.Path;
import DSR_IOT.path.PathArray;
import DSR_IOT.path.PathArrayNode;
import DSR_IOT.query.QuerySingle;
import peersim.core.Network;
import peersim.core.Node;
import utility.FindBest;
import utility.HeapBox;

public class TestClass {
	MyProtocol mainPr;
	QuerySingle query = new QuerySingle(111,"A","Z");
	QuerySingle query2 = new QuerySingle(222,"AA","ZZ");
	QuerySingle query3 = new QuerySingle(333,"b","Z");
	
	PathArrayNode path = new PathArrayNode();	
	PathArrayNode pathCopia;
	PathArrayNode path2 = new PathArrayNode();	
	PathArrayNode path3 = new PathArrayNode();
	PathArrayNode path4 = new PathArrayNode();
	PathArrayNode path5 = new PathArrayNode();
	PathArrayNode path6 = new PathArrayNode();
	
	NodeSingle nodeMain = new NodeSingle(444,"d","Z",678);
	NodeSingle nodeMain2 = new NodeSingle(555,"f","Z",678);
	NodeSingle nodeMain3 = new NodeSingle(666,"AA","Z",678);
	NodeSingle nodeMain4 = new NodeSingle(777,"nn","Z",678);
	NodeSingle nodeMain5 = new NodeSingle(888,"cc","Z",678);
	
	NodeSingle node1 = new NodeSingle(1,"A","b",1);		
	NodeSingle node2 = new NodeSingle(2,"b","c",1);		
	NodeSingle node3 = new NodeSingle(3,"c","d",1);		
	NodeSingle node4 = new NodeSingle(4,"d","e",1);	
	NodeSingle node5 = new NodeSingle(5,"e","f",1);		
	NodeSingle node6 = new NodeSingle(6,"f","g",1);		
	NodeSingle node7 = new NodeSingle(7,"g","h",1);
	NodeSingle node8 = new NodeSingle(8,"d","zz",1);

	
	NodeSingle node9 = new NodeSingle(9,"AA","e",1);	

	NodeSingle node10 = new NodeSingle(10,"cc","f",1);	
	NodeSingle node11 = new NodeSingle(11,"f","mm",1);		
	NodeSingle node12 = new NodeSingle(12,"mm","Z",1);		
	NodeSingle node13 = new NodeSingle(13,"Z","nn",1);
	
	NodeSingle node14 = new NodeSingle(14,"h","k",1);
	
	NodeSingle node15 = new NodeSingle(15,"nn","ll",1);
	NodeSingle node16 = new NodeSingle(16,"e","cc",1);
	NodeSingle node17 = new NodeSingle(17,"AA","cc",1);
	NodeSingle node18 = new NodeSingle(18,"cc","dd",1);
	NodeSingle node19 = new NodeSingle(19,"dd","ee",1);
	NodeSingle node20 = new NodeSingle(20,"AA","b",1);
	NodeSingle node21 = new NodeSingle(21,"d","f",1);
	NodeSingle node22 = new NodeSingle(22,"f","e",1);
	
	NodeSingle node23 = new NodeSingle(23,"A","bb",1);
	NodeSingle node24 = new NodeSingle(24,"A","Z",1);
	
	

	


	public boolean start(Node n, int protocolID) {
		//if(true) return false;
		System.out.println("/////////////////////INIZIO CLASSE TEST///////////////////");
		mainPr = new MyProtocol("protocol.MyProtocol");
		
		test();
		
		
		System.out.println("\n\n/////////////////////FINE CLASSE TEST///////////////////");
		return true;
	}
	
	
	public void init(boolean verbose) {
		MyProtocol pr =(MyProtocol) mainPr.clone();
		pr.setNodeInfo( new NodeSingle(117,"","",30));
		path.initPath(query);
		path2.initPath(query2);
		path3.initPath(query3);
		path4.initPath(query3);
		path5.initPath(query2);
		path6.initPath(query2);
		
		makePath(verbose);
	}
	

	public void test() {
		boolean testRid,testFind,testComp;
		init(false);
		testRid=testRidondanza(false);
		testFind=testFind(false);
		testComp = testComposed(false);
		
		
		testTemp();
		
		
		
		if(testRid) {
			print("TEST RIDONDANZA PASSATO");
		}else {
			print("ERRORE RIDONDANZA!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		if(testFind) {
			print("TEST FIND PASSATO");
		}else {
			print("TEST FIND ERRORE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		if(testComp) {
			print("TEST COMP PASSATO");
		}else {
			print("TEST COMP ERRORE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
	}
	
	
	public void disattivaNodo(long id) {
		Node n;
		MyProtocol prot;
		NodeSingle node;
		for(int i = 0; i<Network.size();i++) {
			n = Network.get(i);
			prot = (MyProtocol) n.getProtocol(MyProtocol.pid); 
			node = (NodeSingle)prot.getNodeInfo();
			if(node.getId()==id) {
				n.setFailState(Node.DOWN);
			}
			
		}
	}
	
	public void testTemp() {
		
		//disattivaNodo(6);
		Node n;
		MyProtocol prot;
		NodeSingle node;
		for(int i = 0; i < Network.size();i++) {
			n = Network.get(i);
			prot = (MyProtocol) n.getProtocol(MyProtocol.pid); 
			node = (NodeSingle)prot.getNodeInfo();
			if(node.getId()!=i) {
				print("Id ed indice non corrispondo con nodo: " + node.getId());
			}
			
		}
		
		
		FindBest temp = new FindBest();
		print("Parametri:");
		//temp.printD();
		print("-_________________________________________________________");
		String paramA = "1";
		String paramB = "10";
		QuerySingle myQuery = new QuerySingle(117,paramA,paramB);
		print("Tempo migliore per trovare la query ("+ paramA + ","+ paramB +"): "+temp.getBestExecTime(myQuery));
	}
	
	
	public void testHeap() {
		ArrayList<HeapBox> H = new ArrayList<>();
		FindBest temp = new FindBest();
		HeapBox heap1 = new HeapBox("1",6);
		HeapBox heap2 = new HeapBox("2",5);
		HeapBox heap3 = new HeapBox("3",4);
		HeapBox heap4 = new HeapBox("4",3);
		HeapBox heap5 = new HeapBox("5",2);
		HeapBox heap6 = new HeapBox("6",4);
		HeapBox heap7 = new HeapBox("6",2);
		
		
		temp.insert(heap1,H);
		
		temp.printH(H);
		temp.insert(heap2,H);
		
		temp.printH(H);
		temp.insert(heap3,H);
		
		temp.printH(H);
		temp.insert(heap4,H);
		
		temp.printH(H);
		temp.insert(heap5,H);
		
		temp.printH(H);
		temp.insert(heap6,H);
		
		temp.decrease(heap7,H);
		temp.root(H);
		print("FINALEEEEEEEEEEE");
		temp.printH(H);
	}
	
	public boolean testRidondanza(boolean verbose) {
		if (verbose) {
			print("INIZIO TEST RIDONDANZA:___________________________________________________________________\n\n");

			print("Primo path:");
			path.printPath();

			print("Path Copia:");
			pathCopia.printPath();

			print("Secondo path:");
			path2.printPath();

			print("Terzo path:");
			path3.printPath();
			print("\n\n");

		}
		
		if(redundantCheck(pathCopia,path,verbose) || 
				!redundantCheck(path,pathCopia,verbose) || 
				redundantCheck(path2,path,verbose) || 
				redundantCheck(path,path3,verbose) ||
				redundantCheck(path,path4,verbose) 
				
				) {
			if(verbose) print("\nFINE TEST RIDONDANZA:___________________________________________________________________\n\n");
			return false;
		}
		if(verbose) print("\nFINE TEST RIDONDANZA:___________________________________________________________________\n\n");
		return true;

	}
	
	public Path analizzaPath(PathArrayNode path, NodeSingle node) {
		
		//print("Nodo analizzato:");
		//node.printInfo();
		Path temp = path.analyzePath(node, true);
		if(temp != null) {
			print("Trovato path alternativo:");
			temp.printPath();
			}
		//path.printPath();
		//print("--------------------------");
		return temp;
	}
	
	public boolean redundantCheck(PathArrayNode path1, PathArrayNode path2, boolean verbose) {
		if(verbose) {
			print("Il path:");
			path2.printPath();
			print("è ridodante nel path ?");
			path1.printPath();
		}
		
		if(path1.isRedundant(path2)) {
			if(verbose)print("Il path è ridondante\n\n");
			return true;
		}
		if(verbose)print("Il path NON è ridondante\n\n");
		return false;
		
	}
	
	
	public void makePath(boolean verbose) {
		analizzaPath(path,node1);
		analizzaPath(path,node2);
		analizzaPath(path,node3);
		analizzaPath(path,node4);
		analizzaPath(path,node5);
		analizzaPath(path,node6);
		analizzaPath(path,node7);
		//analizzaPath(path,node23);
		//analizzaPath(path,node24);
		
		pathCopia = path.getDuplicatePath();
		pathCopia = pathCopia.getTrunk(2, 5);
		pathCopia.addStartParameter("b");
		
		analizzaPath(path2,node9);
		analizzaPath(path2,node16);
		analizzaPath(path2,node10);
		analizzaPath(path2,node11);
		analizzaPath(path2,node12);
		analizzaPath(path2,node13);
		analizzaPath(path2,node15);
		

		
		analizzaPath(path3,node2);
		analizzaPath(path3,node3);
		analizzaPath(path3,node4);
		analizzaPath(path3,node5);
		analizzaPath(path3,node6);
		analizzaPath(path3,node7);
		analizzaPath(path3,node14);
		
		analizzaPath(path4,node2);
		analizzaPath(path4,node3);
		analizzaPath(path4,node8);
		
		analizzaPath(path5,node17);
		analizzaPath(path5,node18);
		analizzaPath(path5,node19);
		
		analizzaPath(path6,node20);
		analizzaPath(path6,node2);
		analizzaPath(path6,node3);
		analizzaPath(path6,node21);
		analizzaPath(path6,node22);
		
		
		if (verbose) {

			print("MAKE-PATH_______________________________________________________\n\n");

			print("Inizio primo path:\n________________________________________________________\n");

			path.printPath();
			print("Fine primo path:\n__________________________________________________________\n\n");

			print("Inizio path copia:\n________________________________________________________\n");

			pathCopia.printPath();
			print("Fine path copia:\n________________________________________________________\n");

			print("Inizio Secondo path:\n________________________________________________________\n");

			path2.printPath();
			print("Fine Secondo path:\n__________________________________________________________\n\n");

			print("Inizio Terzo path:\n________________________________________________________\n");

			path3.printPath();
			print("Fine Terzo path:\n________________________________________________________\n");

			print("Inizio Quarto path:\n________________________________________________________\n");

			path4.printPath();
			print("Fine Quarto path:\n________________________________________________________\n");

			print("FINE MAKE-PATH_______________________________________________________\n\n");
		}

	}
	
	public boolean testFind(boolean verbose) {
		if (verbose) {
			print("INIZIO TEST FIND:_______________________________________________________________");
			print("path -> path2");
			path.printPath();
			path2.printPath();

			print("Query:");
			query.printInfo();
			print("Con nodeMain2:");
			nodeMain2.printInfo();
			print("con nodeMain3:");
			nodeMain3.printInfo();
			print("con nodeMain4:");
			nodeMain4.printInfo();
			print("con nodeMain5:");
			nodeMain5.printInfo();
			print("____________________________________________________________");
		}
		
		Path tempPath;
		
		tempPath = path.getSolutionPath(query);
		if(tempPath!=null) {
			query.addPath(tempPath);
		}
		
		tempPath = path.getSolutionPath(query, nodeMain2);
		if(tempPath!=null) {
			query.addPath(tempPath);
		}
		
		tempPath = path.getSolutionPath(query, nodeMain3);
		if(tempPath!=null) {
			query.addPath(tempPath);
		}

		/*path.getSolutionPath(query, nodeMain2);
		path.getSolutionPath(query, nodeMain3);*/

		if (verbose) {
			print("Dopo find solution con path ed i nodiMain2-3, numero path: " + query.getPaths().size());
			query.printPathsInfo();
			print("____________________________________________________________");
		}

		/*path.getComposedSolutionPath(query, nodeMain2, path2);
		path.getComposedSolutionPath(query, nodeMain3, path2);
		path.getComposedSolutionPath(query, nodeMain4, path2);
		path.getComposedSolutionPath(query, nodeMain5, path2);*/
		
		int temp = 0;
		
		

		tempPath = path.getComposedSolutionPath(query, nodeMain2, path2);
		if(tempPath != null) {
			temp++;
			query.addPath(tempPath);
		}
		tempPath = path.getComposedSolutionPath(query, path2);
		if(tempPath != null) {
			temp++;
			query.addPath(tempPath);
		}
		
		tempPath = path.getComposedSolutionPath(query, nodeMain3, path2);
		if(tempPath != null) {
			temp++;
			query.addPath(tempPath);
		}

		
		tempPath = path.getComposedSolutionPath(query, nodeMain4, path2);
		if(tempPath != null) {
			temp++;
			query.addPath(tempPath);
		}

		
		tempPath = path.getComposedSolutionPath(query, nodeMain5, path2);
		if(tempPath != null) {
			temp++;
			query.addPath(tempPath);
		}
		
		

		if (verbose) {
			print("Dopo findComposedSolution con path ed i nodiMain2-3-4-5, numero path: " + 
		query.getPaths().size() + " temp: " + temp);

			query.printPathsInfo();
			print("FINETEST FIND:_______________________________________________________________");
		}
		if (query.getPaths().size() != 4) {
			return false;
		} else {
			return true;
		}
		
	}
	
	public boolean testComposed(boolean verbose) {

		PathArrayNode temp;
		if(verbose) {
			print("INIZIO TEST COMPOSED:______________________________________________");
			path.printPath();
			path2.printPath();
			path5.printPath();
			path6.printPath();
			print("-------------------");
		}
		
		temp = path.findComposedPath(path2, "nonPresente");
		if( temp != null) {
			print("Trovato path che non dovrebbe esistere\n");
			temp.printPath();
			return false;
		}
		temp = path.findComposedPath(path2, "Z");
		if(temp == null ) { //CasoPresente
			print("Non trovo path che dovrebbe esserci\n");
			return false;
		}
		if(temp.getSize()!=8) {
			print("Dimensione sbagliata\n");
			return false;
		}
		
		
		temp = path.findComposedPath(path5, "ee");
		
		if(temp!= null) {
			print("Non dovrebbero avere elementi in comune");
			temp.printPath();
			return false;
		}
		
		temp = path6.findComposedPath(path5, "ee");
		
		if(temp!= null) {
			print("NON dovrei considerare qusto caso limite. preso tutto il path in cache");
			temp.printPath();
			return false;
		}
		
		temp = path.findComposedPath(path6, "e");
		
		if(temp!= null) {
			print("Path da non cosiderare, altrimenti trovo doppioni nei parametri");
			temp.printPath();
			return false;
		}
		
		
		temp = path.findComposedPath(path2, "f");
		
		if(temp== null) {
			print("Dovrei aver trovato quel path particolare in cui entrambi hanno l'output che cerco");
			return false;
		}
		
		
		
		
		if(verbose) {
			print("Ultimo path trovato");
			temp.printPath();
			print("FINE TEST COMPOSED:______________________________________________");
		}
		return true;
		
	}
	
	public void print(String text) {
		System.out.println(text);
	}
	
	
	
	
	public PathArray path1() {
		PathArray path = new PathArray();
		path.addStartParameter("a");
		addNode(path,0, "b",0);
		addNode(path,1, "c",1);
		addNode(path,2, "d",50);
		addNode(path,3, "e",3);
		addNode(path,4, "f",4);
		addNode(path,5, "g",5);
		return path;
	}
	
	public void addNode(Path path, long id, String output, long time) {
		NodeSingle node = new NodeSingle(id,"",output,time);
		path.addEndNode(node);
	
	}
	
	

	public PathArray path2() {
		PathArray path = new PathArray();
		path.addStartParameter("b");
		addNode(path,5, "i",0);
		addNode(path,6, "l",1);
		addNode(path,7, "m",2);
		addNode(path,8, "n",3);
		addNode(path,9, "o",4);
		addNode(path,10, "p",5);
		return path;
	}
	
	
	
}
