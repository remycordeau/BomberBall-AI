package Morpion.ia;

public class Action {
	private int x;
	private int y;
	
	public Action(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
}