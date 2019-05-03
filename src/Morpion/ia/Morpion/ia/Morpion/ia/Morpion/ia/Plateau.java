package Morpion.ia;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe représentant un plateau de jeu
 */
public class Plateau {
	
	/** Taille (largeur) du plateau */
	private int taille;
	
	/** Tableau des cases */
	private Symbole[][] cases;
	
	/** Nombre de symboles alignés nécessaires pour gagner (dépend de la taille du plateau) */
	private int besoinVictoire;
	
	/**
	 * Constructeur
	 *
	 * @param taille taille (largeur) du plateau
	 * @throws Exception si la taille du plateau est trop petite (taille minimum = 3)
	 */
	public Plateau(int taille) throws Exception {
		if (taille < 3) {
			throw new Exception("Taille du plateau trop petite (" + taille + "). La taille minimale et 3");
		}
		this.taille = taille;
		if (taille == 3) {
			this.besoinVictoire = 3;
		}
		else {
			this.besoinVictoire = 4;
		}
		this.cases = new Symbole[taille][taille];
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				this.cases[x][y] = Symbole.VIDE;
			}
		}
	}
	
	/**
	 * Clone le plateau courant
	 *
	 * @return un plateau
	 */
	public Plateau clone() {
		try {
			Plateau clone = new Plateau(taille);
			for (int x = 0; x < taille; x++) {
				for (int y = 0; y < taille; y++) {
					clone.cases[x][y] = this.cases[x][y];
				}
			}
			return clone;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Retourne la taille du plateau
	 *
	 * @return un entier
	 */
	public int getTaille() {
		return taille;
	}
	
	/**
	 * Retourne le symbole présent sur une case du plateau,
	 *
	 * @param x abscisse de la case
	 * @param y ordonnée de la case
	 * @return un symbole
	 */
	public Symbole getCase(int x, int y) {
		return cases[x][y];
	}
	
	/**
	 * Modifie le contenu d'une case
	 *
	 * @param x abscisse de la case
	 * @param y ordonnée de la case
	 * @param s un symbole
	 */
	public void setCase(int x, int y, Symbole s) {
		cases[x][y] = s;
	}
	
	/**
	 * Teste si une case est libre
	 *
	 * @param x abscisse de la case
	 * @param y ordonnée de la case
	 * @return vrai si la case est libre, faux sinon
	 */
	public boolean estLibre(int x, int y) {
		return (getCase(x, y) == Symbole.VIDE);
	}
	
	/**
	 * Teste si le plateau est rempli
	 *
	 * @return vrai si le plateau est rempli, faux sinon
	 */
	public boolean estRempli() {
		boolean rempli = true;
		for (int x = 0; x < taille && rempli; x++) {
			for (int y = 0; y < taille && rempli; y++) {
				rempli &= (cases[x][y] != Symbole.VIDE);
			}
		}
		return rempli;
	}
	
	/**
	 * Retourne toutes les cases libres (encapsulées par la classe Morpion.ia.Action)
	 *
	 * @return une liste d'actions (c.-à-d. de cases)
	 */
	public List<Action> getToutesCasesLibres() {
		List<Action> libres = new LinkedList<Action>();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				if (estLibre(x, y)) {
					libres.add(new Action(x, y));
				}
			}
		}
		return libres;
	}
	
	/**
	 * Retourne une case libre au hasard
	 *
	 * @return une case (instance de la classe Morpion.ia.Action)
	 */
	public Action getCaseLibre() {
		// Lister toutes les cases libres
		List<Action> libres = getToutesCasesLibres();
		int aleatoire = ThreadLocalRandom.current().nextInt(0, libres.size());
		return libres.get(aleatoire);
	}

	/**
	 * Teste si le plateau contient N symboles consecutifs en partant d'un certain point et en allant dans une certaine direction.
	 * Retourne le symbole présents N fois consécutives si une telle configuration est trouvée.
	 * Retourne le symbole VIDE sinon.
	 *
	 * @param x Point de départ en abscisse
	 * @param y Point de départ en ordonnée
	 * @param directionX direction en x
	 * @param directionY direction en y
	 * @return un symbole
	 */
	private Symbole nSymbolesConsecutifs(int x, int y, int directionX, int directionY) {
		Symbole dernierSymbole = Symbole.VIDE;
		int nombreDernierSymbole = 0;
		while (x >= 0 && y >= 0 && x < taille && y < taille) {
			// Si case non vide
			if (getCase(x, y) != Symbole.VIDE) {
				// Si même symbole que la case précédente
				if (dernierSymbole == getCase(x, y)) {
					nombreDernierSymbole++;
				}
				// Si symbole différent
				else {
					dernierSymbole = getCase(x, y);
					nombreDernierSymbole = 1;
				}
			}
			// Si case vide
			else {
				dernierSymbole = getCase(x, y);
				nombreDernierSymbole = 0;
			}
			// Si N d'affilée
			if (nombreDernierSymbole == besoinVictoire) {
				return dernierSymbole;
			}

			x += directionX;
			y += directionY;
		}
		return Symbole.VIDE;
	}
	
	/**
	 * Teste s'il y a une diagonale gagnante sur le plateau
	 *
	 * @return le symbole gagnant ou le symbole vide
	 */
	public Symbole diagonaleGagnante() {
		boolean trouve = false;
		int i = -taille+besoinVictoire;
		Symbole symb = Symbole.VIDE;
		
		while (i <= taille-besoinVictoire && !trouve) {
			int x, y;
			if (i > 0) {
				x = i;
				y = 0;
			}
			else {
				y = -i;
				x = 0;
			}

			symb = nSymbolesConsecutifs(x, y, 1, 1);
			trouve |= (symb != Symbole.VIDE);
			
			i++;
		}
		
		i = -taille+besoinVictoire;
		while (i <= taille-besoinVictoire && !trouve) {
			int x, y;
			if (i > 0) {
				x = taille-1-i;
				y = 0;
			}
			else {
				y = -i;
				x = taille-1;
			}
			
			symb = nSymbolesConsecutifs(x, y, -1, 1);
			trouve |= (symb != Symbole.VIDE);
			
			i++;
		}
		
		return symb;
	}

	/**
	 * Teste s'il y a une colonne gagnante sur le plateau
	 *
	 * @return le symbole gagnant ou le symbole vide
	 */
	public Symbole colonneGagnante() {
		boolean trouve = false;
		int i = 0;
		Symbole symb = Symbole.VIDE;
		
		while (i < taille && !trouve) {
			symb = nSymbolesConsecutifs(i, 0, 0, 1);
			trouve |= (symb != Symbole.VIDE);
			i++;
		}
		
		return symb;
	}

	/**
	 * Teste s'il y a une ligne gagnante sur le plateau
	 *
	 * @return le symbole gagnant ou le symbole vide
	 */
	public Symbole ligneGagnante() {
		boolean trouve = false;
		int i = 0;
		Symbole symb = Symbole.VIDE;
		
		while (i < taille && !trouve) {
			symb = nSymbolesConsecutifs(0, i, 1, 0);
			trouve |= (symb != Symbole.VIDE);
			i++;
		}
		
		return symb;
	}
	
	@Override
	public String toString() {
		String str = " ";
		for (int x = 0; x < taille; x++) {
			str += " " + x;
		}
		str += "\n";
		for (int y = 0 ; y < taille; y++) {
			str += y+"|";
			for (int x = 0; x < taille; x++) {
				str += getCase(x, y);
				str += "|";
			}
			str += "\n";
		}
		return str;
	}
	
	/**
	 * Méthode principale pour tester la classe
	 *
	 * @param args Paramètres de la ligne de commande
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Plateau p1 = new Plateau(5);
		p1.setCase(1, 0, Symbole.X);
		p1.setCase(2, 1, Symbole.X);
		p1.setCase(3, 2, Symbole.X);
		p1.setCase(4, 3, Symbole.X);
		System.out.println(p1);
		System.out.println("diag ? "+p1.diagonaleGagnante());
		System.out.println("ligne ? "+p1.ligneGagnante());
		System.out.println("colonne ? "+p1.colonneGagnante());
		

		Plateau p2 = new Plateau(5);
		p2.setCase(4, 1, Symbole.X);
		p2.setCase(3, 2, Symbole.X);
		p2.setCase(2, 3, Symbole.X);
		p2.setCase(1, 4, Symbole.X);
		System.out.println(p2);
		System.out.println("diag ? "+p2.diagonaleGagnante());
		System.out.println("ligne ? "+p2.ligneGagnante());
		System.out.println("colonne ? "+p2.colonneGagnante());
		

		Plateau p3 = new Plateau(5);
		p3.setCase(2, 1, Symbole.X);
		p3.setCase(2, 2, Symbole.X);
		p3.setCase(2, 3, Symbole.X);
		p3.setCase(2, 4, Symbole.X);
		System.out.println(p3);
		System.out.println("diag ? "+p3.diagonaleGagnante());
		System.out.println("ligne ? "+p3.ligneGagnante());
		System.out.println("colonne ? "+p3.colonneGagnante());

		Plateau p4 = new Plateau(5);
		p4.setCase(1, 1, Symbole.X);
		p4.setCase(2, 1, Symbole.X);
		p4.setCase(3, 1, Symbole.X);
		p4.setCase(4, 1, Symbole.X);
		System.out.println(p4);
		System.out.println("diag ? "+p4.diagonaleGagnante());
		System.out.println("ligne ? "+p4.ligneGagnante());
		System.out.println("colonne ? "+p4.colonneGagnante());
	}
}
