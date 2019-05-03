package Morpion.ia;

/**
 * Classe représentant une situation de victoire
 */
public class Victoire implements Situation {
	
	/** Morpion.ia.Joueur vainqueur */
	private Joueur vainqueur;
	
	/**
	 * Constructeur
	 *
	 * @param j Morpion.ia.Joueur vaiqueur
	 */
	public Victoire(Joueur j) {
		this.vainqueur = j;
	}
	
	/**
	 * Retourne le vainqueur
	 *
	 * @return un joueur
	 */
	public Joueur getVainqueur() {
		return vainqueur;
	}
}
