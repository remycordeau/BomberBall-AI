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
    double finalscore=-1;
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
            double bestAlpha = this.Alpha;
            int currentAlpha;
            int i =0;
            try{
                for(int j=2;j<10;j++){
                    System.out.println("search maxrecursion = "+j);
                    String branch = Integer.toString(i);
                    GameState state = gameState.clone();
                    actionsATester = new MyArrayList<Action>();
                    listeDActionPossible = new MyArrayList<>();
                    AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,j,actionsATester,listeDActionPossible,branch,true,1);

                    if(returnObj.score>bestAlpha){
                        System.out.println("old score "+bestAlpha+" new score "+returnObj.score);
                        printActions(returnObj.actions);
                        bestAlpha = returnObj.score;
                        this.setMemorizedAction(returnObj.actions.get(0));
                        actionsAEffectuer.clear();
                        actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                        finalscore=returnObj.score;
                        theReturnedBadness=returnObj.badness;
                    }
                    i++;
                }
                rechercheEffectuee=true;

                System.out.println("enchainement de coups choisis");
                //printActions(actionsAEffectuer);


            }catch (IndexOutOfBoundsException e){
                System.out.println("liste des actions possibles vide");
            }
        }

        System.out.println("hey, j'ai "+actionsAEffectuer.size()+" actions à affectuer");
        if (actionsAEffectuer.size()>0){
            Action actionRetournee = actionsAEffectuer.get(0);
            //MyArrayList<Action> lesActionsPossiblesACetEndroit = (MyArrayList) listeDActionPossible.get(0);
            actionsAEffectuer.remove(0);
            if(actionRetournee==Action.ENDTURN){
                actionsAEffectuer.clear();
            }
            System.out.println("le coup choisi est : "+this.actionToString(actionRetournee));
            System.out.println("les actions possibles étaient alors:");

            //printActions(lesActionsPossiblesACetEndroit);

            System.out.println("le score espéré était "+finalscore);
            actionsALaSuite++;
            return actionRetournee;
        }

        rechercheEffectuee=false;
        actionsAEffectuer.clear();
        return Action.ENDTURN;
    }

    private AlphaBetaReturnObj alphaBeta(double alpha, double beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions,MyArrayList<MyArrayList<Action>> retourActionsPossibles, String branch,boolean onJoueVraiment,int myBadness) {

        GameState newState;
        MyArrayList<Action> actionsToReturn = actions.clone();
        double foundAlpha=alpha;
        double foundBeta=beta;

        if(this.getPlayerId()==state.getCurrentPlayerId()){
            System.out.println("we are playing");
        }else{
            System.out.println("we are not playing");
        }

        if(onJoueVraiment){
            if(this.getPlayerId()!=state.getCurrentPlayerId()){
                onJoueVraiment=false;
            }
        }

        if(leveOfRecursion > maxRecursion){
            // TODO choisir quoi retourner (heuristique)
            AlphaBetaReturnObj ret;
            double dist = 2*this.distanceBetweenPlayers(state);
            double score =  1/dist;
            //System.out.println("dist "+dist+" score "+score);
            if(state.getCurrentPlayerId()==this.getPlayerId()){
                ret = new AlphaBetaReturnObj(score,actions,(MyArrayList)retourActionsPossibles,myBadness);
            }else{
                ret = new AlphaBetaReturnObj(-score,actions,(MyArrayList)retourActionsPossibles,myBadness);
            }
            //System.out.println("returned at max recursion, score = "+dist);
            return ret;
        }
        List<Action> possibleActions=null;
        possibleActions = state.getAllPossibleActions();

        if(verbosity>5)printActions(possibleActions);


        for(int i = 0; i < possibleActions.size() && foundAlpha< foundBeta; i++){

            Action chosenAction = possibleActions.get(i);
            //System.out.println(actionToString(chosenAction));

            newState = state.clone();
            newState.apply(chosenAction);

            MyArrayList<Action> actions1 = actions.clone();

            // remembering what actions were possible at that stage for debugging purposes
            MyArrayList<MyArrayList<Action>> retourAcionsPossibles2 = retourActionsPossibles.clone();

            if(onJoueVraiment){
                actions1.add(chosenAction);
                retourAcionsPossibles2.add(new MyArrayList<>(possibleActions));
                //System.out.println("on ajoute un coup");
            }

            Player winner;
            if(newState.gameIsOver()){
                winner = newState.getWinner();
                if(winner!=null){
                    if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){
                        int possibleScore = 2147483646 - 2*leveOfRecursion;
                        if(possibleScore>foundAlpha){
                            actionsToReturn=actions1;
                            foundAlpha=possibleScore;
                        }
                    } else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){
                        int possibleScore = -2147483646 + 2*leveOfRecursion;
                        if(possibleScore>foundAlpha){
                            actionsToReturn=actions1;
                            foundAlpha=possibleScore;
                        }
                    }else if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){
                        int possibleScore = 2147483646 - 2*leveOfRecursion;
                        if(possibleScore<foundBeta){
                            actionsToReturn=actions1;
                            foundBeta=possibleScore;
                        }
                    }else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){
                        int possibleScore = - 2147483646 + 2*leveOfRecursion;
                        if(possibleScore<foundBeta){
                            actionsToReturn=actions1;
                            foundBeta=possibleScore;
                        }
                    }
                }else{
                    //System.out.println("Egalité !");
                    if(this.getPlayerId()==state.getCurrentPlayerId()){
                        if(0>foundAlpha){
                            actionsToReturn=actions1;
                            foundAlpha=0;
                        }
                    }else{
                        if(0<foundBeta){
                            actionsToReturn=actions1;
                            foundBeta=0;
                        }
                    }
                }
            }
            else{
                int recursivebadness=myBadness;
                if(newState.getCurrentPlayerId()==this.getPlayerId()){
                    recursivebadness=myBadness+3;
                }

                // on fait simuler le reste des actions
                AlphaBetaReturnObj returnObj = alphaBeta(foundAlpha, foundBeta,newState,leveOfRecursion+1,maxRecursion,actions1,retourAcionsPossibles2,branch+i,onJoueVraiment,recursivebadness);

                // TODO adapter alpha beta pour des noeuds max à la suite et des noeuds min à la suite.
                if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
                    if(returnObj.score>foundAlpha){
                        //System.out.println("max node, updated alpha");
                        foundAlpha=returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        retourActionsPossibles=((MyArrayList) returnObj.actionsPossibles).clone();
                    }
                }else{  // noeud min
                    if(returnObj.score<foundBeta) {
                        //System.out.println("min node, updated beta");
                        foundBeta = returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        retourActionsPossibles = ((MyArrayList) returnObj.actionsPossibles).clone();
                    }
                }
            }
        }
        AlphaBetaReturnObj ret;
        if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
            ret = new AlphaBetaReturnObj(foundAlpha,actionsToReturn,(MyArrayList)retourActionsPossibles,myBadness);
        }else {
            ret = new AlphaBetaReturnObj(foundBeta, actionsToReturn, (MyArrayList) retourActionsPossibles, myBadness);
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

    public double distanceBetweenPlayers(GameState state){
        Player p1 = state.getPlayers().get(0);
        int x1 = p1.getX();
        int y1 = p1.getY();
        Player p2 = state.getPlayers().get(1);
        int x2 = p2.getX();
        int y2 = p2.getY();
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
}
