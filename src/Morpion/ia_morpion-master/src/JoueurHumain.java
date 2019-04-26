import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JoueurHumain extends Joueur {

	private BlockingQueue<Action> actionAJouer;

	public JoueurHumain(int id, String nom) {
		super(id, nom);
		this.actionAJouer = new LinkedBlockingQueue<Action>(1);
	}
	
	@Override
	public Action choisirAction(Etat etat) throws Exception {
		actionAJouer.clear();
		return actionAJouer.take();
	}

	@Override
	public void proposerAction(Action action) {
		actionAJouer.offer(action);
	}

}
