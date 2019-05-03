package Morpion.ia;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

// TODO: Auto-generated Javadoc
/**
 * Classe représentatant l'interface graphique pour une case du plateau
 */
public class Bouton extends JButton {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Instancie un nouveau bouton pour une case donnée.
	 *
	 * @param x Abscisse de la case
	 * @param y Ordonnée de la case
	 * @param partie Morpion.ia.Partie concernée par la case
	 */
	public Bouton(int x, int y, Partie partie) {
		super();
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				partie.getJoueurCourant().proposerAction(new Action(y, x));
			}
		});
	}
	
}
