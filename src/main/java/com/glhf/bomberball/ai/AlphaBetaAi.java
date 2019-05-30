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

    private int Alpha;
    private int Beta;
    private List<Action> actionsPossibles;
    private MyArrayList<Action> actionsAEffectuer;
    MyArrayList<MyArrayList<Action>> listeDActionPossible;
    private boolean rechercheEffectuee=false;
    int actionsALaSuite=0;

    private final int verbosity = 3;
    int finalscore=-1;
    int theReturnedBadness = -1;

    public AlphaBetaAi(GameConfig config, String player_skin, int playerId) {
        super(config,player_skin,"AlphaBetaAI",playerId);
        //this.Alpha = -1; // initialiser alpha et beta à plus ou moins l'infini
        this.Alpha = -2147483646;
        //this.Beta = 1;
        this.Beta = 2147483646;
        // TODO potentiellement à changer
        this.actionsAEffectuer = new MyArrayList<>();
    }


    @Override
    public Action getMemorizedAction(){
        rechercheEffectuee=false;
        return memorizedAction;
    }


    @Override
    public Action choosedAction(GameState gameState) {

        if(rechercheEffectuee && actionsAEffectuer.size()<=0){
            rechercheEffectuee=false;
        }

        if(!rechercheEffectuee){
            //research of the moves to do
            MyArrayList<Action> actionsATester;
            actionsPossibles=gameState.getAllPossibleActions();
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

                    listeDActionPossible.add(new MyArrayList<>(actionsPossibles));
                    for(int j=2;j<15;j++){
                        System.out.println("search maxrecursion = "+j);
                        String branch = Integer.toString(i);
                        AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,j,actionsATester,listeDActionPossible,branch,true,1);

                        if(returnObj.score>bestAlpha){
                            System.out.println("old score "+bestAlpha+" new score "+returnObj.score);
                            bestAlpha = returnObj.score;
                            this.setMemorizedAction(returnObj.actions.get(0));
                            actionsAEffectuer.clear();
                            actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                            finalscore=returnObj.score;
                            theReturnedBadness=returnObj.badness;
                        }
                        i++;
                    }
                }
                rechercheEffectuee=true;

                System.out.println("enchainement de coups choisis");
                printActions(actionsAEffectuer);


            }catch (IndexOutOfBoundsException e){
                System.out.println("liste des actions possibles vide");
            }
        }

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

            printActions(lesActionsPossiblesACetEndroit);

            System.out.println("le score espéré était "+finalscore);
            if(finalscore>0){
                System.out.println("victoire en "+(2147483646-finalscore)+" coups");
            }
            actionsALaSuite++;
            return actionRetournee;
        }

        rechercheEffectuee=false;
        //System.out.println("something went deeply wrong");
        actionsAEffectuer.clear();

        return Action.ENDTURN;
    }

    private AlphaBetaReturnObj alphaBeta(int alpha, int beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions,MyArrayList<MyArrayList<Action>> retourActionsPossibles, String branch,boolean onJoueVraiment,int myBadness) {
        //on définit la situation actuelle

        //System.out.println();
        //System.out.println("alphabeta level "+leveOfRecursion+" branch "+branch);


        Player currentPlayer = state.getCurrentPlayer();
        Player otherPlayer = state.getPlayers().get((state.getCurrentPlayerId()+1)%2);

        int currentPlayerId = currentPlayer.getPlayerId();
        int otherPlayerId = otherPlayer.getPlayerId();



        //System.out.println("player "+currentPlayerId+" plays against"+otherPlayerId+", with "+currentPlayer.getNumberMoveRemaining()+" moves ermaining");

        if(onJoueVraiment){
            if(this.getPlayerId()!=state.getCurrentPlayerId()){
                onJoueVraiment=false;
                //System.out.println("on joue plus vraiment");
            }else{
                //System.out.println("on joue vraiment");
            }
        }

        if(leveOfRecursion > maxRecursion){
            // TODO choisir quoi retourner (heuristique)
            AlphaBetaReturnObj ret=null;
            if(state.getCurrentPlayerId()==this.getPlayerId()){
                ret = new AlphaBetaReturnObj(2,actions,(MyArrayList)retourActionsPossibles,myBadness);
            }else{
                ret = new AlphaBetaReturnObj(-2,actions,(MyArrayList)retourActionsPossibles,myBadness);
            }
            if(verbosity>2){
                //System.out.println("returned at max recursion");
            }
            return ret;
        }

        // partie en cours
        //System.out.println("about to get possible actions");
        List<Action> possibleActions=null;
        possibleActions = state.getAllPossibleActions();

        //System.out.println("move remaining "+currentPlayer.getNumberMoveRemaining()+" possible action"+actionToString(possibleActions.get(0)));
        //System.out.println("currentPlayerId : "+currentPlayer.getPlayerId()+" stateurrentPlayerId : "+state.getCurrentPlayerId()+" state.getCurrentPlayer.getPLayerId : "+state.getCurrentPlayer().getPlayerId());

        if(verbosity>5)printActions(possibleActions);

        int oldPlayer = currentPlayerId;

        for(int i = 0; i < possibleActions.size() && alpha < beta; i++){

            // On choisit une action possible
            Action chosenAction = possibleActions.get(i);

            if(verbosity>4){
                System.out.println(actionToString(chosenAction));
            }

            //apply action to a copy of current state
            GameState newState = state.clone();
            newState.apply(chosenAction);

            Player winner;
            if(newState.gameIsOver()){
                winner = newState.getWinner();
                if(winner!=null){
                    if(winner.getPlayerId() == this.getPlayerId()){
                        AlphaBetaReturnObj ret = new AlphaBetaReturnObj(2147483646 - leveOfRecursion,actions,(MyArrayList)retourActionsPossibles,myBadness);
                        //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(5,actions,(MyArrayList)retourActionsPossibles);
                        System.out.println("returned at victoire en "+leveOfRecursion+" coups, score "+(2147483646 - leveOfRecursion));
                        return ret;
                    } else  {
                        AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-2147483646+ leveOfRecursion,actions,(MyArrayList)retourActionsPossibles,myBadness);
                        //AlphaBetaReturnObj ret = new AlphaBetaReturnObj(-5,actions,(MyArrayList)retourActionsPossibles);
                        if(verbosity>2){
                            System.out.println("returned at defaite");
                        }
                        return ret;
                    }
                }else{
                    //System.out.println("Egalité !");
                    AlphaBetaReturnObj ret = new AlphaBetaReturnObj(0, actions, (MyArrayList) retourActionsPossibles,myBadness);
                    if (verbosity > 2) {
                        //System.out.println("returned at egalite");
                    }
                    return ret;
                }
            }

            MyArrayList<Action> actions1 = actions.clone();
            if(actions1==null) {
                //System.out.println("hey, actions1 is null man");
            }

            // remembering what actions were possible at that stage for debugging purposes
            MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();

            if(onJoueVraiment){
                actions1.add(chosenAction);
                retourAcionsPossibles2.add(new MyArrayList<>(possibleActions));
                //System.out.println("on ajoute un coup");
            }

            int recursivebadness=myBadness;
            if(state.getCurrentPlayerId()==currentPlayerId){
                recursivebadness=myBadness+3;
            }

            // on fait simuler le reste des actions
            //System.out.println("currentPlayerId : "+currentPlayerId+" stateurrentPlayerId : "+state.getCurrentPlayerId()+" state.getCurrentPlayer.getPLayerId : "+state.getCurrentPlayer().getPlayerId());
            AlphaBetaReturnObj returnObj = alphaBeta(alpha, beta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i,onJoueVraiment,recursivebadness);

            System.out.println("score "+returnObj.score+" alpha "+alpha+" beta "+beta);

            // TODO adapter alpha beta pour des noeuds max à la suite et des noeuds min à la suite.
            if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
                if(returnObj.score>alpha){
                    alpha=returnObj.score;
                    actions = ((MyArrayList<Action>) returnObj.actions).clone();
                    retourActionsPossibles=((MyArrayList) returnObj.actionsPossibles).clone();
                }
            }else{  // noeud min
                if(returnObj.score<beta) {
                    beta = returnObj.score;
                    actions = ((MyArrayList<Action>) returnObj.actions).clone();
                    retourActionsPossibles = ((MyArrayList) returnObj.actionsPossibles).clone();
                }
            }
        }
        AlphaBetaReturnObj ret;
        if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
            ret = new AlphaBetaReturnObj(alpha,actions,(MyArrayList)retourActionsPossibles,myBadness);
        }else{
            ret = new AlphaBetaReturnObj(beta,actions,(MyArrayList)retourActionsPossibles,myBadness);
        }
        if(verbosity>2){
            //System.out.println("returned at reucursivity");
        }
        return ret;
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

    public void printActions(List<Action> list){
        System.out.println();
        for (Action action: list
        ) {
            System.out.print(" | "+actionToString(action)+" | ");
        }System.out.println();
    }
}
