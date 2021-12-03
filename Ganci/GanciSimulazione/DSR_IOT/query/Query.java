package DSR_IOT.query;

import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.path.Path;

public interface Query {
	
	public long getId();

	public void setSelectedPath(int selected);
	public int getSelectedPath();


	

	public void clearUnused();
	public int selectBestPath() ;
	public boolean checkFind();

	public String getInfo();
	public void printInfo();
	public void printPathsInfo();
	
	
	public List<Path> getPaths();
	public Query getCopy();
	
	
	public void addPath(Path newPath); 
	public void meshPath(Query query);
	public void findSolutionPath(NodeInfo node, Path cPath);
	public void analyzeNode(NodeInfo node, boolean liveOptimization);
}