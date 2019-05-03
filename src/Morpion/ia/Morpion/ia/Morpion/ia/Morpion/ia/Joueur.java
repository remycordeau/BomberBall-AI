package Morpion.ia;
// TODO: Auto-generated Javadoc
/**
 * Classe abstraite représentant un joueur
 */
public abstract class Joueur {
	
	/** Nom du joueur */
	private String nom;
	
	/** Identifiant du joueur */
	private int id;
	
	/**
	 * Constructeur
	 *
	 * @param nom nom du joueur
	 */
	public Joueur(String nom) {
		this.id = -1;
		this.nom = nom;
	}
	
	/**
	 * Retourne l'identifiant du joueur
	 *
	 * @return un entier, -1 si le joueur n'est dans aucune partie
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * Modifie l'identifiant du joueur
	 * 
	 * @param id nouvel identifiant
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retourne le nom du joueur
	 *
	 * @return une chaîne de caractères
	 */
	public String getNom() {
		return nom;
	}
	
	/**
	 * Demande au joueur de choisir une action
	 *
	 * @param etat the etat
	 * @return the action
	 * @throws Exception the exception
	 */
	public abstract Action choisirAction(Etat etat) throws Exception;

	/**
	 * Faire une proposition d'action.
	 * Le politique d'adopter ou non l'action proposée relève du type de joueur.
	 * 
	 * @param action action proposée
	 */
	public abstract void proposerAction(Action action);
}
