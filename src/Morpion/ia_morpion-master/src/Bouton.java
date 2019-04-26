import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class Bouton extends JButton {
	private static final long serialVersionUID = 1L;

	
	public Bouton(int x, int y, Partie partie) {
		super();
		this.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(x+" "+y+" clique");
				partie.getJoueurCourant().proposerAction(new Action(y, x));
			}
		});
	}
	
}
