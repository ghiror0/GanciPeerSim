package DSR_IOT.path;

import java.io.PrintStream;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.query.Query;

public interface Path {
	
	
///////////////////////////////////////////////////////////////GET-SET//////////////////////////////////
public int getNodeIndex(long nodeId);
public int getSize();
public void initPath(Query query);

/////////////////////////////////////////////////////////// ADD ///////////////////////////////////////////////////

public void addEndNode(NodeInfo node);

/////////////////////////////////////////////////////////// GET //////////////////////////////////////////////////////

public Path getTrunk(int start, int end);
public Path getSolutionPath(Query query, NodeInfo node);
public Path getSolutionPath(Query query);
public Path getComposedSolutionPath(Query query, NodeInfo node, Path cPath);
public Path getComposedSolutionPath(Query query, Path cPath);
public long getTotalExecTime();
public double getTotalAvaiability();
public long getTotalCost();
public double getTotalThroughput();
public Path getDuplicatePath();


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public boolean isRedundant(Path newPath);
public Path analyzePath(NodeInfo node, boolean LiveOptimization);
public boolean checkFind(Query query);
public boolean checkRemove(Query query);

public boolean allNodeUp();

/////////////////////////////////////////////////////////PRINT//////////////////////////////////////////////////////////////////////
public void printPath();
public void printPath(PrintStream pf);





}
