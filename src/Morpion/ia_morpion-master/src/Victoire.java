
public class Victoire implements Situation {
	private Joueur vainqueur;
	
	public Victoire(Joueur j) {
		this.vainqueur = j;
	}
	
	public Joueur getVainqueur() {
		return vainqueur;
	}
}
