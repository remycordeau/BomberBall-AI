package Morpion.ia;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

// TODO: Auto-generated Javadoc
/**
 * Classe représentant l'application totale avec la partie métier et l'interface graphique
 */
public class Application extends JFrame implements Observer {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** Morpion.ia.Partie en cours */
	private Morpion.ia.Partie partie;
	
	/** Bandeau d'affichage des informations de la partie */
	private JLabel bandeau;
	
	/** Représentation graphique du plateau de jeu */
	private Bouton[][] tableau;
	
	/**
	 * Constructeur
	 *
	 * @param partie La partie à jouer
	 */
	public Application(Partie partie) {
		super("Morpion AI");
		this.partie = partie;
		partie.addObserver(this);

		int taille = partie.getTaille();

		setSize(new Dimension(50*taille, 50*taille));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		this.bandeau = new JLabel("");
		add(bandeau, BorderLayout.NORTH);
		
		this.tableau = new Bouton[taille][taille];
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new GridLayout(taille, taille));
		
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				tableau[y][x] = new Bouton(x, y, partie);
				jPanel.add(tableau[y][x]);
			}
		}
		add(jPanel, BorderLayout.CENTER);
		rafraichir();
		setVisible(true);
	}
	
	/**
	 * Rafraichit l'interface graphique (bandeau et plateau).
	 */
	public void rafraichir() {
		int taille = partie.getTaille();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				Symbole symb = partie.getEtatCourant().getPlateau().getCase(x, y);
				tableau[x][y].setText(symb.toString());
			}
		}
		Joueur joueurCourant = partie.getJoueurCourant();
		bandeau.setText("Morpion.ia.Joueur courant : " + joueurCourant.getNom() + " (J" + joueurCourant.getID() + ")");
		repaint();
	}
	
	/**
	 * Déroule les actions à effectuer lors d'une fin de partie
	 *
	 * @param situation Type de fin de partie : égalite ou victoire d'un joueur
	 */
	public void finDePartie(Situation situation) {
		int taille = partie.getTaille();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				tableau[x][y].setEnabled(false);
			}
		}

		if (situation instanceof Egalite) {
			bandeau.setText("Morpion.ia.Egalite !");
		}
		else {
			Joueur vainqueur = ((Victoire) situation).getVainqueur();
			bandeau.setText(vainqueur.getNom() + " (J" + vainqueur.getID() + ") gagne !");
		}
		
		repaint();
	}

	/**
	 * Met à jour l'application par la notification d'un changement par une entité observée (la partie en l'occurrence).
	 *
	 * @param obs L'objet observé
	 * @param arg Éventuel argument
	 */
	@Override
	public void update(Observable obs, Object arg) {
		rafraichir();
		
		Situation situation = partie.getSituationCourante();
		// Morpion.ia.Egalite ou Morpion.ia.Victoire
		if (!(situation instanceof EnCours)) {
			finDePartie(situation);
		}
	}
	
	/**
	 * Méthode principale
	 *
	 * @param args Paramètres de la ligne de commande
	 * @throws Exception En cas d'erreur lors de la partie ou de son initialisation
	 */
	public static void main(String[] args) throws Exception {
		// Créer N joueurs
		Joueur j1 = new JoueurHumain("Bob");
		//Joueur j2 = new JoueurIAAleatoire("Alice");
		Joueur j2 = new JoueurIAAlphaBeta("IA_AlphaBeta");
		
		// Créer une partie
		int taillePlateau = 4;
		Partie p = new Partie(taillePlateau, j1, j2);
		
		//Créer une instance d'application graphique
		new Application(p);
		
		// Démarrer l'application
		p.demarrer();
	}
}
