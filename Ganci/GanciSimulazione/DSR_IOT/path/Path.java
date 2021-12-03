package DSR_IOT.path;

import java.io.PrintStream;
import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.query.Query;

public interface Path {
	
	
///////////////////////////////////////////////////////////////GET-SET//////////////////////////////////
public List<Long> getIds();
public int getNodeIndex(long nodeId);
public int size();

/////////////////////////////////////////////////////////// ADD ///////////////////////////////////////////////////

public void addPath(Path newPath);
public void addEndNode(NodeInfo node);

/////////////////////////////////////////////////////////// GET //////////////////////////////////////////////////////

public Path getTrunk(int start, int end);
public void getSolutionPath(Query query, NodeInfo node);
public void getComposedSolutionPath(Query query, NodeInfo node, Path cPath);
public double getTotalExecTime();
public Path getDuplicatePath();


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public boolean isRedundant(Path newPath);
public Path analyzePath(NodeInfo node, boolean LiveOptimization);
public boolean checkFind(Query query);


/////////////////////////////////////////////////////////PRINT//////////////////////////////////////////////////////////////////////
public void printPath();
public void printPath(PrintStream pf);





}
