package DSR_IOT;

import DSR_IOT.query.Query;

public class Message {
	static long globalId = 0;
	private long messageId;
	private long starterNodeId;
	private long senderId;
	private boolean find = false;
	Query query;
	
	
	public Message(long id, Query query) {
		messageId = globalId++;
		starterNodeId = id;
		senderId = id;
		this.query = query;
		
	}
	
	public Message() {
	}
	


	
	///////////////////////////////////////////////////////////GET_SET ///////////////////////////////////

	public void setSenderId(long id) {
		senderId = id;
	}

	public long getSenderId() {
		return senderId;
	}
	
	public void setMessageId(int id) {
		messageId = id;
	}
	
	public long getMessageId() {
		return messageId;
	}
	
	public void setStarterNodeId(long id) {
		starterNodeId = id;
	}
	
	public long getStarterNodeId() {
		return starterNodeId;
	}

	public Query getQuery() {
		return query;
	}
	
	public void setFind(boolean find) {
		this.find = find;
	}
	
	public boolean getFind() {
		return find;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	

	public Message duplicateMex() { //TODO prove con clone()
		Message newMex = new Message();

		newMex.messageId = messageId;	
		newMex.starterNodeId = starterNodeId;
		newMex.find = find;
		
		if(query!=null) {
			newMex.query = query.getCopy();
		}
		
		return newMex;
	}

	
	

	
	//////////////////////////////////////////////////////////////PRINT//////////////////////////////////////////////////////////////////////

	public void mexInfo() {	
		System.out.println("Id message: " + messageId + "\tStarterNodeId: " + starterNodeId);
		query.printInfo();
	}

	

	public void printInfo() {
		mexInfo();
		System.out.println("Paths:\n");
		query.printPathsInfo();
	}
	


}
