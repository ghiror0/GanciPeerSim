package DSR_IOT;

import DSR_IOT.query.Query;

public class Message {
	static long globalId = 0;
	static long actualMex = 0;
	private long messageId;
	private long starterNodeId;
	private long senderId;
	private boolean find = false;
	private Query query;
	private int timeToLive;
	
	
	public Message(long id,int timeToLive, Query query) {
		messageId = globalId++;
		starterNodeId = id;
		senderId = id;
		this.query = query;
		this.timeToLive = timeToLive;
		actualMex++;
	}
	
	public Message() {
	}
	
	public Message(Message mex) {
		messageId = mex.getMessageId();
		starterNodeId = mex.getStarterNodeId();
		find = mex.getFind();
		timeToLive = mex.getTimeToLive() - 1;
		if(mex.getQuery()!=null) {
			query = mex.getQuery().getCopy();
		}
		
	
	}
	

	/*public Message duplicateMex() { 
		Message newMex = new Message();

		newMex.messageId = messageId;	
		newMex.starterNodeId = starterNodeId;
		newMex.find = find;
		
		if(query!=null) {
			newMex.query = query.getCopy();
		}
		newMex.timeToLive = timeToLive -1;
		return newMex;
	}
	*/

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
	
	public int getTimeToLive() {
		return timeToLive;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

	

	
	public void removeActualMex() {
		actualMex--;
	}
	
	public void addActualMex() {
		actualMex++;
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
