package com.glhf.bomberball.ai;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.Player;
import com.glhf.bomberball.utils.Action;
import org.lwjgl.Sys;

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

    private final int verbosity = 4;
    int finalscore=-1;

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

                System.out.println("le score espéré était "+finalscore);
                return actionRetournee;
            }
        }

        if(rechercheEffectuee){
            actionsAEffectuer.clear();
            System.out.println("le coup choisi est : "+this.actionToString(Action.ENDTURN));
            rechercheEffectuee=false;
            return Action.ENDTURN;

        }
        int bestAlpha = this.Alpha;
        int currentAlpha;
        int i =0;
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
                String branch = Integer.toString(i);
                AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,5,actionsATester,listeDActionPossible,branch);
                //state.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);
                if(returnObj.score>bestAlpha){
                    bestAlpha = returnObj.score;
                    this.setMemorizedAction(returnObj.actions.get(0));
                    actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                    finalscore=returnObj.score;
                }
                i++;
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

    private AlphaBetaReturnObj alphaBeta(int alpha, int beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions,MyArrayList<MyArrayList<Action>> retourActionsPossibles, String branch) {
        //on définit la situation actuelle

        System.out.println("alphabeta level "+leveOfRecursion+" branch "+branch);

        boolean victoire = true;
        boolean egalite = false;

        int numberAlive=0;
        int numberDead=0;


        System.out.println("about to get possible actions");
        if(!state.getCurrentPlayer().isAlive()){
            System.out.println("caution, the current player is dead : life = "+state.getCurrentPlayer().getLife());
        }

        Player winner = null;
        for (Player p : state.getPlayers()) {
            if (victoire && p.isAlive() && winner ==null) {
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
            victoire=false;
            egalite = true;
        }
        if(victoire && winner!=null){
            if(winner.getPlayerId() == this.getPlayerId()){
                //System.out.println("ia "+ this.getPlayerId()+" a gagné");
                //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(2147483646 - leveOfRecursion,actions,(MyArrayList)retourActionsPossibles);
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(5,actions,(MyArrayList)retourActionsPossibles);
                if(verbosity>2){
                    System.out.println("returned at victoire");
                }
                return ret;
            } else  {
                //System.out.println("ia "+ winner.getPlayerId()+" a gagné");
                //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-2147483646 + leveOfRecursion,actions,(MyArrayList)retourActionsPossibles);
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-5,actions,(MyArrayList)retourActionsPossibles);
                if(verbosity>2){
                    System.out.println("returned at defaite");
                }
                return ret;
            }
        } else if(egalite){
            System.out.println("Egalité !");
            AlphaBetaReturnObj ret = new AlphaBetaReturnObj(0,actions,(MyArrayList)retourActionsPossibles);
            if(verbosity>2){
                System.out.println("returned at egalite");
            }
            return ret;
        } else if(victoire && egalite){
            System.out.println("victoire && egalite, something went wrong");

        }if(leveOfRecursion > maxRecursion){
            // TODO choisir quoi retourner
            AlphaBetaReturnObj ret=null;
            if(state.getCurrentPlayerId()==this.getPlayerId()){
                ret = new AlphaBetaReturnObj(-2,actions,(MyArrayList)retourActionsPossibles);
            }else{
                ret = new AlphaBetaReturnObj(2,actions,(MyArrayList)retourActionsPossibles);
            }
            if(verbosity>2){
                System.out.println("returned at max recursion");
            }
            return ret;


        }
        else{ // partie en cours
            List<Action> possibleActions = state.getAllPossibleActions();
            if(verbosity>5){
                System.out.println();
                for (Action action: possibleActions
                     ) {
                    System.out.print(" | "+actionToString(action)+" | ");
                }System.out.println();
            }
            int oldPlayer = state.getCurrentPlayerId();
            if(state.getCurrentPlayerId() == this.getPlayerId()){ // noeud max

                if(!state.getCurrentPlayer().isAlive()){
                    AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-10,actions,(MyArrayList)retourActionsPossibles);
                    return ret;
                }
                for(int i = 0; i < possibleActions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    MyArrayList<Action> actions1 = actions.clone();
                    MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();
                    if(actions1==null) {
                        System.out.println("hey, actions1 is null man");
                    }
                    Action chosenAction = possibleActions.get(i);
                    if(chosenAction == null){
                        System.out.println("hey, the action you just picked is null");
                        AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-10,actions,(MyArrayList)retourActionsPossibles);
                        return ret;
                    }else{

                        if(verbosity>4){
                            System.out.println(actionToString(chosenAction));
                        }
                        newState.apply(chosenAction);
                        actions1.add(chosenAction);
                        MyArrayList<Action> lesTrucsARetourner = new MyArrayList<>();
                        for (Action actionARetourner:possibleActions
                        ) {lesTrucsARetourner.add(actionARetourner);
                        }
                        retourAcionsPossibles2.add(lesTrucsARetourner);

                        int theCurretPlayerId = state.getCurrentPlayerId();
                        if(oldPlayer!=theCurretPlayerId){
                            System.out.println("=========!!!!!!!!!!!   the player id indeed changed");
                        }
                        if(theCurretPlayerId!=oldPlayer || newState.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN){
                            //int numberOfPlayers = state.getPlayers().size();
                            //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1)% numberOfPlayers);

                            AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i);
                            if(returnObj.score>alpha){
                                alpha=returnObj.score;
                                actions = (MyArrayList<Action>) returnObj.actions;
                                retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                            }
                        } else {
                            AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i);
                            alpha = returnObj.score;
                            actions=(MyArrayList<Action>)returnObj.actions;
                            retourActionsPossibles=(MyArrayList)returnObj.actionsPossibles;
                        }
                    }
                }
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(alpha,actions,(MyArrayList)retourActionsPossibles);
                if(verbosity>2){
                    System.out.println("returned at reucursivity");
                }
                return ret;
            } else {
                if(!state.getCurrentPlayer().isAlive()){
                    AlphaBetaReturnObj ret = new AlphaBetaReturnObj(+10,actions,(MyArrayList)retourActionsPossibles);
                    return ret;
                }
                for(int i = 0; i < possibleActions.size() && alpha < beta; i++){
                    GameState newState = state.clone();
                    MyArrayList<Action> actions1 = actions.clone();
                    MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();
                    if(actions1==null){
                        System.out.println("hey, actions1 is null man");
                    }
                    Action chosenAction = possibleActions.get(i);
                    if(chosenAction == null){
                        System.out.println("hey, the action you just picked is null");
                        AlphaBetaReturnObj ret = new AlphaBetaReturnObj(10,actions,(MyArrayList)retourActionsPossibles);
                        return ret;
                    }else{
                        if(verbosity>4){
                            System.out.println(actionToString(chosenAction));
                        }
                        newState.apply(chosenAction);
                        actions1.add(chosenAction);
                        MyArrayList<Action> lesTrucsARetourner = new MyArrayList<>();
                        for (Action actionARetourner:possibleActions
                        ) {lesTrucsARetourner.add(actionARetourner);
                        }
                        retourAcionsPossibles2.add(lesTrucsARetourner);
                        int theCurretPlayerId = state.getCurrentPlayerId();
                        if(oldPlayer!=theCurretPlayerId){
                            System.out.println("=========!!!!!!!!!!!   the player id indeed changed");
                        }
                        if(oldPlayer!= theCurretPlayerId || newState.getCurrentPlayer().getNumberMoveRemaining() == 0 || chosenAction == Action.ENDTURN) {
                            //int numberOfPlayers = state.getPlayers().size();
                            //newState.setCurrentPlayerId((state.getCurrentPlayerId() + 1) % numberOfPlayers);
                            AlphaBetaReturnObj returnObj =alphaBeta(alpha, beta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i);
                            if(returnObj.score<alpha){
                                beta=returnObj.score;
                                actions=(MyArrayList<Action>)returnObj.actions;
                                retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                            }
                        }else{
                            AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i);
                            beta = returnObj.score;
                            retourActionsPossibles=(MyArrayList) returnObj.actionsPossibles;
                        }
                    }
                }
                AlphaBetaReturnObj ret = new AlphaBetaReturnObj(beta,actions,(MyArrayList)retourActionsPossibles);
                if(verbosity>2){
                    System.out.println("returned at reucursivity");
                }
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
