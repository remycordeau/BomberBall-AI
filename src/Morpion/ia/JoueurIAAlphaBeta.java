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
		this.Alpha = 2147483646; // initialiser alpha et beta à plus ou moins l'infini
		this.Beta = -2147483647;
	}


	@Override
	public Morpion.ia.Action choisirAction(Etat etat) {
        this.joueurCourant = etat.getIdJoueurCourant();
        actionsPossibles = etat.actionsPossibles(); // actions possibles pour les deux joueurs
		int bestAlpha=-2147483647;
		int currentAlpha;
		Morpion.ia.Action bestAction=actionsPossibles.get(0);
        try {
        	int k =0;
            for(Morpion.ia.Action action : actionsPossibles){
            	/*System.out.println("try: "+k);
            	k++;*/
            	Etat state=etat.clone();
            	state.jouer(action);
                currentAlpha=alphaBeta(this.Alpha,this.Beta,state,1);
                if(currentAlpha>bestAlpha){
                	bestAlpha=currentAlpha;
                	bestAction=action;
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Liste des actions possibles vide ou action non existante");
        }
		return  bestAction;
	}

	public int alphaBeta(int alpha, int beta, Etat etat,int levelOfRecusrion) {
	    /*if(){
            //regarder la classe situation pour décrire un état final
        }
        else*/
		actionsPossibles = etat.actionsPossibles();
		//System.out.println("there are "+actionsPossibles.size()+" possible actions");
		/*
		if(actionsPossibles.size()==0){
			System.out.println("no more moves possible");
			Situation st = etat.situationCourante();
			if(st instanceof Victoire){
				if(((Victoire) st).getVainqueur().getID()==joueurCourant){
					return 1;
				}else {
					return -1;
				}
			}else if(st instanceof Egalite){
				return 0;
			}else{
				System.err.println("no moves possible, no winner, no draw, something went wrong");
			}
		}*/
		Situation st = etat.situationCourante();
		if(st instanceof Victoire){
			if(((Victoire) st).getVainqueur().getID()==joueurCourant){
				System.out.println("ai won simulation");
				return 2147483646-levelOfRecusrion;
			}else {
				System.out.println("ai lost simulation");
				return -2147483647+levelOfRecusrion;
			}
		}else if(st instanceof Egalite) {
			System.out.println("ai draw simulation");
			return 0;
		}
		else if (st instanceof EnCours){
			if (etat.getIdJoueurCourant() == joueurCourant) { // si le noeud est de type max, ie c'est à l'ai de jouer
				//System.out.println("alpha playerId: "+etat.getIdJoueurCourant());
				for (int i = 0; i < actionsPossibles.size() && beta<alpha; i++) {
					Etat nouvEtat = etat.clone();
					Morpion.ia.Action action =actionsPossibles.get(i);
					this.setActionMemorisee(action);
					//System.out.println("current playerId :"+nouvEtat.getIdJoueurCourant());
					nouvEtat.jouer(action);
					//nouvEtat.setIdJoueurCourant(nouvEtat.getIdJoueurCourant()+1);
					//System.out.println("time for playerId :"+nouvEtat.getIdJoueurCourant()+" to play");
					alpha = max(alpha, alphaBeta(alpha,beta,nouvEtat,levelOfRecusrion+1));
				}
				return alpha;
			} else { // went from else if to else to allow compilation.
				// si le noeud est de type min, ie c'est à l'autre de jouer
				//System.out.println("beta playerId: "+etat.getIdJoueurCourant());
				for (int i = 0; i < actionsPossibles.size() && alpha<beta; i++) {
					Etat nouvEtat = etat.clone();
					//System.out.println("current playerId :"+nouvEtat.getIdJoueurCourant());
					nouvEtat.jouer(actionsPossibles.get(i));
					//nouvEtat.setIdJoueurCourant(nouvEtat.getIdJoueurCourant()+1);
					//System.out.println("time for playerId :"+nouvEtat.getIdJoueurCourant()+" to play");
					beta = min(beta, alphaBeta(alpha,beta,nouvEtat,levelOfRecusrion+1));
				}
				return beta;
			}
		}
		else{
			System.err.println("something went wrong");
			return 0;
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
