package com.glhf.bomberball.ai;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AlphaBetaAi extends AbstractAI {

    private int joueurCourant;
    private int Alpha;
    private int Beta;
    private List<Action> actionsPossibles;

    private final int verbosity = 10;

    public AlphaBetaAi(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"RandomAI",playerId);
        //this.Alpha = -1; // initialiser alpha et beta à plus ou moins l'infini
        this.Alpha = -2147483646;
        //this.Beta = 1;
        this.Beta = 2147483646;
        // TODO potentiellement à changer
    }



    @Override
    public Action choosedAction(GameState gameState) {
        this.joueurCourant = this.getPlayerId();
        actionsPossibles = gameState.getAllPossibleActions();
        if(verbosity>5){
            System.out.println();
            for (Action action: actionsPossibles
            ) {
                System.out.print(" | "+actionToString(action)+" | ");
            }System.out.println();
        }
        int bestAlpha = this.Alpha;
        int currentAlpha;
        try{
            for(Action action : actionsPossibles){
                if(verbosity>5){
                    System.out.println(actionToString(action));
                }
                GameState state = gameState.clone();
                state.apply(action);
                int numberOfPlayers = state.getPlayers().size();
                //state.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);
                currentAlpha = alphaBeta(this.Alpha, this.Beta,state,1);
                if(currentAlpha>bestAlpha){
                    bestAlpha = currentAlpha;
                    this.setMemorizedAction(action);
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("liste des actions possibles vide");
        }
        return this.getMemorizedAction();
    }

    private int alphaBeta(int alpha, int beta, GameState state, int leveOfRecursion) {
        //on définit la situation actuelle

        boolean victoire = true;
        boolean egalite = false;

        int numberAlive=0;
        int numberDead=0;

        Player winner = null;
        for (Player p : state.getPlayers()) {
            if (winner == null && p.isAlive()) {
                winner = p;
                numberAlive++;
            } else if (p.isAlive()) {
                victoire = false;
                winner = null;
                numberAlive++;
            }else{
                numberDead++;
            }
        }if(numberAlive==0){
            egalite = true;
        }
        if(victoire){
            if(winner.getPlayerId() == this.getPlayerId()){
                System.out.println("ia "+ this.getPlayerId()+" a gagné");
                return 2147483646 - leveOfRecursion;
            } else  {
                System.out.println("ia "+ winner.getPlayerId()+" a gagné");
                return -2147483646 + leveOfRecursion;
            }
        } else if(egalite){
            System.out.println("Egalité !");
            return 0;
        } else if(victoire && egalite){
            System.out.println("victoire && egalite, something went wrong");
        } else { // partie en cours
            List<Action> actions = state.getAllPossibleActions();
            if(verbosity>5){
                System.out.println();
                for (Action action: actions
                     ) {
                    System.out.print(" | "+actionToString(action)+" | ");
                }System.out.println();
            }
            if(state.getCurrentPlayerId() == this.getPlayerId()){ // noeud max
                for(int i = 0; i < actions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    Action chosenAction = actions.get(i);
                    if(verbosity>4){
                        System.out.println(actionToString(chosenAction));
                    }
                    newState.apply(chosenAction);
                    if(state.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN){
                        int numberOfPlayers = state.getPlayers().size();
                        //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);
                        alpha = max(alpha,alphaBeta(alpha, beta,newState,leveOfRecursion++));
                    } else {
                        alpha = alphaBeta(alpha, beta,newState,leveOfRecursion++);
                    }
                }
                return alpha;
            } else {
                for(int i = 0; i < actions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    Action chosenAction = actions.get(i);
                    if(verbosity>4){
                        System.out.println(actionToString(chosenAction));
                    }
                    newState.apply(chosenAction);
                    if(state.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN) {
                        int numberOfPlayers = state.getPlayers().size();
                        //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1) % numberOfPlayers);
                        beta = min(alpha,alphaBeta(alpha, beta,newState,leveOfRecursion++));
                    }else{
                        beta = alphaBeta(alpha, beta,newState,leveOfRecursion++);
                    }
                }
                return beta;
            }
        }
        return 0;
    }

    public String actionToString(Action a){
        String ret="";
        switch (a){
            case ENDTURN:ret="ENDTURN";break;
            case MOVE_UP:ret="MOVE_UP";break;
            case DROP_BOMB:ret="DROP_BOMB";break;
            case MODE_BOMB:ret="MODE_BOMB";break;
            case MODE_MOVE:ret="MODE_MOVE";break;
            case MOVE_DOWN:ret="MOVE_DOWN";break;
            case MOVE_LEFT:ret="MOVE_LEFT";break;
            case MOVE_RIGHT:ret="MOVE_RIGHT";break;
            case NEXT_SCREEN:ret="NEXT_SCREEN";break;
            case DROP_BOMB_UP:ret="DROP_BOMB_UP";break;
            case MENU_GO_BACK:ret="MENU_GO_BACK";break;
            case DELETE_OBJECT:ret="DELETE_OBJECT";break;
            case DROP_BOMB_DOWN:ret="DROP_BOMB_DOWN";break;
            case DROP_BOMB_LEFT:ret="DROP_BOMB_LEFT";break;
            case DROP_BOMB_RIGHT:ret="DROP_BOMB_RIGHT";break;
            case DROP_SELECTED_OBJECT:ret="DROP_SELECTED_OBJECT";break;
        }
        return ret;
    }
}
