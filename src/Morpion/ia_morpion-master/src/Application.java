import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Application extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private Partie partie;
	private JLabel bandeau;
	private Bouton[][] tableau;
	
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
	
	public void rafraichir() {
		int taille = partie.getTaille();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				Symbole symb = partie.getEtatCourant().getPlateau().getCase(x, y);
				tableau[x][y].setText((symb != null)?symb.toString():"");
			}
		}
		Joueur joueurCourant = partie.getJoueurCourant();
		bandeau.setText("Joueur courant : " + joueurCourant.getNom() + " (J" + joueurCourant.getID() + ")");
		repaint();
	}
	
	public void finDePartie(Situation situation) {
		int taille = partie.getTaille();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				tableau[x][y].setEnabled(false);
			}
		}

		if (situation instanceof Egalite) {
			bandeau.setText("Egalite !");	
		}
		else {
			Joueur vainqueur = ((Victoire) situation).getVainqueur();
			bandeau.setText(vainqueur.getNom() + " (J" + vainqueur.getID() + ") gagne !");
		}
		
		repaint();
	}

	@Override
	public void update(Observable obs, Object arg) {
		rafraichir();
		
		Situation situation = partie.getSituationCourante();
		// Egalite ou Victoire
		if (!(situation instanceof EnCours)) {
			finDePartie(situation);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Joueur j1 = new JoueurHumain(0, "Bob");
		Joueur j2 = new JoueurIAAleatoire(1, "Alice");
		Partie p = new Partie(7, j1, j2);
		new Application(p);
		p.demarrer();
	}
}
