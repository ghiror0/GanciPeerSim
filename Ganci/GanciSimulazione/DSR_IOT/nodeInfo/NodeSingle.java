package DSR_IOT.nodeInfo;

public class NodeSingle extends NodeInfo {
	private String input;
	private String output;
	
	public NodeSingle(long id, String input, String output, long executionTime) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.executionTime = executionTime;
	}
	
	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	

	public void printInfo() {

		System.out.println(
				"\nID Nodo: " + id + "\tInput:\t" + input + "\tOutput:\t" + output + "\tExecTime: " + executionTime);

	}
	
	public String takeNodeInfo() {
		return "\nID Nodo: " + id + "\tInput:\t" + input + "\tOutput:\t" + 
				output + "\tExecTime: " + executionTime;
	}

}
