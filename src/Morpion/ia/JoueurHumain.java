package Morpion.ia;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe représentant un joueur humain, choisissant son action via un périphérique d'entrée.
 */
public class JoueurHumain extends Joueur {

	/** File d'attente (de taille 1) pour stocker l'action à jouer */
	private BlockingQueue<Action> actionAJouer;

	/**
	 * Constructeur
	 *
	 * @param nom nom du joueur
	 */
	public JoueurHumain(String nom) {
		super(nom);
		this.actionAJouer = new LinkedBlockingQueue<Action>(1);
	}
	
	@Override
	public Action choisirAction(Etat etat) throws Exception {
		actionAJouer.clear();
		return actionAJouer.take();
	}

	/**
	 * Tente de mémoriser l'action proposée par le joueur humain. Si une action n'a pas déjà été proposée, l'action proposée est retenue. Rien n'est fait sinon.
	 *
	 * @param action action proposée
	 */
	@Override
	public void proposerAction(Action action) {
		actionAJouer.offer(action);
	}

}
