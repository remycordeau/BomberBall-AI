
public class DejaOccupeException extends Exception {

	private static final long serialVersionUID = 1L;

	public DejaOccupeException(int x, int y) {
		super("La case (" + x + ", " + y + ")  est deja occupee.");
	}
}
