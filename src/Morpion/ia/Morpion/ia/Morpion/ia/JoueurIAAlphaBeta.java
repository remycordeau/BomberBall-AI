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
	int joueurCourant;


	public JoueurIAAlphaBeta(String nom) {
		super(nom);
		this.Alpha = -1; // initialiser alpha et beta à plus ou moins l'infini
		this.Beta = 1;
	}


	@Override
	public Morpion.ia.Action choisirAction(Etat etat) {
        this.joueurCourant = this.getID();
        actionsPossibles = etat.actionsPossibles(); // actions possibles pour les deux joueurs
		int bestAlpha=this.Alpha;
		int currentAlpha;
        try {
            for(Morpion.ia.Action action : actionsPossibles){
            	Etat state=etat.clone();
            	state.jouer(action);
            	state.setIdJoueurCourant(state.getIdJoueurCourant() +1);
                currentAlpha=alphaBeta(this.Alpha,this.Beta,state,1);
                //System.out.println("ca "+ currentAlpha);
                //System.out.println("ba "+bestAlpha);
				if(currentAlpha>bestAlpha){
					bestAlpha=currentAlpha;
                	this.setActionMemorisee(action);
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Liste des actions possibles vide ou action non existante");
        }
		return this.getActionMemorisee();
	}

	public int alphaBeta(int alpha, int beta, Etat etat,int levelOfRecursion) {
		Situation situation = etat.situationCourante();
		//System.out.println("situation courante : " + situation.toString());
		if(situation instanceof Victoire){
			if(((Victoire) situation).getVainqueur().getID() == this.getID()){
				System.out.println("victoire de l'ia");
				return 1;
			} else{
				System.out.println("victoire du joueur");
				return -1;
			}
		} else if(situation instanceof Egalite){
			System.out.println("Egalité !");
			return 0;
		}else if(situation instanceof EnCours){
			List<Morpion.ia.Action> actions = etat.actionsPossibles();
			if(etat.getIdJoueurCourant() == this.getID()){ //noeud max
				for(int i = 0; i < actions.size() && alpha < beta; i++){
					Etat nouvEtat = etat.clone();
					nouvEtat.jouer(actions.get(i));
					nouvEtat.setIdJoueurCourant(nouvEtat.getIdJoueurCourant() + 1);
					alpha = max(alpha,alphaBeta(alpha,beta,nouvEtat,levelOfRecursion++));
				}
				return alpha;
			} else { //noeud min
				for(int i = 0; i < actions.size() && alpha < beta; i++){
					Etat nouvEtat = etat.clone();
					nouvEtat.jouer(actions.get(i));
					nouvEtat.setIdJoueurCourant(nouvEtat.getIdJoueurCourant() + 1);
					beta = min(beta,alphaBeta(alpha,beta,nouvEtat,levelOfRecursion++));
				}
				return beta;
			}
		}
		return 0;
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
