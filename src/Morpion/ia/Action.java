package Morpion.ia;

/**
 * Classe représentant une action comme le choix d'un case à joueur
 */
public class Action {
	
	/** Abscisse de la case sur laquelle jouer  */
	private int x;
	
	/** Ordonnée de la case sur laquelle jouer */
	private int y;
	
	/**
	 * Constructeur
	 *
	 * @param x Abscisse
	 * @param y Ordonnée
	 */
	public Action(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Retourne l'abscisse
	 *
	 * @return Un entier
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Retourne l'ordonnée
	 *
	 * @return Un entier
	 */
	public int getY() {
		return y;
	}
	
	@Override
	public String toString() {
		return "("+x+ ", "+y+")";
	}
}
