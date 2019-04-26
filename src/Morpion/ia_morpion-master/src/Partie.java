import java.util.Observable;

public class Partie extends Observable {
	private Joueur[] joueurs;
	private int nombreJoueurs;
	private Etat etatCourant;
	
	public Partie(int taille, Joueur j1, Joueur j2) throws Exception {
		this.nombreJoueurs = 2;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
	}
	
	public Partie(int taille, Joueur j1, Joueur j2, Joueur j3) throws Exception {
		this.nombreJoueurs = 3;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		this.joueurs[2] = j3;
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
	}
	
	public Partie(int taille, Joueur j1, Joueur j2, Joueur j3, Joueur j4) throws Exception {
		this.nombreJoueurs = 4;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		this.joueurs[2] = j3;
		this.joueurs[3] = j4;
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
	}
	
	public int getNombreJoueurs() {
		return nombreJoueurs;
	}
	
	public Etat getEtatCourant() {
		return etatCourant;
	}
	
	public int getIdJoueurCourant() {
		return etatCourant.getJoueurCourant();
	}
	
	public Joueur getJoueurCourant() {
		return joueurs[etatCourant.getJoueurCourant()];
	}
	
	public Joueur[] getTousJoueurs() {
		return joueurs;
	}
	
	public int getTaille() {
		return etatCourant.getPlateau().getTaille();
	}
	
	public void jouer(Action a) {
		etatCourant.jouer(a);
	}
	
	public void joueurSuivant() {
		etatCourant.setJoueurCourant(etatCourant.getJoueurCourant()+1);
	}
	
	public Situation getSituationCourante() {
		return etatCourant.situationCourante();
	}
	
	public void demarrer() {
		while (etatCourant.situationCourante() instanceof EnCours) {
			Action a;
			do {
			try {
				System.out.println("Au tour de "+getJoueurCourant().getNom());
				System.out.println(etatCourant.getPlateau());
				a = getJoueurCourant().choisirAction(etatCourant);
			} catch (Exception e) {
				a = etatCourant.getPlateau().getCaseLibre();
			}
			} while(!etatCourant.estPossible(a));
			etatCourant.jouer(a);
			joueurSuivant();
			setChanged();
			notifyObservers();
		}
	}
}
