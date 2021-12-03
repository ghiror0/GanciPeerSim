package test;


import DSR_IOT.Message;
import DSR_IOT.MyProtocol;
import DSR_IOT.nodeInfo.NodeSingle;
import DSR_IOT.path.Path;
import DSR_IOT.path.PathArray;
import DSR_IOT.query.QuerySingle;
import peersim.core.Node;

public class TestClass {
	MyProtocol mainPr;
	
	
	
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
	
	public boolean start(Node n, int protocolID) {
		//if(true) return false;
		System.out.println("/////////////////////INIZIO CLASSE TEST///////////////////");
		
		mainPr = new MyProtocol("protocol.MyProtocol");
		
		test();
		
		
		System.out.println("\n\n/////////////////////FINE CLASSE TEST///////////////////");
		return true;
	}
	
	
	public void test() {
		MyProtocol pr =(MyProtocol) mainPr.clone();
		pr.setNodeInfo( new NodeSingle(117,"","",30));
		PathArray path =path1();
		PathArray path2 = path2();
		//QuerySingle query = new QuerySingle(1,"a","n");
		//path.findComposedPath(path2,"n").printPath();
		//query.printPathsInfo();
		
		/*Message mex = new Message(1,"a","z");
		mex.addPath(path);
		path.analyzePath(pr, true, mex);
		mex.printInfo();
		if(mex.getFind()) {
			print("Find == true");
		}*/
	}
	
	public void print(String text) {
		System.out.println(text);
	}
	
}
