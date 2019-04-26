
public abstract class Joueur {
	private String nom;
	private int id;
	
	public Joueur(int id, String nom) {
		this.id = id;
		this.nom = nom;
	}
	
	public int getID() {
		return id;
	}
	
	public String getNom() {
		return nom;
	}
	
	public abstract Action choisirAction(Etat etat) throws Exception;

	public abstract void proposerAction(Action action);
}
