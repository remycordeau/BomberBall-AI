package Morpion.ia;

import java.util.List;

/**
 * Classe représentant l'état dans lequel est une partie
 */
public class Etat {
	
	/** La partie */
	private final Partie partie;
	
	/** Morpion.ia.Plateau associé à la partie */
	private Plateau plateau;
	
	/** Identifiant du joueur courant */
	private int idJoueurCourant;
	
	/**
	 * Constructeur
	 *
	 * @param partie la partie
	 * @param plateau le plateau
	 * @param idJoueurCourant identifiant du joueur courant
	 */
	public Etat(Partie partie, Plateau plateau, int idJoueurCourant) {
		this.partie = partie;
		this.plateau = plateau;
		this.idJoueurCourant = idJoueurCourant;
	}
	
	/**
	 * Construit et retourne un clone de l'état courant
	 *
	 * @return Un état
	 */
	public Etat clone() {
		return new Etat(partie, plateau.clone(), idJoueurCourant);
	}
	
	/**
	 * Retourne l'indentifiant du joueur courant
	 *
	 * @return un entier
	 */
	public int getIdJoueurCourant() {
		return idJoueurCourant;
	}
	
	/**
	 * Modifie le joueur courant en mémorisant l'identifiant d'un nouveau joueur
	 *
	 * @param id l'identifiant du nouveau joueur courant
	 */
	public void setIdJoueurCourant(int id) {
		this.idJoueurCourant = id % partie.getNombreJoueurs();
	}
	
	/**
	 * Retourne le plateau
	 *
	 * @return Un plateau
	 */
	public Plateau getPlateau() {
		return plateau;
	}
	
	/**
	 * Jouer.
	 *
	 * @param a the a
	 */
	public void jouer(Action a) {
		if (estPossible(a)) {
			plateau.setCase(a.getX(), a.getY(), Symbole.values()[idJoueurCourant]);
		}
	}
	
	/**
	 * Teste si une action donnée est possible pour l'état courant
	 *
	 * @param a l'action à tester
	 * @return vrai si l'action est possible, faux sinon
	 */
	public boolean estPossible(Action a) {
		return (a.getX() >= 0 && a.getX() < plateau.getTaille()
				&& a.getY() >= 0 && a.getY() < plateau.getTaille()
				&& plateau.estLibre(a.getX(), a.getY()));
	}
	
	/**
	 * Liste et retourne l'ensemble des actions possibles pour l'état courant
	 *
	 * @return un liste d'actions
	 */
	public List<Action> actionsPossibles() {
		return plateau.getToutesCasesLibres();
	}
	
	/**
	 * Détecte et retourne la situation actuelle associée à l'état courant (en cours, égalité ou victoire d'un joueur)
	 *
	 * @return une situation
	 */
	public Situation situationCourante() {
		Joueur[] joueurs = partie.getTousJoueurs();
		Symbole symb = null;
		symb = plateau.ligneGagnante();
		if (symb != Symbole.VIDE) { return new Victoire(joueurs[symb.ordinal()]); }
		
		symb = plateau.colonneGagnante();
		if (symb != Symbole.VIDE) { return new Victoire(joueurs[symb.ordinal()]); }
		
		symb = plateau.diagonaleGagnante();
		if (symb != Symbole.VIDE) { return new Victoire(joueurs[symb.ordinal()]); }
		
		if (plateau.estRempli()) { return new Egalite(); }
		else { return new EnCours(); }
	}
	
}
