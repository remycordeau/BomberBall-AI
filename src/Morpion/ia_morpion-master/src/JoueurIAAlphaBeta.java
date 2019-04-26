import java.util.List;
import java.util.Random;

public class JoueurIAAlphaBeta extends JoueurIA{

	private int Alpha;
	private int Beta;

	public JoueurIAAlphaBeta(int id, String nom) {
		super(id, nom);
	}


	@Override
	public Action choisirAction(Etat etat) {

		Random aleatoire = new Random();
		List<Action> actionsPossibles = etat.actionsPossibles();
		int indiceAction = aleatoire.nextInt(actionsPossibles.size());
		System.out.println(actionsPossibles.get(indiceAction));
		return  actionsPossibles.get(indiceAction);
	}

	@Override
	public void proposerAction(Action action) {
		// Rien (appel par l'interface graphique)

	}

	public int getAlpha() {
		return Alpha;
	}

	public void setAlpha(int alpha) {
		Alpha = alpha;
	}

	public int getBeta() {
		return Beta;
	}

	public void setBeta(int beta) {
		Beta = beta;
	}
}
