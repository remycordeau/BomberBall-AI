import java.util.List;

public class Etat {
	private final Partie partie;
	private Plateau plateau;
	private int idJoueurCourant;
	
	public Etat(Partie partie, Plateau plateau, int idJoueurCourant) {
		this.partie = partie;
		this.plateau = plateau;
		this.idJoueurCourant = idJoueurCourant;
	}
	
	public Etat clone() {
		return new Etat(partie, plateau.clone(), idJoueurCourant);
	}
	
	public int getJoueurCourant() {
		return idJoueurCourant;
	}
	
	public void setJoueurCourant(int id) {
		this.idJoueurCourant = id % partie.getNombreJoueurs();
	}
	
	public Plateau getPlateau() {
		return plateau;
	}
	
	public void jouer(Action a) {
		if (estPossible(a)) {
			plateau.setCase(a.getX(), a.getY(), Symbole.values()[idJoueurCourant]);
		}
	}
	
	public boolean estPossible(Action a) {
		return (a.getX() >= 0 && a.getX() < plateau.getTaille()
				&& a.getY() >= 0 && a.getY() < plateau.getTaille()
				&& plateau.estLibre(a.getX(), a.getY()));
	}
	
	public List<Action> actionsPossibles() {
		return plateau.getToutesCasesLibres();
	}
	
	public Situation situationCourante() {
		Joueur[] joueurs = partie.getTousJoueurs();
		Symbole symb = null;
		symb = plateau.ligneGagnante();
		if (symb != null) { return new Victoire(joueurs[symb.ordinal()]); }
		
		symb = plateau.colonneGagnante();
		if (symb != null) { return new Victoire(joueurs[symb.ordinal()]); }
		
		symb = plateau.diagonaleGagnante();
		if (symb != null) { return new Victoire(joueurs[symb.ordinal()]); }
		
		if (plateau.estRempli()) { return new Egalite(); }
		else { return new EnCours(); }
	}
}
