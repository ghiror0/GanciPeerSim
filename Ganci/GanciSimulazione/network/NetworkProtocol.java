package network;


import peersim.core.Protocol;

public class NetworkProtocol implements Protocol{
	private double x,y;
	
	public NetworkProtocol(String prefix) {
		x=y=-1;
	}
	
	public Object clone() {
	NetworkProtocol ntk = null;
	try {
		ntk = (NetworkProtocol) super.clone();
	}catch(CloneNotSupportedException e) {
		
	}
	return ntk;
}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setX(double x) {
		this.x=x;
	}
	
	public void setY(double y) {
		this.y=y;
	}

}
