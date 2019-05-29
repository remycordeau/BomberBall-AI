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
    private MyArrayList<Action> actionsAEffectuer;
    MyArrayList<MyArrayList<Action>> listeDActionPossible;
    private boolean rechercheEffectuee=false;

    private final int verbosity = 2;

    public AlphaBetaAi(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"RandomAI",playerId);
        //this.Alpha = -1; // initialiser alpha et beta à plus ou moins l'infini
        this.Alpha = -2147483646;
        //this.Beta = 1;
        this.Beta = 2147483646;
        // TODO potentiellement à changer
        this.actionsAEffectuer = new MyArrayList<>();
    }



    @Override
    public Action choosedAction(GameState gameState) {
        this.joueurCourant = this.getPlayerId();
        actionsPossibles = gameState.getAllPossibleActions();
        MyArrayList<Action> actionsATester;




        if(verbosity>1){
            System.out.println();
            for (Action action: actionsPossibles
            ) {
                System.out.print(" | "+actionToString(action)+" | ");
            }System.out.println();

            System.out.println();
            for (Action action: actionsAEffectuer
            ) {
                System.out.print(" | "+actionToString(action)+" | ");
            }System.out.println();
        }

        if(actionsAEffectuer!=null){
            System.out.println("hey, j'ai "+actionsAEffectuer.size()+" actions à affectuer");
            if (actionsAEffectuer.size()>0){
                Action actionRetournee = actionsAEffectuer.get(0);
                MyArrayList<Action> lesActionsPossiblesACetEndroit = (MyArrayList) listeDActionPossible.get(0);
                actionsAEffectuer.remove(0);
                if(actionRetournee==Action.ENDTURN){
                    actionsAEffectuer.clear();
                }
                System.out.println("le coup choisi est : "+this.actionToString(actionRetournee));
                System.out.println("les actions possibles étaient alors:");
                System.out.println();
                for (Action action: lesActionsPossiblesACetEndroit
                ) {
                    System.out.print(" | "+actionToString(action)+" | ");
                }System.out.println();
                return actionRetournee;
            }
        }

        if(rechercheEffectuee){
            actionsAEffectuer.clear();
            System.out.println("le coup choisi est : "+this.actionToString(Action.ENDTURN));
            return Action.ENDTURN;
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
                actionsATester = new MyArrayList<Action>();
                actionsATester.add(action);
                listeDActionPossible = new MyArrayList<>();
                MyArrayList<Action> lestrucsarajouter=new MyArrayList<Action>();
                for (Action actionarajouter:actionsPossibles)
                {
                    lestrucsarajouter.add(actionarajouter);

                }
                listeDActionPossible.add(lestrucsarajouter);


                int numberOfPlayers = state.getPlayers().size();

                AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,10,actionsATester,listeDActionPossible);
                //state.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);
                if(returnObj.score>bestAlpha){
                    bestAlpha = returnObj.score;
                    this.setMemorizedAction(returnObj.actions.get(0));
                    actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                }
            }
            rechercheEffectuee=true;
        }catch (IndexOutOfBoundsException e){
            System.out.println("liste des actions possibles vide");
        }

        if(actionsAEffectuer.size()<=0){
            if(this.getMemorizedAction()==Action.ENDTURN){
                actionsAEffectuer.clear();
            }
            System.out.println("le coup choisi est : "+this.getMemorizedAction());
            return this.getMemorizedAction();
        }else{
            if(actionsAEffectuer.get(0)==Action.ENDTURN){
                actionsAEffectuer.clear();
            }
            System.out.println("le coup choisi est : "+this.actionToString(actionsAEffectuer.get(0)));
            Action actionARenvoyer = actionsAEffectuer.get(0);
            actionsAEffectuer.remove(0);
            return actionARenvoyer;
        }

    }

    private AlphaBetaReturnObj alphaBeta(int alpha, int beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions,MyArrayList<MyArrayList<Action>> retourActionsPossibles) {
        //on définit la situation actuelle

        System.out.println("alphabeta level "+leveOfRecursion);

        boolean victoire = true;
        boolean egalite = false;

        int numberAlive=0;
        int numberDead=0;

        List<Action> possibleActions = state.getAllPossibleActions();

        Player winner = null;
        for (Player p : state.getPlayers()) {
            if (victoire && p.isAlive()) {
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
                //System.out.println("ia "+ this.getPlayerId()+" a gagné");
                //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(2147483646 - leveOfRecursion,actions,(MyArrayList)retourActionsPossibles);
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(5,actions,(MyArrayList)retourActionsPossibles);
                return ret;
            } else  {
                //System.out.println("ia "+ winner.getPlayerId()+" a gagné");
                //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-2147483646 + leveOfRecursion,actions,(MyArrayList)retourActionsPossibles);
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-5,actions,(MyArrayList)retourActionsPossibles);
                return ret;
            }
        } else if(egalite){
            System.out.println("Egalité !");
            AlphaBetaReturnObj ret = new AlphaBetaReturnObj(0,actions,(MyArrayList)retourActionsPossibles);
            return ret;
        } else if(victoire && egalite){
            System.out.println("victoire && egalite, something went wrong");

        }if(leveOfRecursion > maxRecursion){
            // TODO choisir quoi retourner
            AlphaBetaReturnObj ret = new AlphaBetaReturnObj(0,actions,(MyArrayList)retourActionsPossibles);
            return ret;
        }else{ // partie en cours

            if(verbosity>5){
                System.out.println();
                for (Action action: possibleActions
                     ) {
                    System.out.print(" | "+actionToString(action)+" | ");
                }System.out.println();
            }
            if(state.getCurrentPlayerId() == this.getPlayerId()){ // noeud max
                for(int i = 0; i < possibleActions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    MyArrayList<Action> actions1 = actions.clone();
                    MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();
                    if(actions1==null){
                        System.out.println("hey, actions1 is null man");
                    }
                    Action chosenAction = possibleActions.get(i);
                    if(verbosity>4){
                        System.out.println(actionToString(chosenAction));
                    }
                    newState.apply(chosenAction);
                    actions1.add(chosenAction);
                    retourAcionsPossibles2.add((MyArrayList<Action>) possibleActions);


                    if(state.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN){
                        int numberOfPlayers = state.getPlayers().size();
                        //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);
                        AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion++,maxRecursion,actions1,retourAcionsPossibles2);
                        if(returnObj.score>alpha){
                            alpha=returnObj.score;
                            actions = (MyArrayList<Action>) returnObj.actions;
                            retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                        }
                    } else {
                        AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion++,maxRecursion,actions1,retourAcionsPossibles2);
                        alpha = returnObj.score;
                        actions=(MyArrayList<Action>)returnObj.actions;
                        retourActionsPossibles=(MyArrayList)returnObj.actionsPossibles;
                    }
                }
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(alpha,actions,(MyArrayList)retourActionsPossibles);
                return ret;
            } else {
                for(int i = 0; i < possibleActions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    MyArrayList<Action> actions1 = actions.clone();
                    MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();
                    if(actions1==null){
                        System.out.println("hey, actions1 is null man");
                    }
                    Action chosenAction = possibleActions.get(i);
                    if(verbosity>4){
                        System.out.println(actionToString(chosenAction));
                    }
                    newState.apply(chosenAction);
                    actions1.add(chosenAction);
                    retourAcionsPossibles2.add((MyArrayList<Action>) possibleActions);
                    if(state.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN) {
                        int numberOfPlayers = state.getPlayers().size();
                        //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1) % numberOfPlayers);
                        AlphaBetaReturnObj returnObj =alphaBeta(alpha, beta,newState,leveOfRecursion++,maxRecursion,actions1,retourAcionsPossibles2);
                        if(returnObj.score<alpha){
                            beta=returnObj.score;
                            actions=(MyArrayList<Action>)returnObj.actions;
                            retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                        }
                    }else{
                        AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion++,maxRecursion,actions1,retourAcionsPossibles2);
                        beta = returnObj.score;
                        retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                    }
                }
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(beta,actions,(MyArrayList)retourActionsPossibles);
                return ret;
            }
        }
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
