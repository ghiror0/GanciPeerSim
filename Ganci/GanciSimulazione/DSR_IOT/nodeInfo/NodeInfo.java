package DSR_IOT.nodeInfo;

public abstract class NodeInfo {
	protected long id;
	protected long executionTime;
	
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


	
	public abstract void printInfo();
	public abstract String takeNodeInfo();

}
