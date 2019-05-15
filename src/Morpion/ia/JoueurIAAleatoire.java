package Morpion.ia;

import java.util.List;
import java.util.Random;


/**
 * Implémentation d'un joueur artificiel qui joue de manière aléatoire
 */
public class JoueurIAAleatoire extends JoueurIA{

	/**
	 * Constructeur
	 *
	 * @param nom nom du joueur
	 */
	public JoueurIAAleatoire(String nom) {
		super(nom);
	}

	/**
	 * Choisit une action au hasard
	 *
	 * @param etat État actuel de la partie
	 * @return une action
	 */
	@Override
	public Action choisirAction(Etat etat) {
		Random aleatoire = new Random();
		List<Action> actionsPossibles = etat.actionsPossibles();
		int indiceAction = aleatoire.nextInt(actionsPossibles.size());
		return  actionsPossibles.get(indiceAction);
	}


}
