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

	private static final int MaxLevelOfRecursion = 10;

	public JoueurIAAlphaBeta(String nom) {
		super(nom);
		//this.Alpha = -1; // initialiser alpha et beta à plus ou moins l'infini
		this.Alpha = -2147483646;
		//this.Beta = 1;
		this.Beta = 2147483646;
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

            	//System.out.println("symboles identiques case 0,0 d'affilée "+state.getPlateau().nSymbolesConsecutifs2(0,0,1,0));
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
				//return 1;
				return 2147483646-levelOfRecursion;
			} else{
				System.out.println("victoire du joueur");
				//return -1;
				return -2147483647+levelOfRecursion;
			}
		} else if(situation instanceof Egalite){
			System.out.println("Egalité !");
			return 0;
		}else if(situation instanceof EnCours){
			List<Morpion.ia.Action> actions = etat.actionsPossibles();
			if(etat.getIdJoueurCourant() == this.getID()){ //noeud max
				//TODO if reached max profondeur to look
				if(levelOfRecursion>=MaxLevelOfRecursion){
					int heuristique = etat.getPlateau().heuristique(Symbole.values()[etat.getIdJoueurCourant()]);
					etat.setIdJoueurCourant(etat.getIdJoueurCourant()+1);
					int newid = etat.getIdJoueurCourant();
					heuristique-= etat.getPlateau().heuristique(Symbole.values()[newid]);
					heuristique+=levelOfRecursion;
					return heuristique;
				}
				for(int i = 0; i < actions.size() && alpha < beta; i++){
					Etat nouvEtat = etat.clone();
					nouvEtat.jouer(actions.get(i));
					nouvEtat.setIdJoueurCourant(nouvEtat.getIdJoueurCourant() + 1);
					alpha = max(alpha,alphaBeta(alpha, beta,nouvEtat,levelOfRecursion++));
				}
				return alpha;
			} else { //noeud min
				//TODO if reached max profondeur to look
				if(levelOfRecursion>=MaxLevelOfRecursion){
					int heuristique = -etat.getPlateau().heuristique(Symbole.values()[etat.getIdJoueurCourant()]);
					etat.setIdJoueurCourant(etat.getIdJoueurCourant()+1);
					int newid = etat.getIdJoueurCourant();
					heuristique+= etat.getPlateau().heuristique(Symbole.values()[newid]);
					heuristique-=levelOfRecursion;
					return heuristique;
				}
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
