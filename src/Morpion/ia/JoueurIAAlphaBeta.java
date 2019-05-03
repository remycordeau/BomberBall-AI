package Morpion.ia;

import javax.swing.Action;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class JoueurIAAlphaBeta extends JoueurIA {

    //private static final int moinsInf = ;
    //private static final int PlusInf = ;
	private int Alpha;
	private int Beta;
	private List<Morpion.ia.Action> actionsPossibles;

	public JoueurIAAlphaBeta(String nom) {
		super(nom);
		//this.Alpha = ; // initialiser alpha et beta à plus ou moins l'infini
		//this.Beta = ;
	}


	@Override
	public Morpion.ia.Action choisirAction(Etat etat) {
	    Joueur joueurActuel;
        int joueurCourant = etat.getIdJoueurCourant();
        actionsPossibles = etat.actionsPossibles(); // actions possibles pour les deux joueurs
        try {
            for(Morpion.ia.Action action : actionsPossibles){
                alphaBeta(this.Alpha,this.Beta,etat);
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Liste des actions possibles vide ou action non existante");
        }
		return  actionsPossibles.get(0);
	}

	public int alphaBeta(int alpha, int beta, Etat etat) {
	    /*if(){
            //regarder la classe situation pour décrire un état final
        }
        else*/
        if (etat.getIdJoueurCourant() == joueurCourant) { // si le noeud est de type max, ie c'est à l'ai de jouer
            actionsPossibles = etat.actionsPossibles();
            for (int i = 0; i < actionsPossibles.size() && alpha < beta; i++) {
                Etat nouvEtat = etat.clone();
                nouvEtat.jouer(actionsPossibles.get(i));
                nouvEtat.setIdJoueurCourant(joueurCourant);
                alpha = max(alpha, alphaBeta(alpha,beta,nouvEtat));
            }
            return alpha;
        } else if () {
            for (int i = 0; i < actionsPossibles.size() && alpha < beta; i++) {
                Etat nouvEtat = etat.clone();
                nouvEtat.jouer(actionsPossibles.get(i));
                nouvEtat.setIdJoueurCourant(joueurCourant);
                beta = min(alpha, alphaBeta(alpha,beta,nouvEtat));
            }
            return beta;
        }
    }


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
