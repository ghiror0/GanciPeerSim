package DSR_IOT.query;

import java.util.List;

import DSR_IOT.nodeInfo.NodeInfo;
import DSR_IOT.path.Path;

public interface Query {
	
	static String modeExecTime = "executionTime";
	static String modeCost = "cost";
	static String modeAvaiability = "avaiability";
	static String modeThroughput = "throughput";
	
	public long getId();

	public void setSelectedPath(int selected);
	public int getSelectedPath();
	public long getStartingTime();
	public void setBestTime(long bestTime);
	public long getBestTime();
	public long getSelectedTime();
	
	
	public void clearUnused();
	public int selectBestPath(String mode) ;
	public boolean checkFind();

	public String getInfo();
	public void printInfo();
	public void printPathsInfo();
	public String getResultInfo(String mode);
	
	
	public List<Path> getPaths();
	public Query getCopy();
	
	
	public void addPath(Path newPath); 
	public void mergePath(Query query);
	public void findSolutionPath(NodeInfo node, Path cPath);
	public void analyzeNode(NodeInfo node, boolean liveOptimization);
	public void removePaths(int start, int delete);
	
	public int checkQuery();
}