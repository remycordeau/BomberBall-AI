package Morpion.ia;

import java.util.concurrent.ExecutorService;


public class ThreadCalculIA extends Thread {


	    /**
	     * Morpion.ia.Joueur artificiel
	     */
	    private final JoueurIA ia;

	    /**
	     * Morpion.ia.Partie en cours
	     */
	    private final Etat etat;
	    
	    /**
	     * Service d'execution du thread
	     */
	    private final ExecutorService executor;

	    /**
	     * Coup choisi a l'issu de la recherche
	     */
	    private Action actionChoisie;

	    /**
	     * Constructor
	     * @param ia  joueur IA
	     * @param etat etat courant de la partie
	     * @param executor service 
	     */
	    public ThreadCalculIA (JoueurIA ia, Etat etat, ExecutorService executor) {
	    	super("Calcul");
	    	setName("Calcul");
	        this.ia = ia;
	        this.etat = etat;
	        this.executor = executor;
	        this.actionChoisie = null;
	    }
	    
	    public Action getActionChoisie() {
	        return actionChoisie;
	    }

	    /**
	     * Lance la recherche d'un nouveau coup dans un thread separe
	     */
	    @Override
	    public void run() {
		    try {
			    actionChoisie = ia.choisirAction(etat.clone());
		    } catch (Exception ex) {
			    ex.printStackTrace();
		    } finally {
			    executor.shutdownNow();
		    }
	    }
}
