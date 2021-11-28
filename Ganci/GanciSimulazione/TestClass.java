
import peersim.core.Node;

public class TestClass {
	MyProtocol mainPr;
	
	
	
	public PathInfo path1() {
		PathInfo path = new PathInfo();
		path.addStartParameter("a");
		path.addEndNode(0, "b",0);
		path.addEndNode(1, "c",1);
		path.addEndNode(2, "d",2);
		path.addEndNode(3, "v",3);
		path.addEndNode(4, "f",4);
		path.addEndNode(5, "g",5);
		return path;
	}
	

	public PathInfo path2() {
		PathInfo path = new PathInfo();
		path.addStartParameter("h");
		path.addEndNode(5, "i",6);
		path.addEndNode(6, "l",7);
		path.addEndNode(7, "b",8);
		path.addEndNode(8, "n",9);
		path.addEndNode(9, "o",10);
		return path;
	}
	
	public boolean start(Node n, int protocolID) {
		//if(true) return false;
		System.out.println("/////////////////////INIZIO CLASSE TEST///////////////////");
		
		mainPr = new MyProtocol("protocol.MyProtocol");
		
		MyProtocol pr =(MyProtocol) mainPr.clone();
		pr.setId(117);
		pr.setInput("f");
		pr.setOutput("z");
		pr.setExecTime(30);
	
		
		System.out.println("/////////////////////////////////PRIMO PASSAGGIOOOOOO////////////////////////////////");
		
		
		Message mex = new Message(3,"s","v");
		
		PathInfo path = path1();
		PathInfo path2 = path2();
		mex.addPath(path);
		mex.addPath(path2);
		path.printPath();
		path2.printPath();
		
		pr.cachePath.add(path);
		
		//pr.addMessage(mex);
		//pr.analyzeMex(mex);
		//pr.useCache(mex);
		//pr.checkCachePath(path2, "v").printPath();
		
		System.out.println("\n\n/////////////////////FINE CLASSE TEST///////////////////");
		return true;
	}
	
	public void testCache() {
		MyProtocol pr =(MyProtocol) mainPr.clone();
		pr.setId(117);
		pr.setInput("f");
		pr.setOutput("z");
		pr.setExecTime(30);
	
		
		System.out.println("/////////////////////////////////PRIMO PASSAGGIOOOOOO////////////////////////////////");
		
		
		Message mex = new Message(3,"s","v");
		
		PathInfo path = path1();
		PathInfo path2 = path2();
		mex.addPath(path);
		mex.addPath(path2);
		
		
		
		//pr.addMessage(mex);
		//pr.analyzeMex(mex);
		//pr.useCache(mex);
		//pr.checkCachePath(path2, "v");
		
	
		
		System.out.println("||||||||||||||||||||||||||||Stato messaggio:");
		showMessagePath(mex);
		System.out.println("||||||||||||||||||||||||||||Stato cache");
		showCachePath(pr);
		
		print("/////////////////////////////////SECONDO PASSAGGIOOOOOO////////////////////////////////");
		
		Message mex2 = new Message(3,"w","z");
		
		PathInfo path3 = new PathInfo();  //Qui viene testato il comportamento: path di dimensioni uguali, minori o maggiori, path uguali e diversi
		path3.addStartParameter("w");
		//path3.addEndNode(4, "b");
		path3.addEndNode(5, "b",1);
		path3.addEndNode(6, "b",1);
		path3.addEndNode(7, "c",1);
		//path3.addEndNode(8, "c");
		//path3.addEndNode(9, "b");
		//path3.addEndNode(10, "c");
		//path3.addEndNode(6, "d");
		//path3.addEndNode(8, "e");
		//path3.addEndNode(9, "f");
		
		mex2.addPath(path3);
		
		pr.addMessage(mex2);
		pr.analyzeMex(mex2);
		pr.useCache(mex2);
		
		
		print("||||||||||||||||||||||||||||Stato messaggio:");
		showMessagePath(mex2);
		
		
		print("||||||||||||||||||||||||||||Stato cache");
		showCachePath(pr);
	}
	
	public void testTrunk() {
		Message mex = new Message(3,"s","e");
		
		PathInfo path = path1();
		path.printPath();
		PathInfo path2 = path2();
		mex.addPath(path);
		mex.addPath(path2);
		
		path.getTrunk(path.getParamIndex("d"), path.getParamIndex("f")).printPath();
		path.getTrunk(2, 4).printPath();
	}

	
	public void showCachePath(MyProtocol prot) {
		System.out.println("Path presenti nella cache");
		for(PathInfo path:	prot.cachePath) {
			path.printPath();
		}
	}
	
	public void showMessagePath(Message mex) {
		System.out.println("Path presenti nel messaggio");
		for(PathInfo path: mex.paths) {
			path.printPath();
		}
	}
	
	
	public void print(String str) {
		System.out.println(str);;
	}
	
	
	public void niente() {
		
		//testCache();
		//PathInfo path2 = path2();
		//System.out.println("path in cache\n");
		//path.printPath();
		//PathInfo path2 = path.getTrunk(1,3);
		//path2.addEndNode(10, "dd");
		//path.printPath();
		//path2.printPath();
		//print("last: " + path.getLastOutput());
		//PathInfo path2 = path2();
		//System.out.println("path secondario\n");
		//path2.printPath();
		
		
		/*
		PathInfo path3 = new PathInfo();
		path3.addStartParameter("a");
		path3.addEndNode(3, "b");
		path3.addEndNode(4, "c");
		path3.addEndNode(7, "d");
		path3.addEndNode(8, "e");
		path3.addEndNode(9, "f");
		path3.printPath();
		*/
		
		//path.addPath(path2);
		//path.printPath();

	
		//showMessagePath(mex);
		/*
		pr.addMessage(mex);
		//pr.verbose = true;
		pr.nextCycle(n, protocolID);
		
		
		showCachePath(pr);
		*/
		/*
		PathInfo path5 = new PathInfo();
		path5.addStartParameter("i");
		path5.addEndNode(5, "l");
		path5.addEndNode(6, "m");
		path5.addEndNode(7, "c");
		path5.addEndNode(8, "o");
		path5.addEndNode(9, "t");
		System.out.println("path nel messaggio\n");
		path5.printParam();
		System.out.print("\n");
		path5.printIds();
		System.out.print("\n\n\n");
		*/
		
		
		//pr.cachePath.add(path);
		//pr.addToCache(path2);
		

		
		//pr2.cachePath.get(1).printParam();
		
		//path.addNode(5, "f", 2);
		//path.modifyPath(5, "gg", 2, 5);
		//PathInfo path2 = path.alternativePath(5, "n", 3);
		//PathInfo path3 = path.duplicatePath();
		/*PathInfo path2 = path.getTrunk(1, 3);*/
		//System.out.println("Query: input: i output : e\n");
		/*PathInfo path2 = pr2.checkCacheQuery("b", "e");
		path2.printParam();
		System.out.print("\n");
		path2.printIds();
		System.out.print("\n");
		*/
		/*PathInfo path3 = pr2.checkCachePath(path5, "d");
		if(path3==null) {
			System.out.print("Path non trovato");
			return;
		}
		path3.printParam();
		System.out.print("\n");
		path3.printIds();
		System.out.print("\n");
		*/
		/*
		String out;
		out = path.getLastOutput();
		System.out.println("  " + path.equals(path2) + path.equals(path3));
		*/
	}
	
}
