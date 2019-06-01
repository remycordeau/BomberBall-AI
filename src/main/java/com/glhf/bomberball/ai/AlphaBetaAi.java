/**
 * Classe implémentant une intelligence artificielle utilisant l'algorithme alpha-beta couplé à une heuristique
 */
package com.glhf.bomberball.ai;

import com.glhf.bomberball.config.GameConfig;
import com.glhf.bomberball.gameobject.*;
import com.glhf.bomberball.maze.Maze;
import com.glhf.bomberball.maze.cell.Cell;
import com.glhf.bomberball.utils.Action;
import com.glhf.bomberball.utils.Directions;
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
    private MyArrayList<MyArrayList<Action>> listeActionsPossibles;
    private boolean rechercheEffectuee=false;
    private int actionsALaSuite=0;

    private double beginOfGameCoordinatesUsX;
    private double beginOfGameCoordinatesUsY;
    private double beginOfGameCoordinatesHimX;
    private double beginOfGameCoordinatesHimY;

    private double lastPositionStartTurnUsX;
    private double lastPositionStartTurnUsY;
    private double lastPositionStartTurnHimX;
    private double lastPositionStartTurnHimY;

    private boolean beginOfGameInitialized=false;

    private double beginOfTurnCoordinatesUsX;
    private double beginOfTurnCoordinatesUsY;
    private double beginOfTurnCoordinatesHimX;
    private double beginOfTurnCoordinatesHimY;

    private double distanceCoveredThisGameUs=0;
    private double distanceCoveredThisGameHim=0;

    private double maxDistanceOnMap;
    private int nombreToursMaximal;

    private GameState dummyState;
    private  boolean dummyStateInitialized=false;
    private final int maxProfoncdeur = 10;

    private final int verbosity = 3;
    private double finalscore=-1;

    public AlphaBetaAi(GameConfig config, String player_skin, int playerId) {

        super(config,player_skin,"AlphaBetaAI",playerId);
        this.Alpha = -2147483646;
        this.Beta = 2147483646;
        this.actionsAEffectuer = new MyArrayList<>();
    }

    /**
     * Permet de récupérer l'action mémorisée et d'arreter la recherche
     * @return Action :  l'action mémorisée
     */
    @Override
    public Action getMemorizedAction(){
        rechercheEffectuee=false;
        return memorizedAction;
    }


    /**
     * @param gameState
     * @return Action : l'action a effectuer par l'intelligence artificielle
     */
    @Override
    public Action choosedAction(GameState gameState) {

        beginOfTurnCoordinatesUsX = gameState.getPlayers().get(this.getPlayerId()).getX();
        beginOfTurnCoordinatesUsY = gameState.getPlayers().get(this.getPlayerId()).getY();
        beginOfTurnCoordinatesHimX = gameState.getPlayers().get((this.getPlayerId()+1)%2).getX();
        beginOfTurnCoordinatesHimY = gameState.getPlayers().get((this.getPlayerId()+1)%2).getY();

        //calcul des différents paramètres nécessaires à l'heuristique : distance parcourue par les joeurs, positions des joueurs, distance maximale sur la carte
        if(!beginOfGameInitialized){// si début de partie
            beginOfGameCoordinatesUsX=beginOfTurnCoordinatesUsX;
            beginOfGameCoordinatesUsY=beginOfTurnCoordinatesUsY;
            beginOfGameCoordinatesHimX=beginOfTurnCoordinatesHimX;
            beginOfGameCoordinatesHimY=beginOfTurnCoordinatesHimY;

            lastPositionStartTurnUsX = beginOfTurnCoordinatesUsX;
            lastPositionStartTurnUsY = beginOfTurnCoordinatesUsY;
            lastPositionStartTurnHimX = beginOfTurnCoordinatesHimX;
            lastPositionStartTurnHimY = beginOfTurnCoordinatesHimY;
            maxDistanceOnMap = distanceBetweenCoordinates(0,0,gameState.getMaze().getHeight(),gameState.getMaze().getWidth());
            System.out.println("max distance on map "+maxDistanceOnMap);
            nombreToursMaximal = NumberTurn.getInstance().getNbTurn();

            beginOfGameInitialized=true;
        }else{
            Player us = gameState.getPlayers().get(this.getPlayerId());
            Player him = gameState.getPlayers().get((this.getPlayerId()+1)%2);
            distanceCoveredThisGameUs += distanceBetweenCoordinates(lastPositionStartTurnUsX,us.getX(),lastPositionStartTurnUsY,us.getY());
            distanceCoveredThisGameHim += distanceBetweenCoordinates(lastPositionStartTurnHimX,him.getX(),lastPositionStartTurnHimY,him.getY());

            lastPositionStartTurnUsX = beginOfTurnCoordinatesUsX;
            lastPositionStartTurnUsY = beginOfTurnCoordinatesUsY;
            lastPositionStartTurnHimX = beginOfTurnCoordinatesHimX;
            lastPositionStartTurnHimY = beginOfTurnCoordinatesHimY;
        }

        if(rechercheEffectuee && actionsAEffectuer.size()<=0){
            rechercheEffectuee=false;
        }

        if(!rechercheEffectuee){ // si l'on a pas encore effectué de recherche, alors l'on recherche les possibles actions à effectuer
            MyArrayList<Action> actionsATester;
            actionsPossibles=gameState.getAllPossibleActions();
            double bestAlpha = this.Alpha;
            int i =0;
            try{
                for(int j=2;j<10;j++){ // appel à alpha-beta
                    System.out.println("search maxrecursion = "+j);
                    String branch = Integer.toString(i);
                    GameState state = gameState.clone();
                    actionsATester = new MyArrayList<Action>();
                    AlphaBetaReturnObj returnObj = alphaBeta(this.Alpha, this.Beta,state,1,j,actionsATester,branch,true);
                    //listeActionsPossibles = new MyArrayList<>();

                    if(returnObj.score>bestAlpha){ //on mémorise la meilleure action à effectuer
                        System.out.println("old score "+bestAlpha+" new score "+returnObj.score);
                        printActions(returnObj.actions);
                        bestAlpha = returnObj.score;
                        this.setMemorizedAction(returnObj.actions.get(0));
                        actionsAEffectuer.clear();
                        actionsAEffectuer=(MyArrayList<Action>) returnObj.actions;
                        finalscore=returnObj.score;
                        System.out.println("message : "+returnObj.message);
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

        //on récupère et on retourne la prochaine action à effectuer
        if (actionsAEffectuer.size()>0){
            Action actionRetournee = actionsAEffectuer.get(0);
            //MyArrayList<Action> lesActionsPossiblesACetEndroit = (MyArrayList) listeActionsPossibles.get(0);
            actionsAEffectuer.remove(0);
            if(actionRetournee==Action.ENDTURN){ // si la prochaine action à effectuer est une fin de tour, on supprime les suivantes car ce ne sera plus à nous de jouer
                actionsAEffectuer.clear();
            }
            System.out.println("le coup choisi est : "+this.actionToString(actionRetournee));
            //System.out.println("les actions possibles étaient alors:");
            //printActions(lesActionsPossiblesACetEndroit);

            System.out.println("le score espéré était "+finalscore);
            actionsALaSuite++;
            return actionRetournee;
        }

        rechercheEffectuee=false;
        actionsAEffectuer.clear();
        return Action.ENDTURN;
    }

    /**
     *
     * @param alpha
     * @param beta
     * @param state, état sur lequel on applique notre algorithme
     * @param leveOfRecursion, niveau de profondeur d'exploration
     * @param maxRecursion, niveau maximal de profondeur d'exploration autorisé
     * @param actions
     * @param branch, branche de l'arbre que l'on explore
     * @param onJoueVraiment
     * @return AlphaBetaReturnObj contenant un score, une liste d'actions à effectuer
     */
    private AlphaBetaReturnObj alphaBeta(double alpha, double beta, GameState state, int leveOfRecursion, int maxRecursion, MyArrayList<Action> actions, String branch,boolean onJoueVraiment) {

        String message="default message";
        GameState newState;
        MyArrayList<Action> actionsToReturn = actions.clone();
        double foundAlpha=alpha;
        double foundBeta=beta;
        MyArrayList<Action> actions1;
        if(onJoueVraiment){
            if(this.getPlayerId()!=state.getCurrentPlayerId()){
                onJoueVraiment=false;
            }
        }

        // si l'on a dépassé la profondeur maximale autorisée, on choisit de terminer le tour
        if(leveOfRecursion > maxRecursion){

            GameState tryState = state.clone();
            tryState.apply(Action.ENDTURN);
            Player winner;
            actions1 = actions.clone();

            //si la partie est terminée suite à cette action : verification de victoire/defaite et mise à jour éventuelle de alpha/beta
            if(tryState.gameIsOver()){
                winner = tryState.getWinner(); //TODO : prendre state ou tryState ? il manque pas un return ici -> on devrait renvoyer le score non ?

                checkGameResultReturnObject returnObject = checkGameResult(winner,state,leveOfRecursion,foundAlpha,foundBeta,actionsToReturn,actions1,message);
                foundAlpha=returnObject.alpha;
                foundBeta=returnObject.beta;
                actionsToReturn=returnObject.actions;
                message=returnObject.message;

                if(tryState.getCurrentPlayerId() == this.getPlayerId()){ // our turn
                     return new AlphaBetaReturnObj(foundAlpha,actionsToReturn,message);
                } else { //enemy turn
                    return new AlphaBetaReturnObj(foundBeta,actionsToReturn,message);
                }
            }else{ // sinon on retourne l'heuristique // TODO choisir quoi retourner (heuristique)
                AlphaBetaReturnObj ret;
                double score;

                if(this.getPlayerId()==state.getCurrentPlayerId()){ //calcul des différents paramètres nécessaires à notre heuristique, si c'est à nous de jouer

                    initializeDummyState(state);
                    double distFromEnnemy = this.distanceBetweenPlayers(state);
                    double relativeDistFromEnnemy = distFromEnnemy/maxDistanceOnMap;

                    double scoreDistEnemy = -0.5*NumberTurn.getInstance().getNbTurn()*relativeDistFromEnnemy;

                    //double scoreEnemy=(maxDistanceOnMap - relativeDistFromEnnemy) * (nombreToursMaximal/4+(NumberTurn.getInstance().getNbTurn()+leveOfRecursion));

                    double distanceThisTurn = distanceFromBeginOfTurnPos(state.getCurrentPlayer());

                    double walkableScore = maxProfoncdeur*2-walkableDistanceToPlayer(state.getPlayers().get(state.getCurrentPlayerId()),state.getPlayers().get((state.getCurrentPlayerId()+1)%2),dummyState);

                    double relativedistanceThisTurn = distanceThisTurn;

                    double scoreGoodBombs = walkableDistanceFromBombToPlayer(state.getPlayers().get((this.getPlayerId()+1)%2),state);

                    int explode = toBeDestroyedWalls(state);

                    score = scoreDistEnemy+relativedistanceThisTurn+explode;
                    message = "score dist enemy : "+scoreDistEnemy+" score dist this turn : "+relativedistanceThisTurn+" score explode : "+explode;
                    if(walkableScore>-1000000){
                        double walkableScoreScore = 4*walkableScore*(1+0.05*NumberTurn.getInstance().getNbTurn());
                        score+= walkableScoreScore;
                        message+=" score walkable dist : "+walkableScoreScore;
                    }


                }else{ // on suppose que l'adversaire joue de manière "constante"
                    score=2; //à changer ?
                }
                /*
                double distanceFromBeginGamePos = distanceFromBeginOfGamePos(state.getCurrentPlayer());
                double relativeDistanceFromBeginGamePos = distanceFromBeginGamePos/maxDistanceOnMap;

                double distanceCoveredFromStart = 30* (distanceCoveredThisGame(state.getCurrentPlayer())+distanceThisTurn)*(nombreToursMaximal-(NumberTurn.getInstance().getNbTurn()));

                double score = scoreEnemy+relativedistanceThisTurn;
                System.out.println("dist "+dist+" score "+score);
                int score=2;*/

                if(state.getCurrentPlayerId()==this.getPlayerId()){ // on renvoie le score calculé par l'heuristique et la liste des actions associées
                    ret = new AlphaBetaReturnObj(score,actions,"max level from us, score = "+message);
                }else{
                    ret = new AlphaBetaReturnObj(-score,actions,"max level from other");
                }
                //System.out.println("returned at max recursion, score = "+dist);
                return ret;
            }
        }

        // exploration de l'arbre des actions possibles, algorithme alpha beta standard
        List<Action> possibleActions=null;
        possibleActions = state.getAllPossibleActions();

        if(verbosity>5)printActions(possibleActions);


        for(int i = 0; i < possibleActions.size() && foundAlpha< foundBeta; i++){

            Action chosenAction = possibleActions.get(i);
            newState = state.clone();
            newState.apply(chosenAction);
            actions1 = actions.clone();

            if(onJoueVraiment){
                actions1.add(chosenAction);
                //System.out.println("on ajoute un coup");
            }

            Player winner;
            if(newState.gameIsOver()){
                winner = newState.getWinner(); //TODO : prendre newState en paramètre ou state ?

                checkGameResultReturnObject returnObject = checkGameResult(winner,newState,leveOfRecursion,foundAlpha,foundBeta,actionsToReturn,actions1,message);
                foundAlpha=returnObject.alpha;
                foundBeta=returnObject.beta;
                actionsToReturn=returnObject.actions;
                message=returnObject.message;

            }
            else{
                // on fait simuler le reste des actions
                AlphaBetaReturnObj returnObj = alphaBeta(foundAlpha, foundBeta,newState,leveOfRecursion+1,maxRecursion,actions1,branch+i,onJoueVraiment);

                // TODO adapter alpha beta pour des noeuds max à la suite et des noeuds min à la suite.
                if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
                    if(returnObj.score>foundAlpha){
                        //System.out.println("max node, updated alpha");
                        foundAlpha=returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        message=returnObj.message;
                    }
                }else{  // noeud min
                    if(returnObj.score<foundBeta) {
                        //System.out.println("min node, updated beta");
                        foundBeta = returnObj.score;
                        actionsToReturn = (MyArrayList<Action>) returnObj.actions;
                        message=returnObj.message;
                    }
                }
            }
        }
        AlphaBetaReturnObj ret;
        if(state.getCurrentPlayerId() == this.getPlayerId()) { // noeud max
            ret = new AlphaBetaReturnObj(foundAlpha,actionsToReturn,message);
        }else {
            ret = new AlphaBetaReturnObj(foundBeta, actionsToReturn,message);
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
        for (Action action: list){
            System.out.print(" | "+actionToString(action)+" | ");
        }System.out.println();
    }

    /**
     * @param state : état courant
     * @return double : la distance entre les deux joueurs
     */
    public double distanceBetweenPlayers(GameState state){
        Player p1 = state.getPlayers().get(0);
        int x1 = p1.getX();
        int y1 = p1.getY();
        Player p2 = state.getPlayers().get(1);
        int x2 = p2.getX();
        int y2 = p2.getY();
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public double distanceFromBeginOfGamePos(Player player){
        double x1 = player.getX();
        double y1 = player.getY();
        double x2,y2;
        if(player.getPlayerId()==this.getPlayerId()){
            x2 = this.beginOfGameCoordinatesUsX;
            y2 = this.beginOfGameCoordinatesUsY;
        }else{
            x2 = this.beginOfGameCoordinatesHimX;
            y2 = this.beginOfGameCoordinatesHimY;
        }
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }


    public double distanceFromBeginOfTurnPos(Player player){
        double x1 = player.getX();
        double y1 = player.getY();
        double x2,y2;
        if(player.getPlayerId()==this.getPlayerId()) {
            x2 = beginOfTurnCoordinatesUsX;
            y2 = beginOfTurnCoordinatesUsY;
        }else{
            x2 = beginOfTurnCoordinatesHimX;
            y2 = beginOfTurnCoordinatesHimY;
        }
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public double distanceBetweenCoordinates(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }

    public double distanceCoveredThisGame(Player player){
        if(player.getPlayerId()==this.getPlayerId()){
            return distanceCoveredThisGameUs;
        }else{
            return distanceCoveredThisGameHim;
        }
    }

    public double walkableDistanceToPlayer(Player myPlayer,Player Ennemy,GameState state){
        // uses dummyState
        MyArrayList<Player> players = new MyArrayList<Player>();
        myPlayer.setPlayerId(0);
        players.add(myPlayer);
        state.getMaze().setPlayers(players);
        state.setCurrentPlayerId(0);
        myPlayer=state.getCurrentPlayer();

        double walked = walkableDistanceToPlayer(myPlayer.getCell(),Ennemy,state,0,0,Directions.DOWN,maxProfoncdeur,2147483646);
        return walked;
    }

    public double walkableDistanceFromBombToPlayer(Player Ennemy,GameState state){
        double ret=0;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell[] cell1:state.getMaze().getCells()
        ) {
            for (Cell cell:cell1
            ) {
                cells.add(cell);
            }
        }

        Cell currentCell=null;

        for (Cell cell:cells) {
            boolean containsBomb=false;
            for (GameObject object :cell.getGameObjects()){
                if(object instanceof Bomb){
                    containsBomb=true;
                }
            }
            if(containsBomb){
                ret+=walkableDistanceToPlayer(cell,Ennemy,state,0,0,Directions.DOWN,maxProfoncdeur,0);
            }
        }
        return ret;
    }

    public double walkableDistanceToPlayer(Cell cell, Player Ennemy, GameState state, int walked,int profondeur, Directions forbiddenDirection,int limit,int defaut){
        if(cell.getX()==Ennemy.getX() && cell.getY()==Ennemy.getY()){
            //we found the path to the enemy
            return walked;
        }else if(profondeur>limit){
            return 2147483646;
            //return 2* distanceBetweenCoordinates(cell.getX(),cell.getY(),Ennemy.getX(),Ennemy.getY());
        }

        double minFound=2147483646;
        double found;
        List<Cell> adjacentCells = cell.getAdjacentCells();

        // Right
        if(forbiddenDirection!=Directions.RIGHT || profondeur==0){
            if (adjacentCells.get(0) != null) {
                if (adjacentCells.get(0).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(0),Ennemy,state,walked+1,profondeur+1,Directions.RIGHT.opposite(),limit,defaut);
                    if(found<minFound){
                        minFound=found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(0))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(0),Ennemy,state,walked+2,profondeur+1,Directions.RIGHT.opposite(),limit,defaut);
                    if(found<minFound){
                        minFound=found;
                    }
                }
            }
        }
        // Left
        if(forbiddenDirection!=Directions.LEFT || profondeur==0) {
            if (adjacentCells.get(2) != null) {
                if (adjacentCells.get(2).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(2), Ennemy, state, walked + 1, profondeur + 1, Directions.LEFT.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(2))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(2), Ennemy, state, walked + 2, profondeur + 1, Directions.LEFT.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        // Up
        if(forbiddenDirection!=Directions.UP || profondeur==0) {
            if (adjacentCells.get(1) != null) {
                if (adjacentCells.get(1).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(1), Ennemy, state, walked + 1, profondeur + 1, Directions.UP.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(1))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(1), Ennemy, state, walked + 2, profondeur + 1, Directions.UP.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        // Down
        if(forbiddenDirection!=Directions.DOWN || profondeur==0) {
            if (adjacentCells.get(3) != null) {
                if (adjacentCells.get(3).isWalkable()) {
                    found = walkableDistanceToPlayer(adjacentCells.get(3), Ennemy, state, walked + 1, profondeur + 1, Directions.DOWN.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
                if (cellIsDestructible(adjacentCells.get(3))) {
                    found = walkableDistanceToPlayer(adjacentCells.get(3), Ennemy, state, walked + 2, profondeur + 1, Directions.DOWN.opposite(), limit,defaut);
                    if (found < minFound) {
                        minFound = found;
                    }
                }
            }
        }
        return minFound;
    }

    public void initializeDummyState(GameState state){
        Maze maze = (Maze) state.getMaze().clone();

        //on enlève les players.
        maze.setPlayers(new ArrayList<Player>());

        dummyState = new GameState(maze,0,0);
    }

    public boolean cellIsDestructible(Cell cell){
        boolean ret = false;
        for (GameObject object : cell.getGameObjects()){
                if(object instanceof DestructibleWall){
                    ret=true;
                }
        }
        return ret;
    }

    public int toBeDestroyedWalls(GameState state){
        int ret=0;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        for (Cell[] cell1 : state.getMaze().getCells()){
            for (Cell cell:cell1){
                cells.add(cell);
            }
        }
        int retplus;
        Cell currentCell=null;
        for (Cell cell : cells) {
            boolean containsBomb=false;
            for (GameObject object:cell.getGameObjects()){
                if(object instanceof Bomb){
                    retplus=0;
                    for (Directions dir:Directions.values()){
                        boolean continuer=true;
                        for (int l=0;l<initial_bomb_range && continuer;l++){
                            currentCell=cell.getAdjacentCell(dir);
                            if(currentCell!=null){
                                if (cellIsDestructible(currentCell)) {
                                    retplus=retplus+1;
                                    continuer = false;
                                }else if(!currentCell.isWalkable()){
                                    continuer=false;
                                }
                            }
                        }
                    }
                    //System.out.println("retplus = "+retplus);
                    if(retplus<=0){
                        //System.out.println("bad retplus");
                        ret=ret-1000;
                    }else{
                        ret=ret+retplus;
                    }
                }
            }
        }
        //System.out.println("goodBomb "+ret);
        return ret;
    }

    /**
     * Examine la situation courante si la partie est terminée et met éventuellement à jour alpha ou beta avec une meilleure valeur
     * @param winner : vainqueur de la partie
     * @param state : état courant
     * @param leveOfRecursion : niveau maximal de profondeur
     * @param foundAlpha : meilleur alpha pour le moment
     * @param foundBeta : meilleur beta pour le moment
     * @param actionsToReturn : liste d'actions à renvoyer
     * @param actions1 : liste d'actions associées à l'état
     * @param message
     */
    public checkGameResultReturnObject checkGameResult(Player winner, GameState state, int leveOfRecursion, double foundAlpha, double foundBeta, List<Action> actionsToReturn, List<Action> actions1, String message){
        checkGameResultReturnObject ret;
        if(winner!=null){
            if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){ // on gagne et c'était notre tour de jouer
                int possibleScore = +2147483646 - 2*leveOfRecursion;
                if(possibleScore>foundAlpha){
                    actionsToReturn=actions1;
                    foundAlpha=possibleScore;
                    message="on gagne";
                }
            } else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()==state.getCurrentPlayerId()){ // on perd et c'était notre tour de jouer
                int possibleScore = -2147483646 + 2*leveOfRecursion;
                if(possibleScore>foundAlpha){
                    actionsToReturn=actions1;
                    foundAlpha=possibleScore;
                    message="on perd";
                }
            }else if(winner.getPlayerId() == this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){ // on gagne et c'était à l'adversaire de jouer
                int possibleScore = +2147483646 - 2*leveOfRecursion;
                if(possibleScore<foundBeta){
                    actionsToReturn=actions1;
                    foundBeta=possibleScore;
                    message="on gagne";
                }
            }else if(winner.getPlayerId() != this.getPlayerId() && this.getPlayerId()!=state.getCurrentPlayerId()){ // on perd et c'était à l'adversaire de jouer
                int possibleScore = - 2147483646 + 2*leveOfRecursion;
                if(possibleScore<foundBeta){
                    actionsToReturn=actions1;
                    foundBeta=possibleScore;
                    message="on perd";
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
            message="egalité";
        }
        ret = new checkGameResultReturnObject(new MyArrayList<>(actionsToReturn),foundAlpha,foundBeta,message);
        return ret;
    }
}
