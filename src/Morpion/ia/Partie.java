package Morpion.ia;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;



// TODO: Auto-generated Javadoc
/**
 * Classe représentant une partie comme un liste de N joueurs et un état courant
 */
public class Partie extends Observable {
	
	/** Tableau des joueurs */
	private Joueur[] joueurs;
	
	/** Nombre de joueurs */
	private int nombreJoueurs;
	
	/** État actuel de la partie */
	private Etat etatCourant;
	
	/**
	 * Constructeur à 2 joueurs	
	 *
	 * @param taille Taille du plateau
	 * @param j1 joueur 1 (ID 0)
	 * @param j2 joueur 2 (ID 1)
	 * @throws Exception En cas d'erreur lors de l'initialisation de la partie
	 */
	public Partie(int taille, Joueur j1, Joueur j2) throws Exception {
		this.nombreJoueurs = 2;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		for (int i = 0; i < this.nombreJoueurs; i++) {
			this.joueurs[i].setId(i);
		}
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
		
		int aleatoire = ThreadLocalRandom.current().nextInt(0, nombreJoueurs);
		this.etatCourant.setIdJoueurCourant(aleatoire);
	}
	
	/**
	 * Constructeur à 3 joueurs
	 *
	 * @param taille Taille du plateau
	 * @param j1 joueur 1 (ID 0)
	 * @param j2 joueur 2 (ID 1)
	 * @param j3 joueur 3 (ID 2)
	 * @throws Exception En cas d'erreur lors de l'initialisation de la partie
	 */
	public Partie(int taille, Joueur j1, Joueur j2, Joueur j3) throws Exception {
		this.nombreJoueurs = 3;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		this.joueurs[2] = j3;
		for (int i = 0; i < this.nombreJoueurs; i++) {
			this.joueurs[i].setId(i);
		}
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
	}
	
	/**
	 * Constructeur à 4 joueurs
	 *
	 * @param taille Taille du plateau
	 * @param j1 joueur 1 (ID 0)
	 * @param j2 joueur 2 (ID 1)
	 * @param j3 joueur 3 (ID 2)
	 * @param j4 joueur 4 (ID 3)
	 * @throws Exception En cas d'erreur lors de l'initialisation de la partie
	 */
	public Partie(int taille, Joueur j1, Joueur j2, Joueur j3, Joueur j4) throws Exception {
		this.nombreJoueurs = 4;
		this.joueurs = new Joueur[this.nombreJoueurs];
		this.joueurs[0] = j1;
		this.joueurs[1] = j2;
		this.joueurs[2] = j3;
		this.joueurs[3] = j4;
		for (int i = 0; i < this.nombreJoueurs; i++) {
			this.joueurs[i].setId(i);
		}
		this.etatCourant = new Etat(this, new Plateau(taille), 0);
	}
	
	/**
	 * Retourne le nombre de joueurs
	 *
	 * @return un entier
	 */
	public int getNombreJoueurs() {
		return nombreJoueurs;
	}
	
	/**
	 * Gets the etat courant.
	 *
	 * @return the etat courant
	 */
	public Etat getEtatCourant() {
		return etatCourant;
	}
	
	/**
	 * Gets the id joueur courant.
	 *
	 * @return the id joueur courant
	 */
	public int getIdJoueurCourant() {
		return etatCourant.getIdJoueurCourant();
	}
	
	/**
	 * Retourne le joueur courant
	 *
	 * @return un joueur
	 */
	public Joueur getJoueurCourant() {
		return joueurs[etatCourant.getIdJoueurCourant()];
	}
	
	/**
	 * Gets the tous joueurs.
	 *
	 * @return the tous joueurs
	 */
	public Joueur[] getTousJoueurs() {
		return joueurs;
	}
	
	/**
	 * Retourne la taille du plateau
	 *
	 * @return un entier
	 */
	public int getTaille() {
		return etatCourant.getPlateau().getTaille();
	}
	
	/**
	 * Applique sur action sur l'état courant
	 *
	 * @param a action à appliquer
	 */
	public void jouer(Action a) {
		etatCourant.jouer(a);
	}
	
	/**
	 * Passe au joueur suivant
	 */
	public void joueurSuivant() {
		etatCourant.setIdJoueurCourant(etatCourant.getIdJoueurCourant()+1);
	}
	
	/**
	 * Retourne la situation de jeu actuelle (en cours, égalité ou victoire d'un joueur)
	 *
	 * @return une situation
	 */
	public Situation getSituationCourante() {
		return etatCourant.situationCourante();
	}
	
	private Action choixIA(JoueurIA ia) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
        ThreadCalculIA calcul = new ThreadCalculIA(ia, this.etatCourant, executor);
        executor.execute(calcul);
        try {
        	 if (!executor.awaitTermination(JoueurIA.TEMPS_DE_REFLEXION, TimeUnit.MILLISECONDS)) {
        		 executor.shutdown();
        	 }
        }catch (InterruptedException e) {
        	e.getStackTrace();
		}
        
        try {
        	calcul.join();
        }catch (InterruptedException e) {
        	 e.getStackTrace();
		}
        
        Action action;
        if (calcul.getActionChoisie() == null && ia.getActionMemorisee() == null) {
        	action = etatCourant.getPlateau().getCaseLibre();
        } else if (calcul.getActionChoisie() == null && ia.getActionMemorisee() != null) {
        	System.err.println("Aucune action choisie mais action mémorisée");
		    action = ia.getActionMemorisee();
		
		} else {
		    action = calcul.getActionChoisie();
		}
		

          // Terminer les threads de calcul éventuellement toujours en cours
		  for (Thread t : Thread.getAllStackTraces().keySet()) {
		      for (StackTraceElement ste : t.getStackTrace()) {
		          if (ste.getClassName().equals("Morpion.ia.ThreadCalculIA")) {
		              t.stop();
		          }
		      }
		  }
  
          try {
        	  Thread.sleep(200);
          } catch (InterruptedException ex) {
        	  ex.printStackTrace();
          }

		return action;
	}
	
	
	/**
	 * Démarre la partie (boucle de jeu)
	 */
	public void demarrer() {
		while (etatCourant.situationCourante() instanceof EnCours) {
			Action a;
			do {
			try {
				System.out.println("=================================================");
				System.out.println("Au tour de "+getJoueurCourant().getNom());
				System.out.println(etatCourant.getPlateau());
				if (getJoueurCourant() instanceof JoueurIA) {
					a = choixIA((JoueurIA) getJoueurCourant());
				}
				else {
					a = getJoueurCourant().choisirAction(etatCourant);
				}
			} catch (Exception e) {
				a = etatCourant.getPlateau().getCaseLibre();
			}
			} while(!etatCourant.estPossible(a));
			System.out.println("Morpion.ia.Action : " + a);
			etatCourant.jouer(a);
			joueurSuivant();
			setChanged();
			notifyObservers();
		}
	}
}
