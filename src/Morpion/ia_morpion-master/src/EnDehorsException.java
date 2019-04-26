
public class EnDehorsException extends Exception {

	private static final long serialVersionUID = 1L;

	public EnDehorsException(int x, int y) {
		super("La case (" + x + ", " + y + ") est en dehors du plateau.");
	}
}
