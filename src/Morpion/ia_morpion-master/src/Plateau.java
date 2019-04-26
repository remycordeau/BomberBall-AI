import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Plateau {
	private int taille;
	private Symbole[][] cases;
	private int besoinVictoire;
	
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
		cases = new Symbole[taille][taille];
	}
	
	public Plateau clone() {
		try {
			Plateau clone = new Plateau(taille);
			for (int x = 0; x < taille; x++) {
				for (int y = 0; x < taille; y++) {
					clone.cases[x][y] = this.cases[x][y];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public int getTaille() {
		return taille;
	}
	
	public Symbole getCase(int x, int y) {
		return cases[x][y];
	}
	
	public void setCase(int x, int y, Symbole s) {
		cases[x][y] = s;
	}
	
	public boolean estLibre(int x, int y) {
		return (getCase(x, y) == null);
	}
	
	public boolean estRempli() {
		boolean rempli = true;
		for (int x = 0; x < taille && rempli; x++) {
			for (int y = 0; y < taille && rempli; y++) {
				rempli &= (cases[x][y] != null);
			}
		}
		return rempli;
	}
	
	public List<Action> getToutesCasesLibres() {
		List<Action> libres = new LinkedList<Action>();
		for (int x = 0; x < taille; x++) {
			for (int y = 0; y < taille; y++) {
				libres.add(new Action(x, y));
			}
		}
		return libres;
	}
	
	public Action getCaseLibre() {
		// Lister toutes les cases libres
		List<Action> libres = getToutesCasesLibres();
		int aleatoire = ThreadLocalRandom.current().nextInt(0, libres.size());
		return libres.get(aleatoire);
	}

	private Symbole nSymbolesConsecutifs(int x, int y, int directionX, int directionY) {
		Symbole dernierSymbole = null;
		int nombreDernierSymbole = 0;
		while (x >= 0 && y >= 0 && x < taille && y < taille) {
			// Si case non vide
			if (getCase(x, y) != null) {
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
		return null;
	}
	
	public Symbole diagonaleGagnante() {
		boolean trouve = false;
		int i = -taille+besoinVictoire;
		Symbole symb = null;
		
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
			trouve |= (symb != null);
			
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
			trouve |= (symb != null);
			
			i++;
		}
		
		return symb;
	}

	public Symbole colonneGagnante() {
		boolean trouve = false;
		int i = 0;
		Symbole symb = null;
		
		while (i < taille && !trouve) {
			symb = nSymbolesConsecutifs(i, 0, 0, 1);
			trouve |= (symb != null);
			i++;
		}
		
		return symb;
	}

	public Symbole ligneGagnante() {
		boolean trouve = false;
		int i = 0;
		Symbole symb = null;
		
		while (i < taille && !trouve) {
			symb = nSymbolesConsecutifs(0, i, 1, 0);
			trouve |= (symb != null);
			i++;
		}
		
		return symb;
	}
	
	public String toString() {
		String str = " ";
		for (int x = 0; x < taille; x++) {
			str += " " + x;
		}
		str += "\n";
		for (int y = 0 ; y < taille; y++) {
			str += y+"|";
			for (int x = 0; x < taille; x++) {
				if (getCase(x, y) != null) {
					str += getCase(x, y);
				}
				else {
					str += " ";
				}
				str += "|";
			}
			str += "\n";
		}
		return str;
	}
	
	public static void main(String[] args) throws Exception {
		Plateau p1 = new Plateau(5);
		p1.setCase(1, 0, Symbole.X);
		p1.setCase(2, 1, Symbole.X);
		p1.setCase(3, 2, Symbole.X);
		p1.setCase(4, 3, Symbole.X);
		System.out.println(p1);
		System.out.println(p1.diagonaleGagnante());
		System.out.println(p1.ligneGagnante());
		System.out.println(p1.colonneGagnante());
		

		Plateau p2 = new Plateau(5);
		p2.setCase(4, 1, Symbole.X);
		p2.setCase(3, 2, Symbole.X);
		p2.setCase(2, 3, Symbole.X);
		p2.setCase(1, 4, Symbole.X);
		System.out.println(p2);
		System.out.println(p2.diagonaleGagnante());
		System.out.println(p2.ligneGagnante());
		System.out.println(p2.colonneGagnante());
		

		Plateau p3 = new Plateau(5);
		p3.setCase(2, 1, Symbole.X);
		p3.setCase(2, 2, Symbole.X);
		p3.setCase(2, 3, Symbole.X);
		p3.setCase(2, 4, Symbole.X);
		System.out.println(p3);
		System.out.println(p3.diagonaleGagnante());
		System.out.println(p3.ligneGagnante());
		System.out.println(p3.colonneGagnante());

		Plateau p4 = new Plateau(5);
		p4.setCase(1, 1, Symbole.X);
		p4.setCase(2, 1, Symbole.X);
		p4.setCase(3, 1, Symbole.X);
		p4.setCase(4, 1, Symbole.X);
		System.out.println(p4);
		System.out.println(p4.diagonaleGagnante());
		System.out.println(p4.ligneGagnante());
		System.out.println(p4.colonneGagnante());
	}
}
