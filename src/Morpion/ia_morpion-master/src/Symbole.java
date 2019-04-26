
public enum Symbole {
	X("X"),
	O("O"),
	E("E"),
	W("W");
	
	private String affichage;
	
	private Symbole(String affichage) {
		this.affichage = affichage;
	}
	
	public String toString() {
		return affichage;
	}
}
