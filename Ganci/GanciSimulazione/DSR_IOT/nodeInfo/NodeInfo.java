package DSR_IOT.nodeInfo;

public abstract class NodeInfo {
	protected long id;
	protected long executionTime = 0;
	protected long cost = 0;
	protected double avaiability = 0.0;
	protected double throughput = 0.0;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getExecTime() {
		return executionTime;
	}
	

	public void setExecTime(long time) {
		executionTime = time;
	}

	public void setCost(long cost) {
		this.cost = cost;
	}
	
	public long getCost() {
		return executionTime;
	}

	public void setAvaiability(double value) {
		avaiability = value;
	}
	
	public double getAvaiability() {
		return avaiability;
	}

	public void setThroughput(double value) {
		throughput = value;
	}
	
	public double getThroughput() {
		return throughput;
	}



	
	public abstract void printInfo();
	public abstract String takeNodeInfo();

	public String getValueInfo() {
		return "\tExecTime: " + executionTime +"\t\tCost: " + cost  + "\t\tAvaiability: " + avaiability + "\t\tThroughput: " + throughput;
	}
}
